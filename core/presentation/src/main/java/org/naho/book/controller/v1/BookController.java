package org.naho.book.controller.v1;

import lombok.RequiredArgsConstructor;
import org.naho.book.command.GetBookDetailCommand;
import org.naho.book.dto.mapper.BookResponseMapper;
import org.naho.book.dto.response.BookResponse;
import org.naho.book.exception.BookErrorCode;
import org.naho.book.port.in.GetBookDetailInputPort;
import org.naho.book.port.in.ImportBookInputPort;
import org.naho.book.port.in.ListBooksInputPort;
import org.naho.book.result.BookResult;
import org.naho.i18n.message.book.BookDetailMessageKey;
import org.naho.shared.annotation.ApiResponseMessage;
import org.naho.shared.exception.PresentationException;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/books")
@RequiredArgsConstructor
public class BookController {

    private final ListBooksInputPort listBooksInputPort;
    private final GetBookDetailInputPort getBookDetailInputPort;
    private final ImportBookInputPort importBookInputPort;
    private final BookResponseMapper bookResponseMapper;

    @PostMapping(value = "/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ApiResponseMessage(message = BookDetailMessageKey.BOOK_IMPORT_SUCCESS)
    public ResponseEntity<Void> importBookDataFromExcel(@RequestPart("file") MultipartFile file) {
        try {
            importBookInputPort.importBookDataFromExcel(file.getInputStream());
            return ResponseEntity.ok().build();
        } catch (IOException e) {
            throw new PresentationException(
                    BookErrorCode.BOOK_IMPORT_FAILED,
                    BookDetailMessageKey.BOOK_IMPORT_FAILED,
                    e.getMessage()
            );
        }
    }

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

