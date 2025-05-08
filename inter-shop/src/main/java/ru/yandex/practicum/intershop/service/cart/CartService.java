package ru.yandex.practicum.intershop.service.cart;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.intershop.dto.ItemDto;
import ru.yandex.practicum.intershop.emun.CartAction;

public interface CartService {

    Mono<Void> changeCart(Long userId, Long itemId, CartAction operation);

    Flux<ItemDto> getCartItems(Long userId);

    Mono<Integer> getBalance(Long userId);

    Mono<Void> clearCart(Long userId);

    Mono<CartState> getCartState(Long userId);

}
