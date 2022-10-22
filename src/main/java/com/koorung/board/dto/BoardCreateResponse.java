package com.koorung.board.dto;

import com.koorung.board.domain.Board;
import lombok.Builder;
import lombok.Getter;

@Getter             // json으로 생성되기 위함
public class BoardCreateResponse {
    private final Long id;
    private final String title;
    private final String contents;

    // 생성자 오버로딩으로 엔티티를 바로 DTO로 변환
    public BoardCreateResponse(Board board) {
        this.id = board.getId();
        this.title = board.getTitle().length() >= 10 ? board.getTitle().substring(0, 10) : board.getTitle();    // 조회시 10자 제한
        this.contents = board.getContents();
    }

    @Builder
    public BoardCreateResponse(Long id, String title, String contents) {
        this.id = id;
        this.title = title.length() >= 10 ? title.substring(0, 10) : title; // 응답에서는 title을 최대 10글자까지만 리턴하도록 비즈니스로직 추가
        this.contents = contents;
    }
}
