package org.naho.file.usecase;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.naho.file.command.FileUploadCommand;
import org.naho.file.exception.FileErrorCode;
import org.naho.file.exception.FileDomainErrorCode;
import org.naho.file.mapper.FileResultMapper;
import org.naho.file.model.File;
import org.naho.file.port.out.FileRepositoryPort;
import org.naho.file.port.out.FileStorageServicePort;
import org.naho.file.result.FileResult;
import org.naho.i18n.message.file.FileDetailMessageKey;
import org.naho.shared.exception.ApplicationException;
import org.naho.shared.exception.DomainException;

import java.io.InputStream;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UploadFileTest {

    @Mock
    private FileStorageServicePort fileStorageServicePort;

    @Mock
    private FileRepositoryPort fileRepositoryPort;

    @Mock
    private FileResultMapper fileResultMapper;

    @InjectMocks
    private FileStorageUseCase fileStorageUseCase;

    @Test
    void UTCID01_Should_UploadFile_Successfully() {
        // Arrange (Given)
        InputStream inputStream = mock(InputStream.class);
        FileUploadCommand command = new FileUploadCommand(
                "avatars",
                "avatar.png",
                inputStream,
                "image/png",
                1024L
        );

        File savedFile = File.builder()
                .id(10L)
                .objectKey("avatars/uuid-timestamp")
                .originalName("avatar.png")
                .contentType("image/png")
                .size(1024L)
                .build();

        FileResult expectedResult = new FileResult(
                10L,
                "avatars/uuid-timestamp",
                "avatar.png",
                "image/png",
                1024L
        );

        when(fileRepositoryPort.save(any(File.class)))
                .thenReturn(savedFile);

        doNothing().when(fileStorageServicePort).upload(
                anyString(),
                eq(inputStream),
                eq("image/png"),
                eq(1024L)
        );

        when(fileResultMapper.domainToResult(savedFile))
                .thenReturn(expectedResult);

        // Act (When)
        FileResult result = fileStorageUseCase.uploadFile(command);

        // Assert (Then)
        assertEquals(expectedResult, result);

        verify(fileRepositoryPort, times(1))
                .save(argThat(file -> 
                        file.getOriginalName().equals("avatar.png") &&
                        file.getContentType().equals("image/png") &&
                        file.getSize().equals(1024L) &&
                        file.getObjectKey().startsWith("avatars/")
                ));

        verify(fileStorageServicePort, times(1))
                .upload(
                        argThat(key -> key.startsWith("avatars/")),
                        eq(inputStream),
                        eq("image/png"),
                        eq(1024L)
                );

        verify(fileResultMapper, times(1))
                .domainToResult(savedFile);

        verifyNoMoreInteractions(fileRepositoryPort, fileStorageServicePort, fileResultMapper);
    }

    @Test
    void UTCID02_Should_ThrowException_When_CommandOrInputStreamIsNull() {
        // Test with null command
        ApplicationException exceptionNullCommand = assertThrows(
                ApplicationException.class,
                () -> fileStorageUseCase.uploadFile(null)
        );
        assertEquals(FileErrorCode.FILE_NOT_VALID, exceptionNullCommand.getErrorCode());
        assertEquals(FileDetailMessageKey.FILE_EMPTY, exceptionNullCommand.getMessage());

        // Test with null inputStream in command
        FileUploadCommand commandNullStream = new FileUploadCommand(
                "avatars",
                "avatar.png",
                null,
                "image/png",
                1024L
        );
        ApplicationException exceptionNullStream = assertThrows(
                ApplicationException.class,
                () -> fileStorageUseCase.uploadFile(commandNullStream)
        );
        assertEquals(FileErrorCode.FILE_NOT_VALID, exceptionNullStream.getErrorCode());
        assertEquals(FileDetailMessageKey.FILE_EMPTY, exceptionNullStream.getMessage());

        // Test folderName empty
        FileUploadCommand commandNullFolder = new FileUploadCommand(
                "",
                "avatar.png",
                mock(InputStream.class),
                "image/png",
                1024L
        );
        ApplicationException exceptionNullFolder = assertThrows(
                ApplicationException.class,
                () -> fileStorageUseCase.uploadFile(commandNullFolder)
        );
        assertEquals(FileErrorCode.FILE_NOT_VALID, exceptionNullFolder.getErrorCode());
        assertEquals(FileDetailMessageKey.FILE_FOLDER_NAME_EMPTY, exceptionNullFolder.getMessage());

        verifyNoInteractions(fileRepositoryPort, fileStorageServicePort, fileResultMapper);
    }

    @Test
    void UTCID03_Should_ThrowException_When_RepositorySaveFails() {
        // Arrange (Given)
        InputStream inputStream = mock(InputStream.class);
        FileUploadCommand command = new FileUploadCommand(
                "avatars",
                "avatar.png",
                inputStream,
                "image/png",
                1024L
        );

        when(fileRepositoryPort.save(any(File.class)))
                .thenThrow(new RuntimeException("Database error"));

        // Act (When) & Assert (Then)
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> fileStorageUseCase.uploadFile(command)
        );

        assertEquals("Database error", exception.getMessage());

        verify(fileRepositoryPort, times(1))
                .save(any(File.class));

        verifyNoMoreInteractions(fileRepositoryPort);
        verifyNoInteractions(fileStorageServicePort, fileResultMapper);
    }

    @Test
    void UTCID04_Should_ThrowException_When_StorageUploadFails() {
        // Arrange (Given)
        InputStream inputStream = mock(InputStream.class);
        FileUploadCommand command = new FileUploadCommand(
                "avatars",
                "avatar.png",
                inputStream,
                "image/png",
                1024L
        );

        File savedFile = File.builder()
                .id(10L)
                .objectKey("avatars/uuid-timestamp")
                .originalName("avatar.png")
                .contentType("image/png")
                .size(1024L)
                .build();

        when(fileRepositoryPort.save(any(File.class)))
                .thenReturn(savedFile);

        doThrow(new RuntimeException("AWS storage error")).when(fileStorageServicePort).upload(
                anyString(),
                eq(inputStream),
                eq("image/png"),
                eq(1024L)
        );

        // Act (When) & Assert (Then)
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> fileStorageUseCase.uploadFile(command)
        );

        assertEquals("AWS storage error", exception.getMessage());

        verify(fileRepositoryPort, times(1))
                .save(any(File.class));

        verify(fileStorageServicePort, times(1))
                .upload(
                        argThat(key -> key.startsWith("avatars/")),
                        eq(inputStream),
                        eq("image/png"),
                        eq(1024L)
                );

        verifyNoMoreInteractions(fileRepositoryPort, fileStorageServicePort);
        verifyNoInteractions(fileResultMapper);
    }

    @Test
    void UTCID05_Should_ThrowException_When_OriginalNameIsEmpty() {
        // Arrange (Given)
        InputStream inputStream = mock(InputStream.class);
        FileUploadCommand commandNullName = new FileUploadCommand(
                "avatars",
                null,
                inputStream,
                "image/png",
                1024L
        );

        FileUploadCommand commandBlankName = new FileUploadCommand(
                "avatars",
                "  ",
                inputStream,
                "image/png",
                1024L
        );

        // Act (When) & Assert (Then) for null originalName
        DomainException exceptionNull = assertThrows(
                DomainException.class,
                () -> fileStorageUseCase.uploadFile(commandNullName)
        );
        assertEquals(FileDomainErrorCode.FILE_ORIGINAL_NAME_EMPTY, exceptionNull.getErrorCode());
        assertEquals(FileDetailMessageKey.FILE_ORIGINAL_NAME_EMPTY, exceptionNull.getMessage());

        // Act (When) & Assert (Then) for blank originalName
        DomainException exceptionBlank = assertThrows(
                DomainException.class,
                () -> fileStorageUseCase.uploadFile(commandBlankName)
        );
        assertEquals(FileDomainErrorCode.FILE_ORIGINAL_NAME_EMPTY, exceptionBlank.getErrorCode());
        assertEquals(FileDetailMessageKey.FILE_ORIGINAL_NAME_EMPTY, exceptionBlank.getMessage());

        verifyNoInteractions(fileRepositoryPort, fileStorageServicePort, fileResultMapper);
    }

    @Test
    void UTCID06_Should_ThrowException_When_SizeZeroOrNegative() {
        // Arrange (Given)
        InputStream inputStream = mock(InputStream.class);
        FileUploadCommand commandZeroSize = new FileUploadCommand(
                "avatars",
                "avatar.png",
                inputStream,
                "image/png",
                0L
        );

        FileUploadCommand commandNegativeSize = new FileUploadCommand(
                "avatars",
                "avatar.png",
                inputStream,
                "image/png",
                -10L
        );

        // Act (When) & Assert (Then) for zero size
        DomainException exceptionZero = assertThrows(
                DomainException.class,
                () -> fileStorageUseCase.uploadFile(commandZeroSize)
        );
        assertEquals(FileDomainErrorCode.FILE_SIZE_INVALID, exceptionZero.getErrorCode());
        assertEquals(FileDetailMessageKey.FILE_EMPTY, exceptionZero.getMessage());

        // Act (When) & Assert (Then) for negative size
        DomainException exceptionNegative = assertThrows(
                DomainException.class,
                () -> fileStorageUseCase.uploadFile(commandNegativeSize)
        );
        assertEquals(FileDomainErrorCode.FILE_SIZE_INVALID, exceptionNegative.getErrorCode());
        assertEquals(FileDetailMessageKey.FILE_EMPTY, exceptionNegative.getMessage());

        verifyNoInteractions(fileRepositoryPort, fileStorageServicePort, fileResultMapper);
    }
}
