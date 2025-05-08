package ru.yandex.practicum.intershop.repository.order;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import ru.yandex.practicum.intershop.model.Order;

@Repository
public interface OrderRepository extends ReactiveCrudRepository<Order, Long> {

    @Query("""
        SELECT o.user_id, oi.id, oi.order_id, oi.item_id, i.title, i.description, i.price, i.has_image, oi.quantity
        FROM order_item oi
            LEFT JOIN item i on oi.item_id = i.id
            JOIN orders o ON oi.order_id = o.id
        WHERE oi.order_id = :orderId
    """)
    Flux<OrderItemProjection> findAllOrderItemsByOrderId(Long orderId);

    @Query("""
        SELECT o.user_id, oi.id, oi.order_id, oi.item_id, i.title, i.description, i.price, i.has_image, oi.quantity
        FROM
            order_item oi
        JOIN
            orders o ON oi.order_id = o.id
        LEFT JOIN
            item i ON oi.item_id = i.id
        WHERE
            o.user_id = :userId
        ORDER BY
            oi.order_id, oi.id
    """)
    Flux<OrderItemProjection> findAllOrderItems(Long userId);

}