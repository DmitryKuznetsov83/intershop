package ru.yandex.practicum.intershop.service.item;

import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.intershop.dto.ItemDto;
import ru.yandex.practicum.intershop.dto.PageDto;

public interface ItemService {

    Mono<PageDto<ItemDto>> getItems(Long userId, Pageable pageable, String search);

    Mono<ItemDto> getItemById(Long userId, Long id);

    Mono<Long> getItemCount(boolean forceCacheRefresh);

    Mono<Long> getItemCount(String search);

    Mono<byte[]> findImageByPostId(long itemId);

}
