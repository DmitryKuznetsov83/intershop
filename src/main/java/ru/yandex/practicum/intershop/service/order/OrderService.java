package ru.yandex.practicum.intershop.service.order;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.intershop.dto.OrderDto;

public interface OrderService {

    Flux<OrderDto> getOrders();

    Mono<OrderDto> getOrderById(Long id);

    Mono<Long> createOrder();

}
