package ru.yandex.practicum.intershop.controller.advice;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.intershop.exception.EmptyCartException;

import java.util.List;
import java.util.NoSuchElementException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Mono<String> handleConstraintViolationException(ConstraintViolationException ex, Model model) {
        model.addAttribute("errors", List.of("Ошибка валидации: " + ex.getMessage()));
        return Mono.just("page-400.html");
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Mono<String> handleIllegalArgumentException(IllegalArgumentException ex, Model model) {
        model.addAttribute("errors", List.of("Некорректный аргумент: " + ex.getMessage()));
        return Mono.just("page-400.html");
    }

    @ExceptionHandler(IllegalStateException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Mono<String> handleIllegalStateException(IllegalStateException ex, Model model) {
        model.addAttribute("errors", List.of("Некорректный запрос: " + ex.getMessage()));
        return Mono.just("page-400.html");
    }

    @ExceptionHandler(EmptyCartException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Mono<String> handleEmptyCartException(EmptyCartException ex, Model model) {
        model.addAttribute("errors", List.of("Корзина пуста, нельзя оформить Заказ"));
        return Mono.just("page-400.html");
    }

    @ExceptionHandler(NoSuchElementException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Mono<String> handleNoSuchElementException(NoSuchElementException ex, Model model) {
        model.addAttribute("error","Страница не найдена: " + ex.getMessage());
        return Mono.just("page-404.html");
    }

    @ExceptionHandler(AuthorizationDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public Mono<String> handleAuthorizationDeniedException(AuthorizationDeniedException ex, Model model) {
        model.addAttribute("error","Вам запрещен доступ к ресурсу");
        return Mono.just("page-403.html");
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public Mono<String> handleAccessDeniedException(AccessDeniedException ex, Model model) {
        model.addAttribute("error","Вам запрещен доступ к ресурсу");
        return Mono.just("page-403.html");
    }

    @ExceptionHandler(Throwable.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Mono<String> handleThrowable(final Throwable ex, Model model) {
        model.addAttribute("error","Внутренняя ошибка сервера: " + ex.getMessage());
        return Mono.just("page-500.html");
    }

}