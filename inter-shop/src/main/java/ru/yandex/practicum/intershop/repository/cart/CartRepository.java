package ru.yandex.practicum.intershop.repository.cart;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.intershop.repository.item.ItemWithQuantityProjection;
import ru.yandex.practicum.intershop.model.CartItem;

import java.util.Collection;

@Repository
public interface CartRepository extends ReactiveCrudRepository<CartItem, Long> {

    @Query("INSERT INTO cart_item (user_id, item_id, quantity) VALUES (:userId, :itemId, :quantity)")
    Mono<Void> insert(@Param("userId") Long userId, @Param("itemId") Long itemId, @Param("quantity") Integer quantity);

    @Query("""
        SELECT i.id, i.title, i.description, i.price, i.has_image, c.quantity
            FROM cart_item c
            LEFT JOIN item i on c.item_id = i.id
            WHERE c.user_id = :userId
    """)
    Flux<ItemWithQuantityProjection> getCart(Long userId);

    Mono<CartItem> findByUserIdAndItemId(Long userId, Long itemId);

    Mono<Void> deleteByUserId(Long userId);

    Flux<CartItem> findAllByUserIdAndItemIdIn(Long userId, Collection<Long> itemId);
}
