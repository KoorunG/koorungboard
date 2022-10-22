package com.koorung.board;

import com.koorung.board.domain.Board;
import com.koorung.board.repository.board.BoardRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.nio.charset.StandardCharsets;

import static java.nio.charset.StandardCharsets.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest     // Spring Extension 포함
@ExtendWith(RestDocumentationExtension.class)
public class JUnit5ExampleTests {

    private MockMvc mockMvc;        // AsciiDocs에서 별도의 설정을 거친 뒤 초기화를 해주기 때문에 주입받을필요가 없음

    @Autowired
    private BoardRepository boardRepository;

    @BeforeEach
    public void setUp(WebApplicationContext webApplicationContext, RestDocumentationContextProvider restDocumentation) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(documentationConfiguration(restDocumentation))
                .build();
    }
    
    @Test
    @DisplayName("restdocs 테스트")
    void restdocs() throws Exception {
        mockMvc.perform(get("/").accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(document("index"));
    }

    @Test
    @DisplayName("글 단건조회 문서 작성 테스트")
    void restdocs_get() throws Exception {

        // 1. 조회용 글 저장
        Board board = Board.builder().title("글조회용테스트제목").contents("글조회용테스트내용").build();
        boardRepository.save(board);

        // 2. 테스트 작성 및 문서화
        mockMvc.perform(get("/boards/{boardId}", board.getId())
                        .accept(APPLICATION_JSON)
                        .contentType(APPLICATION_JSON)
                        .characterEncoding(UTF_8))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("get"));
    }
}
