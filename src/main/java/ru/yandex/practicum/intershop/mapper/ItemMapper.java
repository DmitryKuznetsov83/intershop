package ru.yandex.practicum.intershop.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import ru.yandex.practicum.intershop.dto.ItemDto;
import ru.yandex.practicum.intershop.repository.item.ItemWithQuantityProjection;
import ru.yandex.practicum.intershop.repository.order.OrderItemProjection;
import ru.yandex.practicum.intershop.model.Item;

@Mapper
public interface ItemMapper {

    ItemMapper INSTANCE = Mappers.getMapper(ItemMapper.class);

    @Mapping(target = "quantity", constant = "0")
    ItemDto mapToItemDto(Item itemModel);

    @Mapping(target = "hasImage", source = "has_image")
    ItemDto mapToItemDto(ItemWithQuantityProjection itemWithQuantityProjection);

    @Mapping(target = "id", source = "orderItemProjection.item_id")
    ItemDto mapToItemDto(OrderItemProjection orderItemProjection);

}
