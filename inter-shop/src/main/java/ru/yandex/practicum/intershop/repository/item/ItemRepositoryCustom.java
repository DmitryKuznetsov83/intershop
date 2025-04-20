package ru.yandex.practicum.intershop.repository.item;

import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.intershop.model.Item;

public interface ItemRepositoryCustom {

    Flux<Item> findAll(Pageable pageable, String search);

    Mono<byte[]> findImageByItemId(Long itemId);

}
