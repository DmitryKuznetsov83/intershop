package ru.yandex.practicum.intershop.mapper;

import ru.yandex.practicum.intershop.dto.OrderFullDto;
import ru.yandex.practicum.intershop.dto.OrderShortDto;
import ru.yandex.practicum.intershop.model.Order;

public class OrderMapper {

    public static OrderFullDto mapToOrderFullDto(Order orderModel) {
        OrderFullDto orderFullDto = new OrderFullDto();
        orderFullDto.setId(orderModel.getId());
        orderFullDto.setItems(orderModel.getItems().stream().map(ItemMapper::mapToItemDto).toList());
        return orderFullDto;
    }

    public static OrderShortDto mapToOrderShortDto(Order orderModel) {
        OrderShortDto orderShortDto = new OrderShortDto();
        orderShortDto.setId(orderModel.getId());
        orderShortDto.setItems(orderModel.getItems().stream().map(ItemMapper::mapToItemDto).toList());
        return orderShortDto;
    }

}
