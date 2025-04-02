package ru.yandex.practicum.intershop.service.cart;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.intershop.dto.ItemDto;
import ru.yandex.practicum.intershop.emun.CartAction;
import ru.yandex.practicum.intershop.mapper.ItemMapper;
import ru.yandex.practicum.intershop.model.CartItem;
import ru.yandex.practicum.intershop.model.Item;
import ru.yandex.practicum.intershop.repository.CartRepositoryJpa;
import ru.yandex.practicum.intershop.repository.ItemRepositoryJpa;

import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class CartServiceImpl implements CartService {

    private final ItemRepositoryJpa itemRepositoryJpa;
    private final CartRepositoryJpa cartRepositoryJpa;

    @Autowired
    public CartServiceImpl(ItemRepositoryJpa itemRepositoryJpa, CartRepositoryJpa cartRepositoryJpa) {
        this.itemRepositoryJpa = itemRepositoryJpa;
        this.cartRepositoryJpa = cartRepositoryJpa;
    }


    @Override
    @Transactional
    public void changeCart(Long itemId, CartAction operation) {
        Item item = itemRepositoryJpa.findById(itemId)
                .orElseThrow(() -> new NoSuchElementException("Товар с id " + itemId + " не найден"));

        CartItem cartItem = item.getCartItem();
        if (operation == CartAction.PLUS && cartItem == null)  {
            cartItem = new CartItem();
            cartItem.setQuantity(1);
            cartItem.setItem(item);
            item.setCartItem(cartItem);
        } else if (operation == CartAction.PLUS && cartItem != null) {
            cartItem.setQuantity(cartItem.getQuantity() + 1);
        } else if (operation == CartAction.MINUS && cartItem != null && cartItem.getQuantity() > 1) {
            cartItem.setQuantity(cartItem.getQuantity() - 1);
        } else if (operation == CartAction.MINUS && cartItem != null && cartItem.getQuantity() == 1) {
            item.setCartItem(null);
        } else if (operation == CartAction.DELETE) {
            item.setCartItem(null);
        }

        itemRepositoryJpa.save(item);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemDto> getCartItems() {
        return cartRepositoryJpa.findAll()
                .stream()
                .map(CartItem::getItem)
                .map(ItemMapper.INSTANCE::mapToItemDto)
                .sorted(Comparator.comparingLong(ItemDto::getId))
                .toList();
    }
}
