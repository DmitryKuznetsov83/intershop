package ru.yandex.practicum.intershop.service.order;

import ru.yandex.practicum.intershop.dto.OrderDto;

import java.util.List;

public interface OrderService {

    List<OrderDto> getOrders();

    OrderDto getOrderById(Long id);

    Long createOrder();

}
