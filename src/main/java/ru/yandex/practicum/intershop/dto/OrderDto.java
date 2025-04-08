package ru.yandex.practicum.intershop.dto;

import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class OrderDto {

    private Long id;
    private List<ItemDto> items;

    public Integer getTotalSum() {
        return items.stream().mapToInt(ItemDto::getSum).sum();
    }
}
