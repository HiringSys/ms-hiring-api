package com.example.hiringsys.exception;

public class InvalidStatusTransitionException extends RuntimeException {

    public InvalidStatusTransitionException(String mensagem) {
        super(mensagem);
    }
}
