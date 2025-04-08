package ru.yandex.practicum.intershop.service.cart;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.intershop.dto.ItemDto;
import ru.yandex.practicum.intershop.emun.CartAction;

public interface CartService {
    Mono<Void> changeCart(Long itemId, CartAction operation);

    Flux<ItemDto> getCartItems();
}
