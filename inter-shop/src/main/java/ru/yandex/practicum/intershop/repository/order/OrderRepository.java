package ru.yandex.practicum.intershop.repository.order;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import ru.yandex.practicum.intershop.model.Order;

@Repository
public interface OrderRepository extends ReactiveCrudRepository<Order, Long> {

    @Query("""
        SELECT oi.id, oi.order_id, oi.item_id, i.title, i.description, i.price, i.has_image, oi.quantity
        FROM order_item oi
            LEFT JOIN item i on oi.item_id = i.id
        WHERE oi.order_id = :orderId
    """)
    Flux<OrderItemProjection> findAllOrderItemsByOrderId(Long orderId);

    @Query("""
        SELECT oi.id, oi.order_id, oi.item_id, i.title, i.description, i.price, i.has_image, oi.quantity
        FROM order_item oi
            LEFT JOIN item i on oi.item_id = i.id
        ORDER BY oi.order_id, oi.id
    """)
    Flux<OrderItemProjection> findAllOrderItems();

}