package ru.yandex.practicum.intershop.service.cart;

import org.springframework.beans.factory.annotation.Autowired;
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

import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class CartServiceImpl implements CartService {

    private final ItemRepository itemRepository;
    private final CartRepository cartRepository;
    private final InterPaymentClient interPaymentClient;

    @Autowired
    public CartServiceImpl(ItemRepository itemRepository, CartRepository cartRepository, InterPaymentClient interPaymentClient) {
        this.itemRepository = itemRepository;
        this.cartRepository = cartRepository;
        this.interPaymentClient = interPaymentClient;
    }

    @Override
    public Mono<Void> changeCart(Long itemId, CartAction operation) {
        return itemRepository.findById(itemId)
                .switchIfEmpty(Mono.error(new NoSuchElementException("Товар с id " + itemId + " не найден")))
                .then(cartRepository.findById(itemId))
                .defaultIfEmpty(new CartItem(itemId, 0))
                .flatMap(cartItem -> {
                    if (operation == CartAction.PLUS && cartItem.getQuantity() == 0) {
                        cartItem.setQuantity(1);
                        return cartRepository.insert(cartItem.getItemId(), cartItem.getQuantity());
                    } else if (operation == CartAction.PLUS && cartItem.getQuantity() > 0) {
                        cartItem.setQuantity(cartItem.getQuantity() + 1);
                        return cartRepository.save(cartItem);
                    } else if (operation == CartAction.MINUS && cartItem.getQuantity() > 1) {
                        cartItem.setQuantity(cartItem.getQuantity() - 1);
                        return cartRepository.save(cartItem);
                    } else if (operation == CartAction.MINUS && cartItem.getQuantity() == 1) {
                        return cartRepository.deleteById(cartItem.getItemId());
                    } else if (operation == CartAction.DELETE) {
                        return cartRepository.deleteById(cartItem.getItemId());
                    } else {
                        return Mono.empty();
                    }
                })
                .then();
    }

    @Override
    public Flux<ItemDto> getCartItems() {
        return cartRepository.getCart()
                .map(ItemMapper.INSTANCE::mapToItemDto)
                .sort(Comparator.comparing(ItemDto::getId));
    }

    @Override
    public Mono<Integer> getBalance() {
        return interPaymentClient.getBalance();
    }

    @Override
    public Mono<Void> clearCart() {
        return cartRepository.deleteAll();
    }

    @Override
    public Mono<CartState> getCartState() {

        Flux<ItemDto> cartItems = getCartItems();
        Mono<List<ItemDto>> itemListMono = cartItems.collectList();
        Mono<Integer> totalMono = cartItems
                .map(item -> item.getPrice() * item.getQuantity())
                .reduce(0, Integer::sum);

        Mono<Optional<Integer>> balanceMono = getBalance()
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
