package ru.yandex.practicum.intershop.mapper;

import ru.yandex.practicum.intershop.dto.ItemFullDto;
import ru.yandex.practicum.intershop.dto.ItemShortDto;
import ru.yandex.practicum.intershop.model.Item;

import java.util.List;
import java.util.stream.Collectors;

public class ItemMapper {

    public static ItemFullDto mapToItemFullDto(Item itemModel) {
        return new ItemFullDto(itemModel.getId(),
                itemModel.getTitle(),
                itemModel.getDescription(),
                itemModel.getPrice(),
                itemModel.isHasImage());
    }

    public static List<ItemFullDto> mapToItemFullDtoList(List<Item> itemsModel) {
        return itemsModel.stream().map(ItemMapper::mapToItemFullDto).collect(Collectors.toList());
    }
}
