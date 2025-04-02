package ru.yandex.practicum.intershop.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderDto {

    private Long id;
    private List<ItemDto> items;

    public Integer getTotalSum() {
        return items.stream().mapToInt(ItemDto::getSum).sum();
    }
}
