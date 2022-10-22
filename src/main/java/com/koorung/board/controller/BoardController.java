package com.koorung.board.controller;

import com.koorung.board.domain.Board;
import com.koorung.board.dto.BoardCreateRequest;
import com.koorung.board.dto.BoardCreateResponse;
import com.koorung.board.dto.BoardEditor;
import com.koorung.board.dto.BoardSearch;
import com.koorung.board.service.BoardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/boards")
public class BoardController {

    private final BoardService boardService;

    @PostMapping
    public void post(@RequestBody @Valid BoardCreateRequest request) {

        // 요청 검증
        request.validate();

        // 요청이 성공하면 Board 생성
        boardService.write(request);

        // Case 1. 저장한 데이터의 PK 리턴하기
        //         클라이언트에서는 수신한 PK를 통해 글 조회 API를 통해서 데이터를 수신받음
//        return created.getId();

        // Case 2. 저장한 데이터의 응답 DTO 리턴하기
//        BoardCreateResponse createResponse = BoardCreateResponse.builder()
//                .title(created.getTitle())
//                .contents(created.getContents())
//                .build();

        // Case 3. 저장한 데이터를 ResponseEntity로 리턴하기
//        return new ResponseEntity<>(createResponse, HttpStatus.OK);

        // Case 4. 응답 필요없음 (Best Case)
        //         클라이언트에서 모든 Board 데이터 컨텍스트를 관리할 때...

        // cf) Bad Case -> 서버에서 Fix된 값을 리턴하는 경우...
    }

    @GetMapping("/{boardId}")
    public BoardCreateResponse getBoard(@PathVariable Long boardId) {
        /**
         * 엔티티를 바로 반환하면 안된다!
         *  1. 요구사항의 변화
         *  2. 검증로직 (모든 요청에 공통적인 검증을 적용하는 것은 불가능하다)
         *  기타 etc...
         *
         *  => 엔티티에 서비스의 정책을 "절대" 넣으면 안된다!!!
         */

        /**
         *  => 그럼 엔티티를 BoardCreateResponse로 변환하는 것은
         *     Service에서? 또는 Controller에서?
         *
         *     BoardController -> WebBoardService -> BoardRepository
         *                      ㄴ BoardService (도메인 서비스?)
         *
         *     개인취향이나 회사정책마다 다르긴 하지만 (Web)Service에서 Request, Response에 대해 알지 못하는 것이 좋을듯
         *     https://xlffm3.github.io/spring%20&%20spring%20boot/DTOLayer/ 참조할 것
         */

        // return boardService.get(boardId);
        Board board = boardService.get(boardId);
        return new BoardCreateResponse(board);
    }

    /**
     * 글이 너무 많은 경우
     *      1. DB에 부담이 많이 갈 수 있음
     *      2. DB -> 애플리케이션 서버로 전송하는 시간이나 트래픽비용들이 많이 들 수 있다.
     * -> 페이징 처리 필요
     */
//    @GetMapping
//    public List<BoardCreateResponse> getList() {
//        return boardService.getList().stream()
//                .map(BoardCreateResponse::new)
//                .collect(toList());
//    }

//    @GetMapping
//    public List<BoardCreateResponse> getListPaging(Pageable pageable) {
//        return boardService.getList(pageable).stream()
//                .map(BoardCreateResponse::new)
//                .collect(toList());
//    }

    @GetMapping
    public List<BoardCreateResponse> getList(@ModelAttribute BoardSearch boardSearch) {
        return boardService.getList(boardSearch).stream()
                .map(BoardCreateResponse::new)
                .collect(toList());
    }

    @PatchMapping("/{boardId}")
    public void editBoard(@PathVariable Long boardId, @RequestBody @Valid BoardEditor boardEditor) {
        boardService.edit(boardId, boardEditor);
    }

    @DeleteMapping("/{boardId}")
    public void deleteBoard(@PathVariable Long boardId) {
        boardService.delete(boardId);
    }
}
