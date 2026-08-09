package org.naho.file.usecase;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.naho.file.constant.FileProperties;
import org.naho.file.exception.FileErrorCode;
import org.naho.file.mapper.StoredFileMapper;
import org.naho.file.model.File;
import org.naho.file.port.out.FileRepositoryPort;
import org.naho.file.port.out.FileStorageServicePort;
import org.naho.file.result.StoredFile;
import org.naho.i18n.message.file.FileDetailMessageKey;
import org.naho.shared.exception.ApplicationException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RetryUploadFileToCloudTest {

    @Mock
    private FileRepositoryPort fileRepositoryPort;

    @Mock
    private FileStorageServicePort fileStorageServicePort;

    @Mock
    private StoredFileMapper storedFileMapper;

    @InjectMocks
    private UploadFileUseCase uploadFileUseCase;

    @Test
    @DisplayName("UTCID01 - Thử lại upload file lên cloud thành công khi retryCount trong giới hạn cho phép")
    void UTCID01_RetryUploadFileToCloudSuccess() {
        // Arrange
        String objectKey = "files/video.mp4";
        File file = mock(File.class);
        StoredFile storedFile = mock(StoredFile.class);

        when(file.incrementRetryCount()).thenReturn(1);
        when(file.getRetryCount()).thenReturn(1);
        when(file.getObjectKey()).thenReturn(objectKey);
        when(storedFileMapper.domainToStoredFile(file)).thenReturn(storedFile);

        // Act
        uploadFileUseCase.retryUploadFileToCloud(file);

        // Assert
        verify(file, times(1)).incrementRetryCount();
        verify(storedFileMapper, times(1)).domainToStoredFile(file);
        verify(fileStorageServicePort, times(1)).uploadFileToCloud(storedFile);
        verify(fileStorageServicePort, times(1)).deleteFileInLocal(objectKey);
        verify(file).markCompleted();
        verify(fileRepositoryPort, times(1)).save(file);
    }

    @Test
    @DisplayName("UTCID02 - Thử lại upload file lên cloud thất bại khi file bị null")
    void UTCID02_FileNull() {
        // Arrange
        File file = null;

        // Act & Assert
        ApplicationException exception = assertThrows(
                ApplicationException.class,
                () -> uploadFileUseCase.retryUploadFileToCloud(file)
        );

        assertEquals(FileErrorCode.FILE_NOT_VALID, exception.getErrorCode());
        assertEquals(FileDetailMessageKey.FILE_NOT_VALID, exception.getMessage());
        verify(fileStorageServicePort, never()).uploadFileToCloud(any());
        verify(fileRepositoryPort, never()).save(any());
    }

    @Test
    @DisplayName("UTCID03 - Thử lại upload file lên cloud vượt quá số lần retry cho phép")
    void UTCID03_RetryLimitExceeded() {
        // Arrange
        File file = mock(File.class);
        int maxRetry = FileProperties.MAX_RETRY_COUNT;
        int exceededRetry = maxRetry + 1;

        when(file.incrementRetryCount()).thenReturn(exceededRetry);
        when(file.getRetryCount()).thenReturn(exceededRetry);

        // Act
        uploadFileUseCase.retryUploadFileToCloud(file);

        // Assert
        verify(file, times(1)).incrementRetryCount();
        verify(file).markRetryLimitExceeded();
        verify(fileStorageServicePort, never()).uploadFileToCloud(any());
        verify(fileRepositoryPort, times(1)).save(file);
    }

    @Test
    @DisplayName("UTCID04 - Thử lại upload file lên cloud gặp lỗi ngoại lệ, cập nhật thời điểm retry tiếp theo")
    void UTCID04_CloudStorageError() {
        // Arrange
        File file = mock(File.class);
        StoredFile storedFile = mock(StoredFile.class);

        when(file.incrementRetryCount()).thenReturn(2);
        when(file.getRetryCount()).thenReturn(2);
        when(storedFileMapper.domainToStoredFile(file)).thenReturn(storedFile);
        doThrow(new RuntimeException("Cloud error"))
                .when(fileStorageServicePort).uploadFileToCloud(storedFile);

        // Act
        uploadFileUseCase.retryUploadFileToCloud(file);

        // Assert
        verify(file, times(1)).incrementRetryCount();
        verify(fileStorageServicePort, times(1)).uploadFileToCloud(storedFile);
        verify(fileStorageServicePort, never()).deleteFileInLocal(any());
        verify(file).setNextRetryAt(any());
        verify(fileRepositoryPort, times(1)).save(file);
    }
}
