package ru.yandex.practicum.intershop.controller;

import jakarta.validation.constraints.Positive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.intershop.dto.ItemDto;
import ru.yandex.practicum.intershop.emun.CartAction;
import ru.yandex.practicum.intershop.service.cart.CartService;

import java.util.List;

@Controller
@RequestMapping("/cart/items")
@Validated
public class CartController {

    private final CartService cartService;

    @Autowired
    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping
    public String getCart(Model model) {
        List<ItemDto> cartItems = cartService.getCartItems();
        model.addAttribute("items", cartItems);
        model.addAttribute("total", cartItems.stream().map(ItemDto::getPrice).reduce(0, Integer::sum));
        model.addAttribute("empty", cartItems.isEmpty());
        return "cart";
    }

    @PostMapping("/{itemId}")
    public String changeCart(@PathVariable @Positive Long itemId,
                             @RequestParam CartAction action) {
        cartService.changeCart(itemId, action);
        return "redirect:/cart/items";
    }

}
