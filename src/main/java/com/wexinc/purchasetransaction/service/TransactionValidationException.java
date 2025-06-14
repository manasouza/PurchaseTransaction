package com.wexinc.purchasetransaction.service;

public class TransactionValidationException extends RuntimeException {

    public TransactionValidationException(String message) {
        super(message);
    }
}
