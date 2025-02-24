package org.personal.loginsystem.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class EmailDoesNotExistException extends RuntimeException {
    public EmailDoesNotExistException(String message) {
        super("Email: " + message + " is not associated with an Account");
    }
}
