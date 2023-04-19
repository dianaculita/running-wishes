package com.project.controllers;

import javax.ws.rs.BadRequestException;

import com.project.exceptions.NotEnoughFundsException;
import com.project.exceptions.ParticipantAlreadyEnrolledException;
import com.project.exceptions.SponsorAlreadyExistsException;
import com.project.exceptions.UserAlreadyExistsException;
import com.project.exceptions.UserNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ErrorHandlerController {

    @ExceptionHandler(value = {UserAlreadyExistsException.class, UserNotFoundException.class,
          NotEnoughFundsException.class, ParticipantAlreadyEnrolledException.class,
          SponsorAlreadyExistsException.class})
    public ResponseEntity<?> handleBadRequest(BadRequestException badRequestException) {
        return new ResponseEntity<>("Bad request!", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {Exception.class, Error.class})
    public ResponseEntity<?> handleInternalServerError() {
        return new ResponseEntity<>("Internal server error!", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
