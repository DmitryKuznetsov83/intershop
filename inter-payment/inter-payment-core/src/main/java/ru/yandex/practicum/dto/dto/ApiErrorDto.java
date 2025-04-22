package ru.yandex.practicum.dto.dto;

import lombok.Data;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
public class ApiErrorDto {

    private String exception;
    private String message;
    private String status;
    private String timestamp;

    public ApiErrorDto(Throwable exception, HttpStatus status) {
        this.exception = exception.getClass().getSimpleName();
        this.message = exception.getMessage();
        this.status = status.toString();
        this.timestamp = DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(LocalDateTime.now());
    }
}
