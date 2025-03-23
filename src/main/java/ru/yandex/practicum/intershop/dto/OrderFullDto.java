package ru.yandex.practicum.intershop.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderFullDto {

    private Long id;
    private ZonedDateTime created;
    private List<ItemDto> items;

    public Integer getTotalSum() {
        return items.stream().mapToInt(ItemDto::getSum).sum();
    }
}
