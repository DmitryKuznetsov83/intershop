package ru.yandex.practicum.intershop.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.intershop.configuration.IntershopConfiguration;
import ru.yandex.practicum.intershop.dto.ItemDto;
import ru.yandex.practicum.intershop.service.cart.CartService;
import ru.yandex.practicum.intershop.service.item.ItemService;
import ru.yandex.practicum.intershop.service.user.AppUserDetails;

import java.util.Optional;

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
    public Mono<String> getItem(@AuthenticationPrincipal AppUserDetails appUserDetails,
                                Model model,
                                @PathVariable @Positive Long itemId) {
        Long userId = Optional.ofNullable(appUserDetails).map(AppUserDetails::getId).orElse(null);
        Mono<ItemDto> item = itemService.getItemById(userId, itemId);
        model.addAttribute("item", item);
        return Mono.just("item");
    }

    @PostMapping()
    public Mono<String> changeCart(@AuthenticationPrincipal AppUserDetails appUserDetails,
                                   @PathVariable @Positive Long itemId,
                                   @Valid @ModelAttribute ChangeCart changeCart) {
        return cartService.changeCart(appUserDetails.getId(), itemId, changeCart.getAction())
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