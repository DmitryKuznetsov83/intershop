package ru.yandex.practicum.dto.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
public class BalanceDto {
    private int balance;
    private String date;

    public BalanceDto(int balance, LocalDateTime date) {
        this.balance = balance;
        this.date = DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(date);
    }
}
