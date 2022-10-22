package com.koorung.board.service;

import com.koorung.board.domain.Board;
import com.koorung.board.dto.BoardCreateRequest;
import com.koorung.board.dto.BoardCreateResponse;
import com.koorung.board.dto.BoardEditor;
import com.koorung.board.dto.BoardSearch;
import com.koorung.board.exception.BoardNotFoundException;
import com.koorung.board.repository.board.BoardRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
class BoardServiceTest {

    @Autowired
    private BoardService boardService;      // 실제 서비스 주입

    @Autowired
    private BoardRepository boardRepository;

    @Test
    @DisplayName("글 작성")
    void writeBoard() {
        //given
        BoardCreateRequest createRequest = BoardCreateRequest.builder()
                .title("제목입니다")
                .contents("내용입니다")
                .build();

        //when
        boardService.write(createRequest);

        //then
        assertThat(boardRepository.count()).isEqualTo(1);
    }

    @Test
    @DisplayName("작성한 글 조회하기 (단건조회)")
    void getBoard() {
        //given - 글 1건 저장
        BoardCreateRequest createRequest = BoardCreateRequest.builder()
                .title("제목입니다")
                .contents("내용입니다")
                .build();
        Long savedId = boardService.write(createRequest);

        //when - 조회
        Board board = boardService.get(savedId);

        //then - 검증
        assertThat(board).isNotNull();
        assertThat(board).extracting("title").isEqualTo("제목입니다");
        assertThat(board).extracting("contents").isEqualTo("내용입니다");
    }

    @Test
    @DisplayName("작성한 글 모두 조회하기 (다건조회)")
    void getBoardAll() {
        // 테스트용 데이터 밀어넣기
        boardRepository.saveAll(Arrays.asList(
                Board.builder()
                        .title("제목입니다")
                        .contents("내용입니다")
                        .build(),
                Board.builder()
                        .title("제목입니다2")
                        .contents("내용입니다2")
                        .build()));

        //when - 모두 조회
        List<Board> boardList = boardService.getList();

        //then
        assertThat(boardList.size()).isEqualTo(2);
        assertThat(boardList).extracting("title").containsExactly("제목입니다", "제목입니다2");
    }
    
    @Test
    @DisplayName("페이징 적용")
    void paging() {
        //given - 글 밀어넣기 (IntStream을 이용하여 for문과 비슷한 기능..)
        List<Board> boardList = IntStream.rangeClosed(1, 30).mapToObj(i ->
                Board.builder()
                        .title("제목 테스트 - " + i)
                        .contents("내용 테스트 - " + i)
                        .build()).collect(Collectors.toList());

        boardRepository.saveAll(boardList);

        //when
        Page<Board> page = boardService.getList(PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "id")));  // 가장 최근 게시

        //then
        assertThat(page.getSize()).isEqualTo(5);
        assertThat(page.getTotalElements()).isEqualTo(30);
        assertThat(page.getContent()).extracting("title").containsExactly(
                "제목 테스트 - 30",
                "제목 테스트 - 29",
                "제목 테스트 - 28",
                "제목 테스트 - 27",
                "제목 테스트 - 26");
    }

    @Test
    @DisplayName("querydsl을 이용한 페이징 기능 테스트")
    void querydslGetlist() {
        //given
        List<Board> boardList = IntStream.rangeClosed(0, 29).mapToObj(i -> Board.builder()
                .title("제목 " + i)
                .contents("내용 " + i)
                .build()).collect(Collectors.toList());

        boardRepository.saveAll(boardList);

        //when
        List<Board> list = boardService.getList(BoardSearch.builder()
                .page(3)
                .size(10)
                .build());// 3페이지에서 10개의 글을 가져온다.

        //then
        assertThat(list.size()).isEqualTo(10);
        assertThat(list.get(9)).extracting("title").isEqualTo("제목 0");
        assertThat(list.get(0)).extracting("contents").isEqualTo("내용 9");
    }

    @Test
    @DisplayName("글 제목 수정하기")
    void edit() {
        //given
        Board board = Board.builder()
                .title("테스트제목")
                .contents("xptmxmsodyd")
                .build();

        boardRepository.save(board);

        Board changedBoard = boardService.get(board.getId());
        BoardEditor boardEditor = BoardEditor.builder()
                .title(null)
                .contents("진짜내용")
                .build();
        //when
        boardService.edit(board.getId(), boardEditor);

        //then
        assertThat(changedBoard.getTitle()).isEqualTo("테스트제목");
        assertThat(changedBoard.getContents()).isEqualTo("진짜내용");
    }

    @Test
    @DisplayName("글 삭제하기")
    void delete() {
        //given
        Board board = Board.builder()
                .title("테스트제목")
                .contents("테스트내용")
                .build();

        boardRepository.save(board);

        //when
        boardService.delete(board.getId());

        //then
        assertThrows(BoardNotFoundException.class, () -> {
            boardService.delete(board.getId());
        });
    }

    @Test
    @DisplayName("글 조회 실패 케이스")
    void fail_get() {
        //given
        Board board = Board.builder()
                .title("타이틀")
                .contents("콘텐츠")
                .build();

        boardRepository.save(board);

        //then
        BoardNotFoundException e = assertThrows(BoardNotFoundException.class, () -> {
            // 올바르지 않은 식별자로 조회했을 때 예외 발생
            Board fail = boardService.get(2L);
        }, "예외처리가 잘못됨");

        // assertThrows가 리턴하는 값을 검증할 수 있음
        // 커스텀 예외를 구현했을 때 이 단계를 생략할 수 있다는 장점... (예외가 비즈니스로직에 fit하게 맞기 때문에)
        assertThat(e.getMessage()).isEqualTo("존재하지 않는 글입니다!");
    }

    @Test
    @DisplayName("게시글 삭제 실패 케이스")
    void fail_delete() {
        //given
        Board board = Board.builder()
                .title("타이틀")
                .contents("콘텐츠")
                .build();

        boardRepository.save(board);

        //when
        assertThrows(BoardNotFoundException.class, () -> {
            boardService.delete(board.getId() + 1L);
        });
    }

    @Test
    @DisplayName("게시글 수정 실패 케이스")
    void fail_update() {
        //given
        Board board = Board.builder()
                .title("타이틀")
                .contents("콘텐츠")
                .build();

        boardRepository.save(board);

        //when
        assertThrows(BoardNotFoundException.class, () -> {
            boardService.edit(board.getId() + 1, BoardEditor.builder()
                    .title("수정된 제목")
                    .contents("수정된 내용")
                    .build());
        });
    }
}