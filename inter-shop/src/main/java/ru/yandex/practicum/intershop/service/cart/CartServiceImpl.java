package ru.yandex.practicum.intershop.service.cart;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.client.InterPaymentClient;
import ru.yandex.practicum.intershop.dto.ItemDto;
import ru.yandex.practicum.intershop.emun.CartAction;
import ru.yandex.practicum.intershop.mapper.ItemMapper;
import ru.yandex.practicum.intershop.model.CartItem;
import ru.yandex.practicum.intershop.repository.cart.CartRepository;
import ru.yandex.practicum.intershop.repository.item.ItemRepository;
import ru.yandex.practicum.intershop.repository.user.AppUserRepository;

import java.security.Principal;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class CartServiceImpl implements CartService {

    private final ItemRepository itemRepository;
    private final CartRepository cartRepository;
    private final AppUserRepository appUserRepository;
    private final InterPaymentClient interPaymentClient;

    @Autowired
    public CartServiceImpl(ItemRepository itemRepository, CartRepository cartRepository, AppUserRepository appUserRepository, InterPaymentClient interPaymentClient) {
        this.itemRepository = itemRepository;
        this.cartRepository = cartRepository;
        this.appUserRepository = appUserRepository;
        this.interPaymentClient = interPaymentClient;
    }

    @Override
    @PreAuthorize("#userId == principal.id or hasRole('ADMIN')")
    public Mono<Void> changeCart(Long userId, Long itemId, CartAction operation) {
        return appUserRepository.findById(userId)
                .switchIfEmpty(Mono.error(new NoSuchElementException("Пользователь с id " + userId + " не найден")))
                .then(itemRepository.findById(itemId))
                .switchIfEmpty(Mono.error(new NoSuchElementException("Товар с id " + itemId + " не найден")))
                .then(cartRepository.findByUserIdAndItemId(userId, itemId))
                .defaultIfEmpty(new CartItem(userId, itemId, 0))
                .flatMap(cartItem -> {
                    cartItem.setUserId(userId);
                    if (operation == CartAction.PLUS && cartItem.getQuantity() == 0) {
                        cartItem.setQuantity(1);
                        return cartRepository.insert(cartItem.getUserId(), cartItem.getItemId(), cartItem.getQuantity());
                    } else if (operation == CartAction.PLUS && cartItem.getQuantity() > 0) {
                        cartItem.setQuantity(cartItem.getQuantity() + 1);
                        return cartRepository.save(cartItem);
                    } else if (operation == CartAction.MINUS && cartItem.getQuantity() > 1) {
                        cartItem.setQuantity(cartItem.getQuantity() - 1);
                        return cartRepository.save(cartItem);
                    } else if (operation == CartAction.MINUS && cartItem.getQuantity() == 1) {
                        return cartRepository.deleteById(cartItem.getId());
                    } else if (operation == CartAction.DELETE) {
                        return cartRepository.deleteById(cartItem.getId());
                    } else {
                        return Mono.empty();
                    }
                })
                .then();
    }

    @Override
    @PreAuthorize("#userId == principal.id or hasRole('ADMIN')")
    public Flux<ItemDto> getCartItems(Long userId) {
        return cartRepository.getCart(userId)
                .map(ItemMapper.INSTANCE::mapToItemDto)
                .sort(Comparator.comparing(ItemDto::getId));
    }

    @Override
    @PreAuthorize("#userId == principal.id or hasRole('ADMIN')")
    public Mono<Integer> getBalance(Long userId) {
        Mono<String> userLogin = ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .map(Principal::getName);
        return interPaymentClient.getBalance(userLogin);
    }

    @Override
    @PreAuthorize("#userId == principal.id or hasRole('ADMIN')")
    public Mono<Void> clearCart(Long userId) {
        return cartRepository.deleteByUserId(userId);
    }

    @Override
    @PreAuthorize("#userId == principal.id or hasRole('ADMIN')")
    public Mono<CartState> getCartState(Long userId) {

        Flux<ItemDto> cartItems = getCartItems(userId);
        Mono<List<ItemDto>> itemListMono = cartItems.collectList();
        Mono<Integer> totalMono = cartItems
                .map(item -> item.getPrice() * item.getQuantity())
                .reduce(0, Integer::sum);

        Mono<Optional<Integer>> balanceMono = getBalance(userId)
                .map(Optional::of)
                .onErrorResume(e -> Mono.just(Optional.empty()));

        return Mono.zip(itemListMono, totalMono, balanceMono)
                .map(tuple -> {
                    List<ItemDto> items = tuple.getT1();
                    int cartSum = tuple.getT2();
                    Optional<Integer> balanceOpt = tuple.getT3();

                    CartState dto = new CartState();
                    dto.setItems(items);
                    dto.setEmpty(items.isEmpty());
                    dto.setCartSum(cartSum);
                    dto.setPaymentServiceAvailable(balanceOpt.isPresent());
                    dto.setBalance(balanceOpt.orElse(0));
                    dto.setPurchaseIsPossible(balanceOpt.map(balance -> balance >= cartSum).orElse(false));

                    return dto;
                });
    }

}
