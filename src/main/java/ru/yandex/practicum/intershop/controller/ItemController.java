package ru.yandex.practicum.intershop.controller;

import jakarta.validation.constraints.Positive;
import org.apache.commons.collections4.ListUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.intershop.configuration.IntershopConfiguration;
import ru.yandex.practicum.intershop.dto.ItemFullDto;
import ru.yandex.practicum.intershop.dto.PageDto;
import ru.yandex.practicum.intershop.dto.PagingDto;
import ru.yandex.practicum.intershop.emun.CartAction;
import ru.yandex.practicum.intershop.emun.Sorting;
import ru.yandex.practicum.intershop.service.cart.CartService;
import ru.yandex.practicum.intershop.service.item.ItemService;

import java.util.List;
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
    public String getItem(Model model, @PathVariable Long itemId) {
        ItemFullDto item = itemService.getItemById(itemId);
        model.addAttribute("item", item);
        return "item";
    }

    @PostMapping()
    public String changeCart(@PathVariable Long itemId,
                             @RequestParam CartAction action) {
        cartService.changeCart(itemId, action);
        return "redirect:/items/" + itemId;
    }

    // IMAGES
    @GetMapping("/image")
    public ResponseEntity<ByteArrayResource> getImage(@PathVariable long itemId) {
        Optional<byte[]> image = itemService.findImageByPostId(itemId);
        if (image.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        ByteArrayResource resource = new ByteArrayResource(image.get());
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(resource);
    }

}