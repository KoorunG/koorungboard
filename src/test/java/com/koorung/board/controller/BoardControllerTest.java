package com.koorung.board.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.koorung.board.domain.Board;
import com.koorung.board.dto.BoardCreateRequest;
import com.koorung.board.dto.BoardEditor;
import com.koorung.board.repository.board.BoardRepository;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.regex.Matcher;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

//@WebMvcTest                         // 스프링 mvc 테스트 시 사용! (단 @SpringBootTest와 같이 올 수 없다)
@AutoConfigureMockMvc               // 스프링부트 테스트 + @WebMvcTest의 Mocking을 같이 사용하고 싶을 때 사용
@SpringBootTest                     // 스프링부트 테스트
@Transactional                      // 각 테스트별로 별도의 트랜잭션 적용 - @BeforeEach를 굳이 적용하지 않아도 된다...!
class BoardControllerTest {

    @Autowired
    private BoardRepository boardRepository;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;


    private String createBoard(String title, String contents) throws JsonProcessingException {
//        BoardCreateRequest createRequest = new BoardCreateRequest(title, contents);
        BoardCreateRequest createRequest = BoardCreateRequest.builder()
                .title(title)
                .contents(contents)
                .build();
        return objectMapper.writeValueAsString(createRequest);
    }

    private String updateBoard(String title, String contents) throws JsonProcessingException {
        BoardEditor boardEditor = BoardEditor.builder()
                .title(title)
                .contents(contents)
                .build();
        return objectMapper.writeValueAsString(boardEditor);
    }

    @Test
    @DisplayName("알맞은 POST:/boards 요청 시 success 출력")
    void boardsPost() throws Exception {
        // given
        // JSON을 직접 작성하기보다는 객체를 넘기도록 리팩토링
        String jsonString = createBoard("글쓰기제목테스트", "글쓰기내용테스트");

        mockMvc.perform(
                        post("/boards")
                                .contentType(APPLICATION_JSON)
                                .characterEncoding(UTF_8)
//                       .param("title", "글쓰기제목 테스트")
//                       .param("contents", "글쓰기내용 테스트"))
                                .content(jsonString))
                .andExpect(status().isOk())
//               .andExpect(content().json(jsonString))
//               .andExpect(jsonPath("$.*").isEmpty())
                .andDo(print());
    }

//    @Test
//    @DisplayName("검증에 걸리는 POST:/boards 요청 시 title이 없으면 예외처리")
//    void validation() throws Exception {
//
//        String jsonString = createBoard(null, "글쓰기내용 테스트");
//        mockMvc.perform(post("/boards")
//                        .contentType(APPLICATION_JSON)
//                        .characterEncoding(UTF_8)
////                .content("{\"title\" : \"\" , \"contents\" : \"글쓰기내용 테스트\"}"))      // title이 빈 값일 때 검증
//                        .content(jsonString))        // title이 null일때 검증
////                .andExpect(status().isOk())
//                .andExpect(status().isBadRequest())
//                .andExpect(jsonPath("$.code").value("404"))
//                .andExpect(jsonPath("$.message").value("잘못된 요청입니다!"))
////                .andExpect(jsonPath("$.validation.title").value("제목은 반드시 존재해야 합니다!"))
//                .andDo(print());
//    }

    @Test
    @DisplayName("POST:/boards 요청 시 DB에 저장된다")
    void saveBoard() throws Exception {

        String jsonString = createBoard("글쓰기제목테스트", "글쓰기내용테스트");
        // when
        mockMvc.perform(post("/boards")
                        .contentType(APPLICATION_JSON)
                        .characterEncoding(UTF_8)
                        .content(jsonString))
                .andExpect(status().isOk())
                .andDo(print());

        // then
        assertThat(boardRepository.count()).isEqualTo(1L);
        assertThat(boardRepository.findAll()).extracting("title").containsExactly("글쓰기제목테스트");
        assertThat(boardRepository.findAll()).extracting("contents").containsExactly("글쓰기내용테스트");
    }

    @Test
    @DisplayName("글 조회 (단건 조회)")
    void getBoard() throws Exception {
        //given
        Board board = Board.builder().title("글조회용테스트제목ㄴㅁㄹㄴㅇㄹㅇㄴㄹㄴㅇㄹㅇㄴㄹㅇㄴㄹㅇ").contents("글조회용테스트내용").build();
        boardRepository.save(board);
        //when
        mockMvc.perform(get("/boards/{boardId}", board.getId())
                        .contentType(APPLICATION_JSON)
                        .characterEncoding(UTF_8))
                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.id").value(board.getId()))
                .andExpect(jsonPath("$.title").value("글조회용테스트제목ㄴ"))     // 제목이 10글자 이상일 경우 10글자로 substring 처리
                .andExpect(jsonPath("$.contents").value("글조회용테스트내용"))
                .andDo(print());
    }

//    @Test
//    @DisplayName("글 조회 (다건 조회)")
//    void getBoardList() throws Exception {
//        //given
//        Board board = Board.builder()
//                .title("글조회용테스트제목ㄴㅁㄹㄴㅇㄹㅇㄴㄹㄴㅇㄹㅇㄴㄹㅇㄴㄹㅇ")
//                .contents("글조회용테스트내용")
//                .build();
//        boardRepository.save(board);
//
//        Board board2 = Board.builder()
//                .title("글조회용테스트2")
//                .contents("글조회용테스트내용2")
//                .build();
//        boardRepository.save(board2);
//
//        //when
//        mockMvc.perform(get("/boards")
//                .contentType(APPLICATION_JSON)
//                .characterEncoding(UTF_8))
//                .andExpect(status().isOk())
//                /**
//                 * [
//                 *     {
//                 *         "id": 1,
//                 *         "title": "글조회용테스트제목ㄴ",
//                 *         "contents": "글조회용테스트내용"
//                 *     },
//                 *     {
//                 *         "id": 2,
//                 *         "title": "글조회용테스트2",
//                 *         "contents": "글조회용테스트내용2"
//                 *     }
//                 * ]
//                 */
//                .andExpect(jsonPath("$.[0].title").value("글조회용테스트제목ㄴ"))
//                .andExpect(jsonPath("$.[1].title").value("글조회용테스트2"))
//                .andExpect(jsonPath("$.[0].contents").value("글조회용테스트내용"))
//                .andExpect(jsonPath("$.[1].contents").value("글조회용테스트내용2"))
//                .andExpect(jsonPath("$.size()", Matchers.is(2)))
//                .andDo(print());
//    }

