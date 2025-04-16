package ru.yandex.practicum.intershop.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import reactor.core.publisher.Mono;

@Controller
@RequestMapping(path = "/")
public class RootController {

    @GetMapping
    public Mono<String> root() {
        return Mono.just("redirect:/main/items");
    }

    @GetMapping("/main")
    public Mono<String> rootMain() {
        return Mono.just("redirect:/main/items");
    }
}
