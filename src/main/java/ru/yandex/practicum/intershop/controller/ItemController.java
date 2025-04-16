package ru.yandex.practicum.intershop.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.intershop.configuration.IntershopConfiguration;
import ru.yandex.practicum.intershop.dto.ItemDto;
import ru.yandex.practicum.intershop.service.cart.CartService;
import ru.yandex.practicum.intershop.service.item.ItemService;

@Controller
@RequestMapping(path = "/items/{itemId}")
@Validated
public class ItemController {

    private final ItemService itemService;
    private final CartService cartService;

    @Autowired
    public ItemController(ItemService itemService, IntershopConfiguration intershopConfiguration, CartService cartService) {
        this.itemService = itemService;
        this.cartService = cartService;
    }

    @GetMapping()
    public Mono<String> getItem(Model model, @PathVariable @Positive Long itemId) {
        Mono<ItemDto> item = itemService.getItemById(itemId);
        model.addAttribute("item", item);
        return Mono.just("item");
    }

    @PostMapping()
    public Mono<String> changeCart(@PathVariable @Positive Long itemId,
                                   @Valid @ModelAttribute ChangeCart changeCart) {
        return cartService.changeCart(itemId, changeCart.getAction())
                .then(Mono.just("redirect:/items/" + itemId));
    }

    // IMAGES
    @GetMapping("/image")
    public Mono<ResponseEntity<ByteArrayResource>> getImage(@PathVariable @Positive Long itemId) {
        return itemService.findImageByPostId(itemId)
                .map(image -> ResponseEntity.ok()
                        .contentType(MediaType.IMAGE_JPEG)
                        .body(new ByteArrayResource(image)));
    }

}