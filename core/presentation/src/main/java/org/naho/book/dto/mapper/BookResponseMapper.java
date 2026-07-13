package org.naho.book.dto.mapper;

import org.mapstruct.Mapper;
import org.naho.book.dto.response.BookResponse;
import org.naho.book.result.BookResult;
import org.naho.file.dto.mapper.FileResponseMapper;

import java.util.List;

@Mapper(
        componentModel = "spring",
        uses = {FileResponseMapper.class}
)
public interface BookResponseMapper {

    BookResponse resultToResponse(BookResult result);

    List<BookResponse> listResultToResponse(List<BookResult> results);
}
