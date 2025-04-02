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
import ru.yandex.practicum.intershop.dto.OrderDto;
import ru.yandex.practicum.intershop.service.order.OrderService;

import java.util.List;

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
    public String getOrders(Model model) {
        List<OrderDto> orders = orderService.getOrders();
        model.addAttribute("orders", orders);
        return "orders";
    }

    @GetMapping("/{id}")
    public String getOrder(Model model,
                           @PathVariable @Positive Long id,
                           @RequestParam(defaultValue = "false") boolean newOrder) {
        OrderDto order = orderService.getOrderById(id);
        model.addAttribute("newOrder", newOrder);
        model.addAttribute("order", order);
        return "order";
    }

}
