package org.personal.loginsystem.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidSecurityQuestionOrAnswerException extends RuntimeException {
    public InvalidSecurityQuestionOrAnswerException() {
        super("The given security question or answer was incorrect");
    }
}
