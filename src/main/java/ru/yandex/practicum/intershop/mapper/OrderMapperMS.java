package ru.yandex.practicum.intershop.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import ru.yandex.practicum.intershop.dto.OrderFullDto;
import ru.yandex.practicum.intershop.dto.OrderShortDto;
import ru.yandex.practicum.intershop.model.Order;

@Mapper
public interface OrderMapperMS {

    OrderMapperMS INSTANCE = Mappers.getMapper(OrderMapperMS.class);

    @Mapping(target = "items", expression = "java(orderModel.getItems().stream().map(ItemMapperMS.INSTANCE::mapToItemDto).toList())")
    OrderFullDto mapToOrderFullDto(Order orderModel);

    @Mapping(target = "items", expression = "java(orderModel.getItems().stream().map(ItemMapperMS.INSTANCE::mapToItemDto).toList())")
    OrderShortDto mapToOrderShortDto(Order orderModel);

}
