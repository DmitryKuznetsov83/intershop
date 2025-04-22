package ru.yandex.practicum.interpayment.server.service;

import reactor.core.publisher.Mono;
import ru.yandex.practicum.dto.BalanceDto;
import ru.yandex.practicum.dto.TransactionDto;

public interface BalanceService {
    Mono<BalanceDto> getBalance();

    Mono<BalanceDto> changeBalance(TransactionDto transactionDto);
}
