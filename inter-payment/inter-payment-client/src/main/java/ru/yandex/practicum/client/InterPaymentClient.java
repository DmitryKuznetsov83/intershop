package ru.yandex.practicum.client;

import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.dto.*;

import java.util.UUID;

public class InterPaymentClient {

    private final WebClient webClient;

    public InterPaymentClient(WebClient.Builder webClientBuilder, String url) {
        this.webClient = webClientBuilder.baseUrl(url).build();
    }

    public Mono<Integer> getBalance() {
        return webClient.get()
                .uri("/balance")
                .retrieve()
                .bodyToMono(BalanceDto.class)
                .map(BalanceDto::getBalance);
    }

    public Mono<BalanceDto> payment(Integer amount) {
        return webClient.post()
                .uri("/balance")
                .bodyValue(new TransactionDto(UUID.randomUUID(), TransactionOperation.PAYMENT, amount))
                .retrieve()
                .onStatus(
                        code -> code == HttpStatus.PAYMENT_REQUIRED,
                        response -> response.bodyToMono(ApiErrorDto.class)
                                .flatMap(ApiErrorDto -> Mono.error(new LackOfFundsException(ApiErrorDto.getMessage())))
                )
                .onStatus(
                        code -> code == HttpStatus.CONFLICT,
                        response -> response.bodyToMono(ApiErrorDto.class)
                                .flatMap(ApiErrorDto -> Mono.error(new RepeatTransactionException(ApiErrorDto.getMessage())))
                )
                .bodyToMono(BalanceDto.class);
    }

    public Mono<Integer> topUp(Integer amount) {
        return webClient.post()
                .uri("/balance")
                .bodyValue(new TransactionDto(UUID.randomUUID(), TransactionOperation.TOPPING, amount))
                .retrieve()
                .bodyToMono(BalanceDto.class)
                .map(BalanceDto::getBalance);
    }

}
