package ru.yandex.practicum.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor

public class TransactionDto {
    @NotNull
    UUID uuid;

    @NotNull
    TransactionOperation operation;

    @NotNull
    @Positive
    Integer amount;
}
