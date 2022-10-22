package com.koorung.board.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@ToString(of = {"title", "contents"})
public class BoardEditor {

    // 기능이 다르다면 코드가 매우 비슷하더라도 별도의 클래스를 만드는 것이 나음
    @NotBlank(message = "제목을 입력하세요.")
    private String title;

    @NotBlank(message = "내용을 입력하세요.")
    private String contents;

    public BoardEditor(String title, String contents) {
        this.title = title;
        this.contents = contents;
    }

    public static BoardEditor.PostEditorBuilder builder() {
        return new PostEditorBuilder();
    }


    @ToString(of = {"title", "contents"})
    public static class PostEditorBuilder {
        private String title;
        private String contents;

        public PostEditorBuilder() {
        }

        public BoardEditor.PostEditorBuilder title(final String title) {
            if (title != null) {
                this.title = title;
            }
            return this;
        }

        public BoardEditor.PostEditorBuilder contents(final String contents) {
            if (contents != null)
                this.contents = contents;
            return this;
        }

        public BoardEditor build() {
            return new BoardEditor(this.title, this.contents);
        }
    }
}
