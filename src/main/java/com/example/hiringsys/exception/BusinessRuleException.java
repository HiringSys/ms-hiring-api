package com.example.hiringsys.exception;

public class BusinessRuleException extends RuntimeException {

    public BusinessRuleException(String mensagem) {
        super(mensagem);
    }
}
