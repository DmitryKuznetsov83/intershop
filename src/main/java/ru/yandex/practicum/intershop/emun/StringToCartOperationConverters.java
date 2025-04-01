package ru.yandex.practicum.intershop.emun;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class StringToCartOperationConverters implements Converter<String, CartAction> {

    @Override
    public CartAction convert(String sorting) {
        try {
            return CartAction.valueOf(sorting.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid type: " + sorting + ". Allowed: " + Arrays.toString(CartAction.values()));
        }
    }

}
