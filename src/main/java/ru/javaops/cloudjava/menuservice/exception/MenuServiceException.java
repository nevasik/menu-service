package ru.javaops.cloudjava.menuservice.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class MenuServiceException extends RuntimeException {

    private final HttpStatus status;

    public MenuServiceException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

}
