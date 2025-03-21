package ru.yandex.practicum.intershop.controller;

import jakarta.validation.constraints.Positive;
import org.apache.commons.collections4.ListUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.yandex.practicum.intershop.configuration.IntershopConfiguration;
import ru.yandex.practicum.intershop.dto.ItemFullDto;
import ru.yandex.practicum.intershop.dto.PageDto;
import ru.yandex.practicum.intershop.dto.PagingDto;
import ru.yandex.practicum.intershop.emun.Sorting;
import ru.yandex.practicum.intershop.service.item.ItemService;

import java.util.List;

@Controller
@RequestMapping(path = "/items/{itemId}")
@Validated
public class ItemController {

    private final ItemService itemService;

    @Autowired
    public ItemController(ItemService itemService, IntershopConfiguration intershopConfiguration) {
        this.itemService = itemService;
    }

    @GetMapping()
    public String getItem(Model model, @PathVariable Long itemId) {
        ItemFullDto item = itemService.getItemById(itemId);
        model.addAttribute("item", item);
        return "item";
    }

}