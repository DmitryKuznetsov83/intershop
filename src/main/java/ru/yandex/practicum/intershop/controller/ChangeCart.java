package ru.yandex.practicum.intershop.controller;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.intershop.emun.CartAction;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChangeCart {
    CartAction action;
}
