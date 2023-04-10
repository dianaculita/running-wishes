package com.project.exceptions;

import javax.ws.rs.BadRequestException;

public class ParticipantAlreadyEnrolledException extends BadRequestException {

    public ParticipantAlreadyEnrolledException() {
        super("This participant is already enrolled to the competition(s) requested!");
    }
}
