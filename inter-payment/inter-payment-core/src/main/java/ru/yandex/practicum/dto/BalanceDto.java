package ru.yandex.practicum.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class BalanceDto {
    private int balance;
    private LocalDateTime date;
}
