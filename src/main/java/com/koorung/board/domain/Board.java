package com.koorung.board.domain;

import com.koorung.board.dto.BoardEditor;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(of = {"id", "title", "contents"})
public class Board {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "board_id")
    private Long id;

    private String title;

    @Lob                        // @Lob를 이용하여 DB에는 Long text 형태로 넘기는 것이 좋다
    private String contents;

    @Builder
    public Board(String title, String contents) {
        this.title = title;
        this.contents = contents;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    // 파라미터를 업데이트용 폼인 BoardEditor 으로
    public void changeBoard(BoardEditor boardEditor) {
        this.title = boardEditor.getTitle();
        this.contents = boardEditor.getContents();
    }
}
