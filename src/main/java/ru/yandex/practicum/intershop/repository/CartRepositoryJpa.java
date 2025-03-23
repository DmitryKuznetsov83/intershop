package ru.yandex.practicum.intershop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.intershop.model.CartItem;
import ru.yandex.practicum.intershop.model.Item;

import java.util.List;

public interface CartRepositoryJpa extends JpaRepository<CartItem, Item> {
}
