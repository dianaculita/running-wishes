package com.project.exceptions;

public class SponsorAlreadyExistsException extends RuntimeException{

    public SponsorAlreadyExistsException() {
        super("The sponsor already has a contract with the competition(s) requested!");
    }

}
