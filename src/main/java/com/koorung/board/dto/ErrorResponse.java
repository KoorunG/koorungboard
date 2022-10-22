package com.koorung.board.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * 에러 반환용 클래스
 * {
 *     "code" : "404",
 *     "message" : "잘못된 반환입니다!",
 *     "validation" : {
 *         "title" : "값을 입력해주세요",
 *         "contents" : "내용을 입력해주세요"
 *     }
 * }
 */
@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_EMPTY)     // Empty가 아닌 값만 JSON으로 내려줄 때 사용한다
@RequiredArgsConstructor
public class ErrorResponse {

    private final String code;
    private final String message;
    private final Map<String, String> validation;
//    private final List<ValidationTuple> validation = new ArrayList<>();         // ArrayList로 초기화
    //    private final Map<String, String> validation = new HashMap<>();
    public void addValidation(String fieldName, String errorMessage) {
        this.validation.put(fieldName, errorMessage);
    }
//
//    @Getter
//    @RequiredArgsConstructor
//    private static class ValidationTuple {
//        private final String fieldName;
//        private final String errorMessage;
//    }
}
