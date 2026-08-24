package org.naho.book.usecase;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.naho.file.constant.FileAccessStatus;
import org.naho.file.port.in.UploadFileInputPort;
import org.naho.file.port.out.FileRepositoryPort;
import org.naho.file.result.FileResult;
import org.naho.file.result.StoredFile;
import org.naho.shared.port.out.TransactionPort;

import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UploadBookCoverImageTest {

    @Mock
    private FileRepositoryPort fileRepositoryPort;
    @Mock
    private UploadFileInputPort uploadFileInputPort;
    @Mock
    private TransactionPort transactionPort;

    @InjectMocks
    private UploadBookCoverImageUseCase uploadBookCoverImageUseCase;

    @BeforeEach
    @SuppressWarnings("unchecked")
    void setUp() {
        lenient().when(transactionPort.execute(any(Supplier.class))).thenAnswer(inv -> {
            Supplier<?> s = inv.getArgument(0);
            return s.get();
        });
    }

    @Test
    @DisplayName("UTCID01 - Upload ảnh bìa sách thành công")
    void UTCID01_UploadCoverImage_Success() {
        StoredFile storedFile = mock(StoredFile.class);
        FileResult fileResult = FileResult.builder().id(1L).accessUrl("url").build();

        when(uploadFileInputPort.uploadFileToCloud(storedFile)).thenReturn(fileResult);

        FileResult result = uploadBookCoverImageUseCase.uploadCoverImage(storedFile);

        assertNotNull(result);
        assertEquals(1L, result.id());
        verify(fileRepositoryPort, times(1)).createNewForUpload(storedFile, FileAccessStatus.PUBLIC);
        verify(uploadFileInputPort, times(1)).uploadFileToCloud(storedFile);
    }
}
