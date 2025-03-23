package ru.yandex.practicum.intershop.emun;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class StringToSortingConverter implements Converter<String, Sorting> {

    @Override
    public Sorting convert(String sorting) {
        try {
            return Sorting.valueOf(sorting.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid type: " + sorting + ". Allowed: " + Arrays.toString(Sorting.values()));
        }
    }

}
