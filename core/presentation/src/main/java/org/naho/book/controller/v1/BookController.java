package org.naho.book.controller.v1;

import lombok.RequiredArgsConstructor;
import org.naho.book.command.GetBookDetailCommand;
import org.naho.book.dto.mapper.BookResponseMapper;
import org.naho.book.dto.response.BookResponse;
import org.naho.book.port.in.GetBookDetailInputPort;
import org.naho.book.port.in.ListBooksInputPort;
import org.naho.book.result.BookResult;
import org.naho.i18n.message.book.BookDetailMessageKey;
import org.naho.shared.annotation.ApiResponseMessage;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/books")
@RequiredArgsConstructor
public class BookController {

    private final ListBooksInputPort listBooksInputPort;
    private final GetBookDetailInputPort getBookDetailInputPort;
    private final BookResponseMapper bookResponseMapper;

    @GetMapping
    @ApiResponseMessage(message = BookDetailMessageKey.BOOK_GET_LIST_SUCCESS)
    public ResponseEntity<List<BookResponse>> listBooks() {
        List<BookResult> results = listBooksInputPort.listBooks();
        List<BookResponse> response = bookResponseMapper.listResultToResponse(results);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{bookId}")
    @ApiResponseMessage(message = BookDetailMessageKey.BOOK_GET_DETAIL_SUCCESS)
    public ResponseEntity<BookResponse> getBookDetail(@PathVariable Long bookId) {
        BookResult result = getBookDetailInputPort.getBookDetail(new GetBookDetailCommand(bookId));
        BookResponse response = bookResponseMapper.resultToResponse(result);
        return ResponseEntity.ok(response);
    }

}

