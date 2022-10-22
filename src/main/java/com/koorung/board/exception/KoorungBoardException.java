package com.koorung.board.exception;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

// 프로젝트 공통 예외를 추상클래스로 선언
@Getter
public abstract class KoorungBoardException extends RuntimeException{

    private final Map<String, String> validation = new HashMap<>();
    public KoorungBoardException(String message) {
        super(message);
    }

    public KoorungBoardException(String message, Throwable cause) {
        super(message, cause);
    }

    // 에러코드를 이런식으로 추상메소드로 만들고 상속받는 클래스에서 정의하는 것이 좋다.
    public abstract int statusCode();

    public void addValidation(String fieldName, String message) {
        validation.put(fieldName, message);
    }
}
