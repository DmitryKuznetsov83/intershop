package ru.yandex.practicum.intershop.mapper;

import ru.yandex.practicum.intershop.dto.ItemDto;
import ru.yandex.practicum.intershop.model.Item;
import ru.yandex.practicum.intershop.model.OrderItem;

public class ItemMapper {

    public static ItemDto mapToItemDto(Item itemModel) {
        ItemDto itemDto = new ItemDto();

        itemDto.setId(itemModel.getId());
        itemDto.setTitle(itemModel.getTitle());
        itemDto.setDescription(itemModel.getDescription());
        itemDto.setPrice(itemModel.getPrice());
        itemDto.setHasImage(itemModel.isHasImage());
        itemDto.setQuantity(itemModel.getCartQuantity());

        return itemDto;
    }

    public static ItemDto mapToItemDto(OrderItem orderItem) {

        Item item = orderItem.getItem();

        ItemDto itemDto = new ItemDto();
        itemDto.setId(item.getId());
        itemDto.setTitle(item.getTitle());
        itemDto.setDescription(item.getDescription());
        itemDto.setPrice(item.getPrice());
        itemDto.setHasImage(item.isHasImage());
        itemDto.setQuantity(orderItem.getQuantity());

        return itemDto;
    }

}
