package com.project.exceptions;

import javax.ws.rs.BadRequestException;

public class UserNotFoundException extends BadRequestException {

    public UserNotFoundException() {
        super("This user does not exist!");
    }

}
