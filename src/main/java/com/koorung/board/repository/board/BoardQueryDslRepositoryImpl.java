package com.koorung.board.repository.board;

import com.koorung.board.domain.Board;
import com.koorung.board.dto.BoardSearch;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import javax.persistence.EntityManager;
import java.util.List;

import static com.koorung.board.domain.QBoard.board;

/**
 * Querydsl 기능 추가용 커스텀 리포지토리
 */
@RequiredArgsConstructor
public class BoardQueryDslRepositoryImpl implements BoardQueryDslRepository {

    // 1. QuerydslConfig에서 @Bean으로 등록한 JPAQueryFactory 주입
    private final JPAQueryFactory queryFactory;

    // 2. 이렇게 생성자에서 em을 전달하여 객체를 주입받아도 된다.
//    public BoardQueryDslRepositoryImpl(EntityManager em) {
//        this.queryFactory = new JPAQueryFactory(em);
//    }

    @Override
    public List<Board> getList(BoardSearch boardSearch) {
        return queryFactory
                .select(board)
                .from(board)
                .limit(boardSearch.getSize())                                      // 10개만 조회
                .offset(boardSearch.getOffset())                  // 1페이지 : 0부터 시작, 2페이지 : 10부터 시작 ....
                .orderBy(board.id.desc())
                .fetch();
    }
}

