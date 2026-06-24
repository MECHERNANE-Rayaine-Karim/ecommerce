package com.rayaine.ecommerce.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.security.access.AccessDeniedException;


@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<String> handleProductNotFound( ProductNotFoundException productNotFoundException){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(productNotFoundException.getMessage());
    }

    @ExceptionHandler(OrderNotFoundException.class)
    public ResponseEntity<String> handleOrderNotFound( OrderNotFoundException orderNotFoundException){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(orderNotFoundException.getMessage());
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<String> handleUsernameNotFound( UsernameNotFoundException usernameNotFoundException){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(usernameNotFoundException.getMessage());
    }

    @ExceptionHandler(UnavailableProductException.class)
    public ResponseEntity<String> handleUnavailableProduct( UnavailableProductException unavailableProductException){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(unavailableProductException.getMessage());
    }

    @ExceptionHandler(InvalidOrderOperationException.class)
    public ResponseEntity<String> handleInvalidOrderOperation( InvalidOrderOperationException invalidOrderOperationException){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(invalidOrderOperationException.getMessage());
    }

    @ExceptionHandler(UsernameAlreadyExistsException.class)
    public ResponseEntity<String> handleUsernameAlreadyExists( UsernameAlreadyExistsException usernameAlreadyExistsException){
        return ResponseEntity.status(HttpStatus.CONFLICT).body(usernameAlreadyExistsException.getMessage());
    }

    @ExceptionHandler(UnauthorizedOrderAccessException.class)
    public ResponseEntity<String> handleUnauthorizedOrderAccess( UnauthorizedOrderAccessException unauthorizedOrderAccessException){
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(unauthorizedOrderAccessException.getMessage());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<String> handleAccessDenied( AccessDeniedException accessDeniedException){
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(accessDeniedException.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGeneric( Exception exception){
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred");
    }


}
