package ru.yandex.practicum.intershop.service.item;

import org.springframework.data.domain.Pageable;
import ru.yandex.practicum.intershop.dto.ItemFullDto;
import ru.yandex.practicum.intershop.dto.ItemShortDto;
import ru.yandex.practicum.intershop.dto.PageDto;
import ru.yandex.practicum.intershop.emun.Sorting;

import java.util.List;

public interface ItemService {

    PageDto<ItemFullDto> getItems(String search, Pageable pageable);

    ItemFullDto getItemById(Long id);

    Long getItemCount();

    void saveItem(ItemFullDto itemFullDto);

}
