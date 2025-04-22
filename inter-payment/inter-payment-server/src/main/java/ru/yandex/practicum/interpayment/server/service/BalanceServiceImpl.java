package ru.yandex.practicum.interpayment.server.service;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.dto.dto.BalanceDto;
import ru.yandex.practicum.dto.dto.TransactionDto;
import ru.yandex.practicum.dto.TransactionOperation;
import ru.yandex.practicum.dto.exception.LackOfFundsException;
import ru.yandex.practicum.dto.exception.RepeatTransactionException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
class BalanceServiceImpl implements BalanceService {

    @Value("${payment_server.initial_balance:20000}")
    private Integer balance;
    private final Map<UUID, TransactionDto> transactions = new HashMap<>();

    @PostConstruct
    private void init() {
        UUID initialTransactionId = UUID.randomUUID();
        transactions.put(initialTransactionId, new TransactionDto(initialTransactionId, TransactionOperation.TOPPING, 20000));
    }


    @Override
    public Mono<BalanceDto> getBalance() {
        return Mono.just(new BalanceDto(balance, LocalDateTime.now()));
    }

    @Override
    public Mono<BalanceDto> changeBalance(TransactionDto transactionDto) {
        UUID transactionUuid = transactionDto.getUuid();

        if (transactions.containsKey(transactionDto.getUuid())) {
            return Mono.error(new RepeatTransactionException(transactionDto.getUuid()));
        } else {
            transactions.put(transactionUuid, transactionDto);
        }

        if (transactionDto.getOperation() == TransactionOperation.TOPPING) {
            balance += transactionDto.getAmount();
        }

        if (transactionDto.getOperation() == TransactionOperation.PAYMENT) {
            if (balance < transactionDto.getAmount()) {
                return Mono.error(new LackOfFundsException(transactionUuid));
            }
            balance -= transactionDto.getAmount();
        }
        return Mono.just(new BalanceDto(balance, LocalDateTime.now()));
    }

}
