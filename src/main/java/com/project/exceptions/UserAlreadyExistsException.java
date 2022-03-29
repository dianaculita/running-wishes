package com.project.exceptions;

import javax.ws.rs.BadRequestException;

public class UserAlreadyExistsException extends BadRequestException {

    public UserAlreadyExistsException() {
        super("The user CNP already exists!");
    }
}
