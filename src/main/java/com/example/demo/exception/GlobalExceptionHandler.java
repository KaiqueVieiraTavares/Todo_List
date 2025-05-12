package com.example.demo.exception;

import com.example.demo.exception.taskexceptions.TaskNotFound;
import com.example.demo.exception.userexceptions.EmailAlreadyExistsException;
import com.example.demo.exception.userexceptions.UserNotFound;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;


@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResp> handleAccessDenied(AccessDeniedException e) {
        ErrorResp errorResp = new ErrorResp(
                HttpStatus.FORBIDDEN.value(),
                "Acesso negado: " + e.getMessage(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResp);
    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResp> handleException(Exception e){
        ErrorResp errorResp = new ErrorResp(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                e.getMessage(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResp);
    }
    @ExceptionHandler(UserNotFound.class)
    public ResponseEntity<ErrorResp> userNotFound(UserNotFound ex){
        ErrorResp errorResponse = new ErrorResp(
                HttpStatus.NOT_FOUND.value(),
                ex.getMessage(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(TaskNotFound.class)
    public ResponseEntity<ErrorResp> taskNotFound(TaskNotFound taskNotFound){
        ErrorResp errorResp = new ErrorResp(HttpStatus.NOT_FOUND.value()
                , taskNotFound.getMessage()
                ,LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResp);
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ErrorResp> emailAlreadyExists(EmailAlreadyExistsException e){
        ErrorResp errorResp = new ErrorResp(
                HttpStatus.CONFLICT.value(),
                e.getMessage(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResp);
    }
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResp> handleDataIntegrityViolation(DataIntegrityViolationException e){
        ErrorResp errorResp = new ErrorResp(HttpStatus.CONFLICT.value(), "Ja existe um registro com os mesmos valores ", LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResp);
    }
}
