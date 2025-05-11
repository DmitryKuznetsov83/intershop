package ru.yandex.practicum.interpayment.server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.dto.dto.ApiErrorDto;
import ru.yandex.practicum.dto.dto.BalanceDto;
import ru.yandex.practicum.dto.dto.TransactionDto;
import ru.yandex.practicum.dto.exception.LackOfFundsException;
import ru.yandex.practicum.dto.exception.RepeatTransactionException;
import ru.yandex.practicum.interpayment.server.service.BalanceService;

@RestController
@RequestMapping("/balance")
@Validated
public class BalanceController {

    private final BalanceService balanceService;

    @Autowired
    public BalanceController(BalanceService balanceService) {
        this.balanceService = balanceService;
    }

    @GetMapping("/{userLogin}")
    public Mono<BalanceDto> getBalance(@PathVariable String userLogin) {
        return balanceService.getBalance(userLogin);
    }

    @PostMapping("/{userLogin}")
    public Mono<BalanceDto> changeBalance(@PathVariable String userLogin, @RequestBody @Validated TransactionDto transactionDto) {
        return balanceService.changeBalance(userLogin, transactionDto);
    }

    @ExceptionHandler({RepeatTransactionException.class})
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiErrorDto handleRepeatTransactionException(final RepeatTransactionException exception) {
        return getApiError(exception, HttpStatus.CONFLICT);
    }

    @ExceptionHandler({LackOfFundsException.class})
    @ResponseStatus(HttpStatus.PAYMENT_REQUIRED)
    public ApiErrorDto handleLackOfFundsException(final LackOfFundsException exception) {
        return getApiError(exception, HttpStatus.PAYMENT_REQUIRED);
    }


    private ApiErrorDto getApiError(Throwable exception, HttpStatus httpStatus) {
        return new ApiErrorDto(exception, httpStatus);
    }

}
