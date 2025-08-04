package com.example.Transaction.exceptions;

public class TransactionFailedException extends RuntimeException{
    public TransactionFailedException(String message){
        super(message);
    }
}
