package com.project.exceptions;

import javax.ws.rs.BadRequestException;

public class NotEnoughFundsException extends BadRequestException {

    public NotEnoughFundsException() {
        super("All donation funds are consumed! Donation not possible!");
    }
}
