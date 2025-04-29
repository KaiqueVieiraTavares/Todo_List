package com.example.demo.exception.userexceptions;

public class UserNotFound extends RuntimeException {
    public UserNotFound(String message) {
        super(message);
    }

    public UserNotFound(){
        super("usuario nao encontrado");
    }
}
