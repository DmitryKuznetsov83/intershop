package ru.yandex.practicum.intershop.service.item;

import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.intershop.dto.ItemDto;
import ru.yandex.practicum.intershop.dto.PageDto;

public interface ItemService {

    Mono<PageDto<ItemDto>> getItems(Pageable pageable, String search);

    Mono<ItemDto> getItemById(Long id);

    Mono<Long> getItemCount();

    Mono<byte[]> findImageByPostId(long itemId);
}
