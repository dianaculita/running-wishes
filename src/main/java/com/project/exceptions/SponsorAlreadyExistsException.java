package com.project.exceptions;

import javax.ws.rs.BadRequestException;

public class SponsorAlreadyExistsException extends BadRequestException {

    public SponsorAlreadyExistsException() {
        super("The sponsor already has a contract with the competition(s) requested!");
    }

}
