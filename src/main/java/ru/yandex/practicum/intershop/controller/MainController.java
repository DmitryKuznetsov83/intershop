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
@RequestMapping(path = "/main/items")
@Validated
public class MainController {

    private final ItemService itemService;
    private final IntershopConfiguration intershopConfiguration;

    @Autowired
    public MainController(ItemService itemService, IntershopConfiguration intershopConfiguration) {
        this.itemService = itemService;
        this.intershopConfiguration = intershopConfiguration;
    }

    @GetMapping
    public String getItems(Model model,
                           @RequestParam(required = false) String search,
                           @RequestParam(required = false) Sorting sorting,
                           @RequestParam(required = false) @Positive Integer pageSize,
                           @RequestParam(defaultValue = "1") @Positive Integer pageNumber) {
        if (sorting == null) {
            sorting = intershopConfiguration.getSortingByDefault();
        }
        if (pageSize == null) {
            pageSize = intershopConfiguration.getPagingSizeByDefault();
        }

        Sort sort = switch (sorting) {
            case ALPHA -> Sort.by("title");
            case PRICE -> Sort.by("price");
            default -> Sort.unsorted();
        };
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize, sort);
        PageDto<ItemFullDto> page = itemService.getItems(search, pageable);
        List<List<ItemFullDto>> partitionedItems = ListUtils.partition(page.content(), intershopConfiguration.getCellInRow());
        PagingDto pagingDto = new PagingDto(pageSize, pageNumber, page.hasPrevious(), page.hasNext());

        model.addAttribute("pageSizeOptions", intershopConfiguration.getPagingSizeOptions());
        model.addAttribute("search", search);
        model.addAttribute("sorting", sorting.name());
        model.addAttribute("paging", pagingDto);
        model.addAttribute("items", partitionedItems);

        return "main";
    }

}