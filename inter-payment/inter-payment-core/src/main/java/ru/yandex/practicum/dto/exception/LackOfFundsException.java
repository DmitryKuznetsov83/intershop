package ru.yandex.practicum.dto.exception;

import java.util.UUID;

public class LackOfFundsException extends RuntimeException {

    public LackOfFundsException(String message) {
        super(message);
    }

    public LackOfFundsException(UUID transactionId) {
        super("Translation with id " + transactionId + " can't be processed. Not enough funds");
    }

}
