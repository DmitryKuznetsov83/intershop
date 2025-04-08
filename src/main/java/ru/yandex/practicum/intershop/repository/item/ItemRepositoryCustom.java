package ru.yandex.practicum.intershop.repository.item;

import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ItemRepositoryCustom {

    Flux<ItemWithQuantityProjection> findAll(Pageable pageable, String search);

    Mono<byte[]> findImageByItemId(Long itemId);

}
