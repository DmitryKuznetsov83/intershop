package ru.yandex.practicum.client;

import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.dto.*;
import ru.yandex.practicum.dto.dto.ApiErrorDto;
import ru.yandex.practicum.dto.dto.BalanceDto;
import ru.yandex.practicum.dto.dto.TransactionDto;
import ru.yandex.practicum.dto.exception.LackOfFundsException;
import ru.yandex.practicum.dto.exception.RepeatTransactionException;

import java.util.UUID;

public class InterPaymentClient {

    private final WebClient webClient;
    private final ReactiveOAuth2AuthorizedClientManager reactiveOAuth2AuthorizedClientManager;

    public InterPaymentClient(WebClient.Builder webClientBuilder, String url, ReactiveOAuth2AuthorizedClientManager reactiveOAuth2AuthorizedClientManager) {
        this.webClient = webClientBuilder.baseUrl(url).build();
        this.reactiveOAuth2AuthorizedClientManager = reactiveOAuth2AuthorizedClientManager;
    }

    public Mono<Integer> getBalance(Mono<String> userLogin) {
        return userLogin.flatMap(login ->
                getToken()
                        .flatMap(accessToken -> webClient.get()
                                .uri("/balance/" + login)  // Используем извлеченный login
                                .headers(h -> h.setBearerAuth(accessToken))
                                .retrieve()
                                .bodyToMono(BalanceDto.class)
                                .map(BalanceDto::getBalance)
                        )
        );
    }

    public Mono<BalanceDto> payment(Mono<String> userLogin, Integer amount) {

        return userLogin.flatMap(login ->
                getToken()
                        .flatMap(accessToken -> webClient.post()
                                .uri("/balance/" + login)
                                .headers(h -> h.setBearerAuth(accessToken))
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
                                .bodyToMono(BalanceDto.class)));

    }

    public Mono<Integer> topUp(Mono<String> userLogin, Integer amount) {
        return userLogin.flatMap(login ->
                getToken()
                        .flatMap(accessToken -> webClient.post()
                                .uri("/balance" + login)
                                .headers(h -> h.setBearerAuth(accessToken))
                                .bodyValue(new TransactionDto(UUID.randomUUID(), TransactionOperation.TOPPING, amount))
                                .retrieve()
                                .bodyToMono(BalanceDto.class)
                                .map(BalanceDto::getBalance)));
    }

    private Mono<String> getToken() {
        return reactiveOAuth2AuthorizedClientManager.authorize(OAuth2AuthorizeRequest
                        .withClientRegistrationId("inter-shop")
                        .principal("system")
                        .build())
                .map(OAuth2AuthorizedClient::getAccessToken)
                .map(OAuth2AccessToken::getTokenValue);
    }

}
