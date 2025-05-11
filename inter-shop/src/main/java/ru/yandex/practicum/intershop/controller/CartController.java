package ru.yandex.practicum.intershop.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.intershop.service.cart.CartService;
import ru.yandex.practicum.intershop.service.cart.CartState;
import ru.yandex.practicum.intershop.service.user.AppUserDetails;

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
    public Mono<String> getCart(@AuthenticationPrincipal AppUserDetails appUserDetails,
                                Model model) {
        Mono<CartState> cartState = cartService.getCartState(appUserDetails.getId());

        model.addAttribute("items",                     cartState.map(CartState::getItems));
        model.addAttribute("empty",                     cartState.map(CartState::isEmpty));
        model.addAttribute("total",                     cartState.map(CartState::getCartSum));
        model.addAttribute("paymentServiceAvailable",   cartState.map(CartState::isPaymentServiceAvailable));
        model.addAttribute("balance",                   cartState.map(CartState::getBalance));
        model.addAttribute("purchaseIsPossible",        cartState.map(CartState::isPurchaseIsPossible));

        return Mono.just("cart");

    }

    @PostMapping("/{itemId}")
    public Mono<String> changeCart(@AuthenticationPrincipal AppUserDetails appUserDetails,
                                   @PathVariable @Positive Long itemId,
                                   @Valid @ModelAttribute ChangeCart changeCart) {
        return cartService.changeCart(appUserDetails.getId(), itemId, changeCart.getAction())
                .then(Mono.just("redirect:/cart/items"));
    }

}
