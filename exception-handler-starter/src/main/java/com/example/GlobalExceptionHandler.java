package com.example;
import com.example.exception.*;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(value= EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(EntityNotFoundException ex){
        log.error(HttpStatus.NOT_FOUND +  " " + ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(HttpStatus.NOT_FOUND.value(),
                ex.getMessage(), new Date()));
    }

    @ExceptionHandler(value= JournalistDoesNotMatchException.class)
    public ResponseEntity<ErrorResponse> handleJournalistDoesNotMatchException(JournalistDoesNotMatchException ex){
        log.error(HttpStatus.BAD_REQUEST + " " + ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(),
                ex.getMessage(), new Date()));
    }

    @ExceptionHandler(value= AuthorDoesNotMatchException.class)
    public ResponseEntity<ErrorResponse> handleAuthorDoesNotMatchException(AuthorDoesNotMatchException ex){
        log.error(HttpStatus.BAD_REQUEST + " " + ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(),
                ex.getMessage(), new Date()));
    }

    @ExceptionHandler(value= AuthorIsBlockedException.class)
    public ResponseEntity<ErrorResponse> handleAuthorIsBlockedException(AuthorIsBlockedException ex){
        log.error(HttpStatus.LOCKED + " " + ex.getMessage());
        return ResponseEntity.status(HttpStatus.LOCKED).body(new ErrorResponse(HttpStatus.LOCKED.value(),
                ex.getMessage(), new Date()));
    }

    @ExceptionHandler(value= CommentNotCorrespondsToNewException.class)
    public ResponseEntity<ErrorResponse> handleCommentNotCorrespondsToNewException(CommentNotCorrespondsToNewException ex){
        log.error(HttpStatus.CONFLICT + " " + ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorResponse(HttpStatus.CONFLICT.value(),
                ex.getMessage(), new Date()));
    }

    @ExceptionHandler(value= JournalistIsBlockedException.class)
    public ResponseEntity<ErrorResponse> handleJournalistIsBlockedException(JournalistIsBlockedException ex){
        log.error(HttpStatus.LOCKED + " " + ex.getMessage());
        return ResponseEntity.status(HttpStatus.LOCKED).body(new ErrorResponse(HttpStatus.LOCKED.value(),
                ex.getMessage(), new Date()));
    }

    @ExceptionHandler(value= TokenIsNotValidException.class)
    public ResponseEntity<ErrorResponse> handleTokenIsNotValidException(TokenIsNotValidException ex){
        log.error(HttpStatus.FORBIDDEN + " " + ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(HttpStatus.FORBIDDEN.value(),
                ex.getMessage(), new Date()));
    }

    @ExceptionHandler(value= NoAccessException.class)
    public ResponseEntity<ErrorResponse> handleNoAccessException(NoAccessException ex){
        log.error(HttpStatus.FORBIDDEN + " " + ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(HttpStatus.FORBIDDEN.value(),
                ex.getMessage(), new Date()));
    }

    @ExceptionHandler(value= WrongAuthorException.class)
    public ResponseEntity<ErrorResponse> handleWrongAuthorException(WrongAuthorException ex){
        log.error(HttpStatus.FORBIDDEN + " " + ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(HttpStatus.FORBIDDEN.value(),
                ex.getMessage(), new Date()));
    }

    @ExceptionHandler(value= WrongJournalistException.class)
    public ResponseEntity<ErrorResponse> handleWrongJournalistException(WrongJournalistException ex){
        log.error(HttpStatus.FORBIDDEN + " " + ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(HttpStatus.FORBIDDEN.value(),
                ex.getMessage(), new Date()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        List<String> errorMessages = ex.getBindingResult().getFieldErrors()
                .stream().map(FieldError::getDefaultMessage).collect(Collectors.toList());
        log.error(HttpStatus.BAD_REQUEST + " " + ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(),
                errorMessages.toString(), new Date()));
    }

    @ExceptionHandler(value=ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolationException(ConstraintViolationException ex){
        log.error(HttpStatus.BAD_REQUEST + " " + ex.getMessage());
        List<String> errorMessages = new ArrayList<>();
        for (ConstraintViolation<?> violation : ex.getConstraintViolations()) {
            errorMessages.add(violation.getMessage());
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(),
                errorMessages.toString(), new Date()));
    }

    @ExceptionHandler(value= MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex){
        log.error(HttpStatus.METHOD_NOT_ALLOWED + " " + ex.getMessage());
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(new ErrorResponse(HttpStatus.METHOD_NOT_ALLOWED.value(),
                ex.getMessage(), new Date()));
    }

    @ExceptionHandler(value= HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException ex){
        log.error(HttpStatus.METHOD_NOT_ALLOWED + " " + ex.getMessage());
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(new ErrorResponse(HttpStatus.METHOD_NOT_ALLOWED.value(),
                ex.getMessage(), new Date()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
        log.error(HttpStatus.BAD_REQUEST + " " + ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(),
                ex.getMessage()));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        log.error(HttpStatus.BAD_REQUEST + " " + ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(),
                ex.getMessage(), new Date()));
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoHandlerFoundException(NoHandlerFoundException ex) {
        log.error(HttpStatus.NOT_FOUND + " " + ex.getRequestURL());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(HttpStatus.NOT_FOUND.value(), ex.getRequestURL(), new Date()));
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleUnsupportedMediaTypeException(HttpMediaTypeNotSupportedException ex) {
        log.error(String.valueOf(HttpStatus.UNSUPPORTED_MEDIA_TYPE), ex.getContentType());
        return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body(new ErrorResponse(HttpStatus.UNSUPPORTED_MEDIA_TYPE.value(), ex.getContentType().toString(), new Date()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleCommonException(Exception ex){
        log.error(ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(),ex.getMessage(), new Date()));
    }
}
