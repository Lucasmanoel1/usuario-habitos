package com.lucasmanoel.usuario.controller;

import com.lucasmanoel.usuario.infrastructure.exceptions.ConflictException;
import com.lucasmanoel.usuario.infrastructure.exceptions.ResourceNotFoundException;
import com.lucasmanoel.usuario.infrastructure.exceptions.UnauthorizedException;
import com.lucasmanoel.usuario.infrastructure.exceptions.dto.ErrorResponseDTO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionsHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handlerResourceNotFoundExceptions(ResourceNotFoundException exception,
                                                                              HttpServletRequest request){
        return  ResponseEntity.status(HttpStatus.NOT_FOUND).body(buildErro(HttpStatus.NOT_FOUND.value(),
                exception.getMessage(),
                "Not Found",
                request.getRequestURI()
        ));
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ErrorResponseDTO> handlerConflictException(ConflictException exception, HttpServletRequest request){
        return  ResponseEntity.status(HttpStatus.CONFLICT).body(buildErro(HttpStatus.CONFLICT.value(),
                exception.getMessage(),
                "CONFLICT",
                request.getRequestURI()
        ));
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorResponseDTO> handlerUnauthorizedException(UnauthorizedException exception, HttpServletRequest request){
        return  ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(buildErro(HttpStatus.UNAUTHORIZED.value(),
                exception.getMessage(),
                "UNAUTHORIZED",
                request.getRequestURI()
        ));
    }

    private ErrorResponseDTO buildErro(int status, String mensagem, String error, String path){
       return ErrorResponseDTO.builder()
                .timestamp(LocalDateTime.now())
                .message(mensagem)
                .error(error)
                .status(status)
                .path(path)
                .build();
}
}
