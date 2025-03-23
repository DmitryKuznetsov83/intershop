package ru.yandex.practicum.intershop.service.cart;

import ru.yandex.practicum.intershop.dto.ItemFullDto;
import ru.yandex.practicum.intershop.emun.CartAction;

import java.util.List;

public interface CartService {
    void changeCart(Long itemId, CartAction operation);

    List<ItemFullDto> getCartItems();
}
