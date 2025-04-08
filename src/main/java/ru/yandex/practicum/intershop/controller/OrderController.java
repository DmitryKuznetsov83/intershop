package ru.yandex.practicum.intershop.controller;

import jakarta.validation.constraints.Positive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.intershop.dto.OrderDto;
import ru.yandex.practicum.intershop.service.order.OrderService;

@Controller
@RequestMapping(path = "/orders")
@Validated
public class OrderController {

    private final OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public Mono<String> getOrders(Model model) {
        Flux<OrderDto> orders = orderService.getOrders();
        model.addAttribute("orders", orders);
        return Mono.just("orders");
    }

    @GetMapping("/{id}")
    public Mono<String> getOrder(Model model,
                                 @PathVariable @Positive Long id,
                                 @RequestParam(defaultValue = "false") boolean newOrder) {
        Mono<OrderDto> order = orderService.getOrderById(id);
        model.addAttribute("newOrder", newOrder);
        model.addAttribute("order", order);
        return Mono.just("order");
    }

}
