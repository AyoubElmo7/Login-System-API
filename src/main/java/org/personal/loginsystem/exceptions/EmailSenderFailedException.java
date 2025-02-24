package org.personal.loginsystem.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class EmailSenderFailedException extends RuntimeException {
    public EmailSenderFailedException(String message) {
        super("Failed to send email to " + message);
    }
}
