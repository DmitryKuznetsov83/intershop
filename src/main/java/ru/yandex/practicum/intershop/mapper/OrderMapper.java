package ru.yandex.practicum.intershop.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import ru.yandex.practicum.intershop.dto.OrderDto;
import ru.yandex.practicum.intershop.model.Order;

@Mapper
public interface OrderMapper {

    OrderMapper INSTANCE = Mappers.getMapper(OrderMapper.class);

    @Mapping(target = "items", expression = "java(orderModel.getItems().stream().map(ItemMapper.INSTANCE::mapToItemDto).toList())")
    OrderDto mapToOrderDto(Order orderModel);

}
