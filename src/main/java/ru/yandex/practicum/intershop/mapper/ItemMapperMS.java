package ru.yandex.practicum.intershop.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import ru.yandex.practicum.intershop.dto.ItemDto;
import ru.yandex.practicum.intershop.model.Item;
import ru.yandex.practicum.intershop.model.OrderItem;

@Mapper
public interface ItemMapperMS {

    ItemMapperMS INSTANCE = Mappers.getMapper(ItemMapperMS.class);

    @Mapping(target = "quantity", expression = "java(itemModel.getCartQuantity())")
    ItemDto mapToItemDto(Item itemModel);

    @Mapping(target="id", source = "orderItem.item.id")
    @Mapping(target="title", source = "orderItem.item.title")
    @Mapping(target="description", source = "orderItem.item.description")
    @Mapping(target="price", source = "orderItem.item.price")
    @Mapping(target="hasImage", source = "orderItem.item.hasImage")
    @Mapping(target="quantity", source = "orderItem.quantity")
    ItemDto mapToItemDto(OrderItem orderItem);

}
