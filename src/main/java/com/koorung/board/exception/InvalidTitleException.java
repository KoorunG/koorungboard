package com.koorung.board.exception;

import lombok.Getter;

/**
 * status : 400
 */
@Getter
public class InvalidTitleException extends KoorungBoardException {

    // 특정 상황에 맞는 예외이므로 message를 상수로 전달한다.
    private static final String MESSAGE = "적절하지 않은 제목입니다!";

    public InvalidTitleException(String fieldName, String message) {
        super(MESSAGE);
        addValidation(fieldName, message);
    }

    public InvalidTitleException(Throwable cause) {
        super(MESSAGE, cause);
    }

    @Override
    public int statusCode() {
        return 400;
    }
}
