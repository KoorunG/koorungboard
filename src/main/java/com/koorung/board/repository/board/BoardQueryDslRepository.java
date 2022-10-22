package com.koorung.board.repository.board;

import com.koorung.board.domain.Board;
import com.koorung.board.dto.BoardSearch;

import java.util.List;

public interface BoardQueryDslRepository {
    List<Board> getList(BoardSearch boardSearch);
}
