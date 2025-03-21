package ru.yandex.practicum.intershop.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.yandex.practicum.intershop.service.initial_loader.InitialLoaderService;
import ru.yandex.practicum.intershop.service.initial_loader.InitialLoaderServiceImpl;
import ru.yandex.practicum.intershop.service.item.ItemService;

@Controller
@RequestMapping(path = "/admin")
public class AdminController {

    private final ItemService itemService;
    private final InitialLoaderService initialLoaderService;

    @Autowired
    public AdminController(ItemService itemService, InitialLoaderServiceImpl initialLoaderServiceImpl, InitialLoaderService initialLoaderService) {
        this.itemService = itemService;
        this.initialLoaderService = initialLoaderService;
    }

    @GetMapping("/initial-loader")
    public String initialLoading(Model model) {
        model.addAttribute("dbCount", itemService.getItemCount());
        model.addAttribute("ilCount", initialLoaderService.getItemCount());
        return "initial-loader";
    }

    @PostMapping("/initial-loader")
    public String initialLoadingStart() {
        initialLoaderService.load();
        return "redirect:/admin/initial-loader";
    }

}
