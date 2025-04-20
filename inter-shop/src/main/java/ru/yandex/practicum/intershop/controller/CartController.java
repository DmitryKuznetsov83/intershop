package ru.yandex.practicum.intershop.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.intershop.dto.ItemDto;
import ru.yandex.practicum.intershop.service.cart.CartService;

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
    public Mono<String> getCart(Model model) {
        Flux<ItemDto> cartItems = cartService.getCartItems();
        model.addAttribute("items", cartItems);

        model.addAttribute("empty", cartItems.hasElements().map(hasElements -> !hasElements));
        model.addAttribute("total", cartItems
                .map(item -> item.getPrice() * item.getQuantity())
                .reduce(0, Integer::sum));

        return Mono.just("cart");
    }

    @PostMapping("/{itemId}")
    public Mono<String> changeCart(@PathVariable @Positive Long itemId,
                                   @Valid @ModelAttribute ChangeCart changeCart) {
        return cartService.changeCart(itemId, changeCart.getAction())
                .then(Mono.just("redirect:/cart/items"));
    }

}
