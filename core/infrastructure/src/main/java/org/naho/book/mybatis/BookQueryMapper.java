package org.naho.book.mybatis;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.naho.book.entity.BookEntity;

import java.util.Optional;

@Mapper
public interface BookQueryMapper {
    Double findFirstNodeGlobalOrderIndexById(@Param("bookId") Long bookId);

    Double findLastNodeGlobalOrderIndexById(@Param("bookId") Long bookId);

    Optional<BookEntity> findBySpeakingQuestionId(@Param("speakingQuestionId") Long speakingQuestionId);
}
