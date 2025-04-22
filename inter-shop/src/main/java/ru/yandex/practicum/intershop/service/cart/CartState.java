package ru.yandex.practicum.intershop.service.cart;

import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.intershop.dto.ItemDto;

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