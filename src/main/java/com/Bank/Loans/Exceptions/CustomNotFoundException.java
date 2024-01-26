package com.Bank.Loans.Exceptions;

public class CustomNotFoundException extends RuntimeException{

    public CustomNotFoundException(String message) {
        super(message);
    }
}
