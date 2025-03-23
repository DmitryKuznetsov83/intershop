package ru.yandex.practicum.intershop.service.order;

import ru.yandex.practicum.intershop.dto.OrderFullDto;
import ru.yandex.practicum.intershop.dto.OrderShortDto;

import java.util.List;
import java.util.Optional;

public interface OrderService {

    List<OrderShortDto> getOrders();

    OrderFullDto getOrderById(Long id);

    Long createOrder();

}
