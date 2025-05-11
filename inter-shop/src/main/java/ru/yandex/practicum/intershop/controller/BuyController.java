package ru.yandex.practicum.intershop.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.intershop.service.order.OrderService;
import ru.yandex.practicum.intershop.service.user.AppUserDetails;

@Controller
@RequestMapping("/buy")
public class BuyController {

    private final OrderService orderService;

    @Autowired
    public BuyController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping()
    public Mono<String> createOrder(@AuthenticationPrincipal AppUserDetails appUserDetails) {
        return orderService.createOrder(appUserDetails.getId())
                .map(order -> "redirect:/orders/" + order + "?newOrder=true")
                .onErrorResume(Mono::error);
    }

}
