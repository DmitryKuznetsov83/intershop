package ru.yandex.practicum.intershop.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(path = "/")
public class RootController {

    @GetMapping
    public String root() {
        return "redirect:/main/items";
    }

    @GetMapping("/main")
    public String rootMain() {
        return "redirect:/main/items";
    }
}
