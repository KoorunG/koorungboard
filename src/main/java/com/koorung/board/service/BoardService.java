package com.koorung.board.service;

import com.koorung.board.domain.Board;
import com.koorung.board.dto.BoardCreateRequest;
import com.koorung.board.dto.BoardEditor;
import com.koorung.board.dto.BoardSearch;
import com.koorung.board.exception.BoardNotFoundException;
import com.koorung.board.repository.board.BoardRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class BoardService {
    private final BoardRepository boardRepository;       // Field Injection은 테스트케이스서만 쓰도록 하자!
                                                    // 거의 생성자 주입을 쓴다.

    public Long write(BoardCreateRequest request) {
        // 1. 요청 DTO를 엔티티로 변환
        Board board = Board.builder()
                .title(request.getTitle())
                .contents(request.getContents())
                .build();
        // 2. 저장
        return boardRepository.save(board).getId();
    }

    public Board get(Long boardId) {
        return boardRepository.findById(boardId)
                .orElseThrow(BoardNotFoundException::new);
    }

    public List<Board> getList() {
        return boardRepository.findAll();
    }

    public Page<Board> getList(Pageable pageable) {
        // findAll(Pageable pageable) 메소드 사용...!
        return boardRepository.findAll(pageable);
    }

    /**
     * Querydsl을 이용한 조회쿼리
     * @param boardSearch
     */
    public List<Board> getList(BoardSearch boardSearch) {
        return boardRepository.getList(boardSearch);
    }

    public void edit(Long id, BoardEditor boardEdit) {
        Board board = boardRepository.findById(id).orElseThrow(BoardNotFoundException::new);

        // BoardEdit의 필드에 @NotBlank validation이 없는 상태 && 수정조건이 넘어가지 않았을 경우에 대한 validation도 생각해봐야 할것...
        String newTitle = boardEdit.getTitle() == null ? board.getTitle() : boardEdit.getTitle();
        String newContents = boardEdit.getContents() == null ? board.getContents() : boardEdit.getContents();
        boardEdit.setTitle(newTitle);
        boardEdit.setContents(newContents);     // 이정도 하면 무난할듯...?
        board.changeBoard(boardEdit);
    }

    public void delete(Long id) {
        Board board = boardRepository.findById(id).orElseThrow(BoardNotFoundException::new);
        boardRepository.delete(board);
    }
}
