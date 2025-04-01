package ru.yandex.practicum.intershop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.intershop.model.Item;
import ru.yandex.practicum.intershop.model.Order;

@Repository
public interface OrderRepositoryJpa extends JpaRepository<Order, Long>  {
}
