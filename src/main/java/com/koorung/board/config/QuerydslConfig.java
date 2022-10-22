package com.koorung.board.config;


import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

// QueryDsl용 JPAQueryFactory @Bean 주입용
@Configuration
public class QuerydslConfig {

    @PersistenceContext
    private EntityManager em;

    // JPAQueryFactory를 @Bean으로 등록하는 방식으로 진행
    @Bean
    public JPAQueryFactory jpaQueryFactory() {
        return new JPAQueryFactory(em);
    }
}
