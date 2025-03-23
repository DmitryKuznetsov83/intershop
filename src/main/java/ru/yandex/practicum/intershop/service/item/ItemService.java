package ru.yandex.practicum.intershop.service.item;

import org.springframework.data.domain.Pageable;
import ru.yandex.practicum.intershop.dto.ItemDto;
import ru.yandex.practicum.intershop.dto.PageDto;

import java.util.Optional;

public interface ItemService {

    PageDto<ItemDto> getItems(String search, Pageable pageable);

    ItemDto getItemById(Long id);

    Long getItemCount();

    Optional<byte[]> findImageByPostId(long itemId);
}
