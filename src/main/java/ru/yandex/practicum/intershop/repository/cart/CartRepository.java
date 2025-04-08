package ru.yandex.practicum.intershop.repository.cart;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.intershop.repository.item.ItemWithQuantityProjection;
import ru.yandex.practicum.intershop.model.CartItem;

@Repository
public interface CartRepository extends ReactiveCrudRepository<CartItem, Long> {

    @Query("INSERT INTO cart_item (item_id, quantity) VALUES (:itemId, :quantity)")
    Mono<Void> insert(@Param("itemId") Long itemId, @Param("quantity") Integer quantity);


    @Query("""
        SELECT i.id, i.title, i.description, i.price, i.has_image, c.quantity
            FROM cart_item c
            LEFT JOIN item i on c.item_id = i.id
    """)
    Flux<ItemWithQuantityProjection> getCart();

}
