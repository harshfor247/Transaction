package com.example.Transaction.exceptions;

public class PaymentAlreadyDoneException extends RuntimeException{
    public PaymentAlreadyDoneException(String message) {
        super(message);
    }
}
