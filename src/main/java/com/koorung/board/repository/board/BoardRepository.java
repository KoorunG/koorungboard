package com.koorung.board.repository.board;

import com.koorung.board.domain.Board;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardRepository extends JpaRepository<Board, Long>, BoardQueryDslRepository {

}
