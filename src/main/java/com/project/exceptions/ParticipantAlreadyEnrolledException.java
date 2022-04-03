package com.project.exceptions;

public class ParticipantAlreadyEnrolledException extends RuntimeException{

    public ParticipantAlreadyEnrolledException() {
        super("This participant is already enrolled to the competition(s) requested!");
    }
}
