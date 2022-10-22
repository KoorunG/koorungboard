package com.koorung.board.dto;

import com.koorung.board.exception.InvalidTitleException;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

@Getter
@Setter
@ToString(of = {"title", "contents"})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BoardCreateRequest {

    // Validtaion을 이용하여 데이터 검증
    @NotEmpty(message = "제목은 반드시 존재해야 합니다!")
    private String title;

    @NotBlank(message = "내용은 반드시 존재해야 합니다!")
    private String contents;

    /**
     * 빌더의 장점
     *  1. 가독성에 좋다.
     *  2. 필요한 값만 받을 수 있다.
     *  3. 불변성을 유지할 수 있다. (final 사용시?)
     */
    @Builder
    private BoardCreateRequest(String title, String contents) {
        this.title = title;
        this.contents = contents;
    }

    public void validate() {
        if(title.contains("바보")) throw new InvalidTitleException("title", "바보는 제목으로 올 수 없습니다!");
    }
}
