package org.naho.book.mybatis;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface BookQueryMapper {
    Double findFirstNodeGlobalOrderIndexById(@Param("bookId") Long bookId);

    Double findLastNodeGlobalOrderIndexById(@Param("bookId") Long bookId);
}
