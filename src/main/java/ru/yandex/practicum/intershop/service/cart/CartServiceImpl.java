package ru.yandex.practicum.intershop.service.cart;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.intershop.dto.ItemDto;
import ru.yandex.practicum.intershop.emun.CartAction;
import ru.yandex.practicum.intershop.mapper.ItemMapper;
import ru.yandex.practicum.intershop.model.CartItem;
import ru.yandex.practicum.intershop.repository.cart.CartRepository;
import ru.yandex.practicum.intershop.repository.item.ItemRepository;

import java.util.Comparator;
import java.util.NoSuchElementException;

@Service
public class CartServiceImpl implements CartService {

    private final ItemRepository itemRepository;
    private final CartRepository cartRepository;

    @Autowired
    public CartServiceImpl(ItemRepository itemRepository, CartRepository cartRepository) {
        this.itemRepository = itemRepository;
        this.cartRepository = cartRepository;
    }

    @Override
    public Mono<Void> changeCart(Long itemId, CartAction operation) {
        return itemRepository.findById(itemId)
                .switchIfEmpty(Mono.error(new NoSuchElementException("Товар с id " + itemId + " не найден")))
                .then(cartRepository.findById(itemId))
                .defaultIfEmpty(new CartItem(itemId, 0))
                .flatMap(cartItem -> {
                    System.out.println("qwe"+cartItem.getQuantity());
                    if (operation == CartAction.PLUS && cartItem.getQuantity() == 0) {
                        cartItem.setQuantity(1);
                        System.out.println("insert");
                        return cartRepository.insert(cartItem.getItemId(), cartItem.getQuantity());
                    } else if (operation == CartAction.PLUS && cartItem.getQuantity() > 0) {
                        cartItem.setQuantity(cartItem.getQuantity() + 1);
                        System.out.println("save");
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
    @Transactional(readOnly = true)
    public Flux<ItemDto> getCartItems() {
        return cartRepository.getCart()
                .map(ItemMapper.INSTANCE::mapToItemDto)
                .sort(Comparator.comparing(ItemDto::getId));
    }
}
