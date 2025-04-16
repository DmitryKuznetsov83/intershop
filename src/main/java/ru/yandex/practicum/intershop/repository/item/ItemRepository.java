package ru.yandex.practicum.intershop.repository.item;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.intershop.model.Item;
import ru.yandex.practicum.intershop.repository.order.OrderItemProjection;


@Repository
public interface ItemRepository extends ReactiveCrudRepository<Item, Long>, ItemRepositoryCustom {

    @Query("""
        SELECT COUNT(*)
        FROM item i
        WHERE lower(i.title) LIKE lower(:searchPattern) OR lower(i.description) LIKE lower(:searchPattern)
    """)
    Mono<Long> countWithSearch(String searchPattern);

}
