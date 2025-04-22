package ru.yandex.practicum.dto;

import java.util.UUID;

public class RepeatTransactionException extends RuntimeException{

    public RepeatTransactionException(String message) {
        super(message);
    }

    public RepeatTransactionException(UUID transactionId) {
        super("Translation with id " + transactionId + " has already been processed.");
    }

}
