package com.koorung.board.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 검색조건 클래스
 */

@Getter
@Setter
public class BoardSearch {

    @Builder.Default            // @Builder.Default를 쓰기 위해서는 클래스위에 @Builder를 선언해야 함
    private int page = 1;
    @Builder.Default
    private int size = 10;

    @Builder
    public BoardSearch(Integer page, Integer size) {
//        this.page = page == null ? 1 : page;
//        this.size = size == null ? 10 : size;
        this.page = page;
        this.size = size;
    }

    public long getOffset() {
        long offset = (page - 1) * (long) size;
        return offset < 0 ? 0 : offset;
    }
}
