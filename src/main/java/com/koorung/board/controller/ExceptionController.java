package com.koorung.board.controller;

import com.koorung.board.dto.ErrorResponse;
import com.koorung.board.exception.BoardNotFoundException;
import com.koorung.board.exception.InvalidTitleException;
import com.koorung.board.exception.KoorungBoardException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;

@Slf4j
@RestControllerAdvice
public class ExceptionController {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({MethodArgumentNotValidException.class})
    public ErrorResponse invalidRequestHandler(MethodArgumentNotValidException e) {

        log.error("exceptionHandler", e);

        // #2. 리턴타입 : ErrorResponse
        ErrorResponse errorResponse = ErrorResponse.builder()
                .code("404")
                .message("잘못된 요청입니다!")
                .build();

        e.getFieldErrors().forEach(fieldError -> {
            errorResponse.addValidation(fieldError.getField(), fieldError.getDefaultMessage());
        });

        return errorResponse;
        // #1. 리턴타입 : HashMap
//        FieldError fieldError = e.getFieldError();
//        String field = fieldError.getField();
//        String defaultMessage = e.getFieldError().getDefaultMessage();
//
//        Map<String, String> error = new HashMap<>();
//        error.put(field, defaultMessage);
//
//        return error;
    }

//    @ResponseStatus(HttpStatus.NOT_FOUND)
//    @ExceptionHandler
//    public ErrorResponse boardNotFound(BoardNotFoundException e) {
//        return ErrorResponse.builder()
//                .code(HttpStatus.NOT_FOUND.toString())
//                .message(e.getMessage())
//                .build();
//    }
//
//    @ResponseStatus(HttpStatus.BAD_REQUEST)
//    @ExceptionHandler
//    public ErrorResponse invalidTitle(InvalidTitleException e) {
//        return ErrorResponse.builder()
//                .code(HttpStatus.BAD_REQUEST.toString())
//                .message(e.getMessage())
//                .build();
//    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> koorungBoardException(KoorungBoardException e) {
        ErrorResponse body = ErrorResponse.builder()
                .code(String.valueOf(e.statusCode()))
                .message(e.getMessage())
                .validation(e.getValidation())
                .build();
        return ResponseEntity.status(e.statusCode()).body(body);
    }
}
