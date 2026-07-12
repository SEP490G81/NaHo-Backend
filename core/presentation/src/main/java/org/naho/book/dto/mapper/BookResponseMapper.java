package org.naho.book.dto.mapper;

import org.mapstruct.Mapper;
import org.naho.book.dto.response.BookResponse;
import org.naho.book.result.BookListItemResult;

import java.util.List;

@Mapper(componentModel = "spring")
public interface BookResponseMapper {

    BookResponse resultToResponse(BookListItemResult result);

    List<BookResponse> listResultToResponse(List<BookListItemResult> results);
}
