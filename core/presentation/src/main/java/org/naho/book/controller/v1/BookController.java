package org.naho.book.controller.v1;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.naho.book.command.GetBookDetailCommand;
import org.naho.book.dto.mapper.BookRequestMapper;
import org.naho.book.dto.mapper.BookResponseMapper;
import org.naho.book.dto.request.UpdateBookRequest;
import org.naho.book.dto.response.BookResponse;
import org.naho.book.exception.BookErrorCode;
import org.naho.book.port.in.*;
import org.naho.book.result.BookResult;
import org.naho.file.constant.FileAccessStatus;
import org.naho.file.constant.FileFolderConstant;
import org.naho.file.dto.mapper.FileResponseMapper;
import org.naho.file.dto.response.FileResponse;
import org.naho.file.exception.FileErrorCode;
import org.naho.file.port.out.FileStorageServicePort;
import org.naho.file.port.out.FileValidatorPort;
import org.naho.file.result.FileResult;
import org.naho.file.result.StoredFile;
import org.naho.i18n.message.book.BookDetailMessageKey;
import org.naho.i18n.message.file.FileDetailMessageKey;
import org.naho.shared.annotation.ApiResponseMessage;
import org.naho.shared.exception.PresentationException;
import org.naho.user.port.out.RoleRepositoryPort;
import org.naho.user.result.AccessTokenPayload;
import org.naho.user.type.RoleName;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    private final UpdateBookInputPort updateBookInputPort;
    private final BookResponseMapper bookResponseMapper;
    private final BookRequestMapper bookRequestMapper;
    private final RoleRepositoryPort roleRepositoryPort;
    private final FileValidatorPort fileValidatorPort;
    private final FileStorageServicePort fileStorageServicePort;
    private final UploadBookCoverImageInputPort uploadBookCoverImageInputPort;
    private final FileResponseMapper fileResponseMapper;

    // ROLE: ADMIN, CONTENT_MANAGER
    @PreAuthorize("hasAnyRole('ADMIN', 'CONTENT_MANAGER')")
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

    // ROLE: ADMIN, CONTENT_MANAGER, LEANER
    @PreAuthorize("hasAnyRole('ADMIN', 'CONTENT_MANAGER', 'LEARNER')")
    @GetMapping
    @ApiResponseMessage(message = BookDetailMessageKey.BOOK_GET_LIST_SUCCESS)
    public ResponseEntity<List<BookResponse>> listBooks() {
        List<BookResult> results = listBooksInputPort.listBooks();
        List<BookResponse> response = bookResponseMapper.listResultToResponse(results);
        return ResponseEntity.ok(response);
    }

    // ROLE: ADMIN, CONTENT_MANAGER, LEANER
    @PreAuthorize("hasAnyRole('ADMIN', 'CONTENT_MANAGER', 'LEARNER')")
    @GetMapping("/{bookId}")
    @ApiResponseMessage(message = BookDetailMessageKey.BOOK_GET_DETAIL_SUCCESS)
    public ResponseEntity<BookResponse> getBookDetail(@PathVariable Long bookId) {
        BookResult result = getBookDetailInputPort.getBookDetail(new GetBookDetailCommand(bookId));
        BookResponse response = bookResponseMapper.resultToResponse(result);
        return ResponseEntity.ok(response);
    }

    // ROLE: CONTENT_MANAGER
    @PreAuthorize("hasAnyRole('CONTENT_MANAGER', 'ADMIN')")
    @PutMapping("/{bookId}")
    @ApiResponseMessage(message = BookDetailMessageKey.BOOK_UPDATE_SUCCESS)
    public ResponseEntity<BookResponse> updateBook(
            @PathVariable Long bookId,
            @RequestBody @Valid UpdateBookRequest request,
            @AuthenticationPrincipal AccessTokenPayload payload
    ) {
        List<String> roleSet = roleRepositoryPort.findRoleNamesByUserId(payload.userId());
        boolean isAdminOrManager =
                roleSet.contains(RoleName.ADMIN.name()) ||
                        roleSet.contains(RoleName.CONTENT_MANAGER.name());

        var command = bookRequestMapper.toUpdateCommand(request, bookId, payload.userId(), isAdminOrManager);
        BookResult result = updateBookInputPort.updateBook(command);
        BookResponse response = bookResponseMapper.resultToResponse(result);
        return ResponseEntity.ok(response);
    }

    // ROLE: CONTENT_MANAGER
    @PreAuthorize("hasAnyRole('CONTENT_MANAGER', 'ADMIN')")
    @PostMapping(value = "/cover-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ApiResponseMessage(message = BookDetailMessageKey.BOOK_COVER_UPLOAD_SUCCESS)
    public ResponseEntity<FileResponse> uploadCoverImage(
            @RequestPart("file") MultipartFile file
    ) {
        try {
            fileValidatorPort.validateImageFile(file.getBytes());
            StoredFile storedFile = fileStorageServicePort.saveFileToLocal(
                    file,
                    FileFolderConstant.BOOKS,
                    FileAccessStatus.PUBLIC
            );

            FileResult result = uploadBookCoverImageInputPort.uploadCoverImage(storedFile);
            FileResponse response = fileResponseMapper.resultToResponse(result);

            return ResponseEntity.ok(response);
        } catch (IOException e) {
            throw new PresentationException(
                    FileErrorCode.FILE_UPLOAD_FAILED,
                    FileDetailMessageKey.FILE_UPLOAD_FAILED,
                    e.getMessage()
            );
        }
    }
}