    @Test
    @DisplayName("글조회 - 페이징")
    void paging() throws Exception {
        //given
        //given - 글 밀어넣기 (IntStream을 이용하여 for문과 비슷한 기능..)
        List<Board> boardList = IntStream.rangeClosed(1, 30).mapToObj(i ->
                Board.builder()
                        .title("제목테스트-" + i)
                        .contents("내용테스트-" + i)
                        .build()).collect(Collectors.toList());
        boardRepository.saveAll(boardList);

        //when
        mockMvc.perform(get("/boards?page=3&size=10")
                .contentType(APPLICATION_JSON)
                .characterEncoding(UTF_8))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", Matchers.is(10)))
//                .andExpect(jsonPath("$.[0].id").value(10))
                .andExpect(jsonPath("$.[0].title").value("제목테스트-10"))
                .andExpect(jsonPath("$.[0].contents").value("내용테스트-10"))
                .andDo(print());
    }



    @Test
    @DisplayName("페이지를 0으로 요청하면 첫 페이지를 가져온다.")
    void paging_validation() throws Exception {
        //given
        //given - 글 밀어넣기 (IntStream을 이용하여 for문과 비슷한 기능..)
        List<Board> boardList = IntStream.rangeClosed(1, 30).mapToObj(i ->
                Board.builder()
                        .title("제목테스트-" + i)
                        .contents("내용테스트-" + i)
                        .build()).collect(Collectors.toList());
        boardRepository.saveAll(boardList);

        System.out.println("git diff test");
        System.out.println("git branch test");
        System.out.println("hello, i'm koorung -1");

        //when
        mockMvc.perform(get("/boards?page=0&size=10")
                        .contentType(APPLICATION_JSON)
                        .characterEncoding(UTF_8))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", Matchers.is(10)))
//                .andExpect(jsonPath("$.[0].id").value(30))
                .andExpect(jsonPath("$.[0].title").value("제목테스트-30"))
                .andExpect(jsonPath("$.[0].contents").value("내용테스트-30"))
                .andDo(print());
    }

    @Test
    @DisplayName("글 수정")
    void edit() throws Exception {
        // given
        Board board = Board.builder()
                .title("수정하기전제목")
                .contents("수정하기전내용")
                .build();
        boardRepository.save(board);

        // expected
        mockMvc.perform(patch("/boards/{boardId}", board.getId())
                .contentType(APPLICATION_JSON)
                .characterEncoding(UTF_8)
                .content(updateBoard("수정하고난뒤제목", "수정하고난뒤내용")))
                .andExpect(status().isOk())
                .andDo(print());

        Board findBoard = boardRepository.findById(board.getId()).orElseThrow(IllegalArgumentException::new);
        assertThat(findBoard.getTitle()).isEqualTo("수정하고난뒤제목");
        assertThat(findBoard.getContents()).isEqualTo("수정하고난뒤내용");
    }

    @Test
    @DisplayName("게시글 삭제 테스트")
    void deleteTest() throws Exception {
        // given
        Board board = Board.builder()
                .title("수정하기전제목")
                .contents("수정하기전내용")
                .build();
        boardRepository.save(board);

        // 삭제전 : 남아있음
        assertThat(boardRepository.findById(board.getId())).isPresent();

        //when
        mockMvc.perform(delete("/boards/{boardId}", board.getId())
                .contentType(APPLICATION_JSON)
                .characterEncoding(UTF_8))
                .andExpect(status().isOk())
                .andDo(print());
        // 삭제후 : 비어있음
        assertThat(boardRepository.findById(board.getId())).isEmpty();

        // 추가적으로... 인증에 대한 부분 (Spring Security), 비즈니스 로직(BizLogic), 세션(HttpSession) 등등을 추가적으로 구현해줘야 할 것
    }

    @Test
    @DisplayName("존재하지 않는 게시글 조회")
    void fail_getBoard() throws Exception {
        // expected
        mockMvc.perform(get("/boards/{boardId}", 1L)
                .contentType(APPLICATION_JSON)
                .characterEncoding(UTF_8))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("존재하지 않는 글입니다!"))
                .andDo(print());
    }

    @Test
    @DisplayName("존재하지 않는 게시글 수정")
    void fail_editBoard() throws Exception {
        // Expected
        mockMvc.perform(patch("/boards/{boardId}", 1L)
                .contentType(APPLICATION_JSON)
                .characterEncoding(UTF_8)
                .content(updateBoard("수정될 제목", "수정될 내용")))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("존재하지 않는 글입니다!"))
                .andDo(print());
    }

    @Test
    @DisplayName("글 생성 시 제목에 '바보'가 들어가있으면 예외 발생")
    void invalid_title() throws Exception {
        // Expected
        mockMvc.perform(post("/boards")
                        .contentType(APPLICATION_JSON)
                        .characterEncoding(UTF_8)
                        .content(createBoard("저는 바보입니다", "테스트")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("적절하지 않은 제목입니다!"))
                .andDo(print());
    }
}

