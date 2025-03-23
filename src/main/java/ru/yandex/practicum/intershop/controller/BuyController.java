package ru.yandex.practicum.intershop.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.yandex.practicum.intershop.service.order.OrderService;

@Controller
@RequestMapping("/buy")
public class BuyController {

    private final OrderService orderService;

    @Autowired
    public BuyController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping()
    public String changeCart() {
        Long orderId = orderService.createOrder();
        return "redirect:/orders/" + orderId + "?newOrder=true";
    }

}
