package ru.yandex.practicum.interpayment.server.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.dto.dto.BalanceDto;
import ru.yandex.practicum.dto.dto.TransactionDto;
import ru.yandex.practicum.dto.TransactionOperation;
import ru.yandex.practicum.dto.exception.LackOfFundsException;
import ru.yandex.practicum.dto.exception.RepeatTransactionException;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
class BalanceServiceImpl implements BalanceService {

    @Value("${payment_server.initial_balance:20000}")
    private Integer initialBalance;
    private final ConcurrentHashMap<String, Integer> balances = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<UUID, TransactionDto> transactions = new ConcurrentHashMap<>();


    @Override
    public Mono<BalanceDto> getBalance(String userLogin) {
        Integer currentBalance = getBalanceBlocking(userLogin);
        return Mono.just(new BalanceDto(currentBalance, LocalDateTime.now()));
    }

    @Override
    public Mono<BalanceDto> changeBalance(String userLogin, TransactionDto transactionDto) {
        UUID transactionUuid = transactionDto.getUuid();

        if (transactions.containsKey(transactionDto.getUuid())) {
            return Mono.error(new RepeatTransactionException(transactionDto.getUuid()));
        } else {
            transactions.put(transactionUuid, transactionDto);
        }

        Integer transactionAmount = transactionDto.getAmount();

        if (transactionDto.getOperation() == TransactionOperation.TOPPING) {
            balances.compute(userLogin, (ul, b) -> b == null ? initialBalance + transactionAmount : b + transactionAmount);
        }

        if (transactionDto.getOperation() == TransactionOperation.PAYMENT) {
            Integer currentBalance = getBalanceBlocking(userLogin);
            if (currentBalance < transactionAmount) {
                return Mono.error(new LackOfFundsException(transactionUuid));
            }
            balances.compute(userLogin, (ul, b) -> b - transactionAmount);
        }

        return Mono.just(new BalanceDto(balances.get(userLogin), LocalDateTime.now()));
    }

    private Integer getBalanceBlocking(String userLogin) {
        Integer existing = balances.putIfAbsent(userLogin, initialBalance);
        if (existing == null) {
            UUID txId = UUID.randomUUID();
            transactions.put(txId,
                    new TransactionDto(txId, TransactionOperation.TOPPING, initialBalance));
            return initialBalance;
        } else {
            return existing;
        }
    }

}