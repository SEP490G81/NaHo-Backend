package org.naho.book.controller.v1;

import lombok.RequiredArgsConstructor;
import org.naho.book.dto.mapper.BookResponseMapper;
import org.naho.book.dto.response.BookResponse;
import org.naho.book.port.in.ListBooksInputPort;
import org.naho.book.result.BookResult;
import org.naho.i18n.message.book.BookDetailMessageKey;
import org.naho.shared.annotation.ApiResponseMessage;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/books")
@RequiredArgsConstructor
public class BookController {

    private final ListBooksInputPort listBooksInputPort;
    private final BookResponseMapper bookResponseMapper;

    @GetMapping
    @ApiResponseMessage(message = BookDetailMessageKey.BOOK_GET_LIST_SUCCESS)
    public ResponseEntity<List<BookResponse>> listBooks() {
        List<BookResult> results = listBooksInputPort.listBooks();
        List<BookResponse> response = bookResponseMapper.listResultToResponse(results);
        return ResponseEntity.ok(response);
    }

}

