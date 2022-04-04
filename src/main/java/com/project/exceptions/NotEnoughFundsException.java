package com.project.exceptions;

public class NotEnoughFundsException extends RuntimeException{

    public NotEnoughFundsException() {
        super("All donation funds are consumed! Donation not possible!");
    }
}
