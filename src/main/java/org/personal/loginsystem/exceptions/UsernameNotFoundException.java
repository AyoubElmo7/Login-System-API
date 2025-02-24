package org.personal.loginsystem.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class UsernameNotFoundException extends RuntimeException {
    public UsernameNotFoundException(String message) {
        super("Username: " + message + " was not found");
    }
}
