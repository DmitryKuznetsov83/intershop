package ru.yandex.practicum.intershop.service.cart;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.intershop.dto.ItemDto;
import ru.yandex.practicum.intershop.model.CartItem;

import java.util.List;

@Data
@NoArgsConstructor
public class CartState {

    private List<ItemDto> items;
    private boolean empty;
    private int cartSum;
    private boolean paymentServiceAvailable;
    private int balance;
    private boolean purchaseIsPossible;

}