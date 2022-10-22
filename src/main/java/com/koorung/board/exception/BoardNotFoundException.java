package com.koorung.board.exception;

/**
 * status : 404
 */
public class BoardNotFoundException extends KoorungBoardException {

    // 특정 상황에 맞는 예외이므로 message를 상수로 전달한다.
    private static final String MESSAGE = "존재하지 않는 글입니다!";
    public BoardNotFoundException() {
        super(MESSAGE);
    }

    public BoardNotFoundException(Throwable cause) {
        super(MESSAGE, cause);
    }

    @Override
    public int statusCode() {
        return 404;
    }
}
