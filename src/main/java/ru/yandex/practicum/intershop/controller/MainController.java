package ru.yandex.practicum.intershop.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.apache.commons.collections4.ListUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.intershop.configuration.IntershopConfiguration;
import ru.yandex.practicum.intershop.dto.ItemDto;
import ru.yandex.practicum.intershop.dto.PageDto;
import ru.yandex.practicum.intershop.dto.PagingDto;
import ru.yandex.practicum.intershop.emun.Sorting;
import ru.yandex.practicum.intershop.service.cart.CartService;
import ru.yandex.practicum.intershop.service.item.ItemService;

import java.util.List;

@Controller
@RequestMapping(path = "/main/items")
@Validated
public class MainController {

    private final ItemService itemService;
    private final CartService cartService;
    private final IntershopConfiguration intershopConfiguration;

    @Autowired
    public MainController(ItemService itemService, CartService cartService, IntershopConfiguration intershopConfiguration) {
        this.itemService = itemService;
        this.cartService = cartService;
        this.intershopConfiguration = intershopConfiguration;
    }

    @GetMapping
    public Mono<String> getItems(Model model,
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
            default -> Sort.by("id");
        };
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize, sort);

        Mono<PageDto<ItemDto>> pageDto = itemService.getItems(pageable, search);

        Mono<List<List<ItemDto>>> partitionedItems = pageDto.map(p -> ListUtils.partition(p.content(), intershopConfiguration.getCellInRow()));
        Integer finalPageSize = pageSize;
        Mono<PagingDto> pagingDto = pageDto.map(p -> new PagingDto(finalPageSize, pageNumber, p.hasPrevious(), p.hasNext()));

        model.addAttribute("pageSizeOptions", intershopConfiguration.getPagingSizeOptions());
        model.addAttribute("search", search);
        model.addAttribute("sorting", sorting.name());
        model.addAttribute("paging", pagingDto);
        model.addAttribute("items", partitionedItems);

        return Mono.just("main");
    }

    @PostMapping("/{itemId}")
    public Mono<String> changeCart(@PathVariable @Positive Long itemId,
                                   @Valid @ModelAttribute ChangeCart changeCart) {
        return cartService.changeCart(itemId, changeCart.getAction())
                .then(Mono.just("redirect:/main/items"));
    }

}