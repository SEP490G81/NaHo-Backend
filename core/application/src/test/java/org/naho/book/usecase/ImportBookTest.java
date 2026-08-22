package org.naho.book.usecase;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.naho.book.port.out.ImportBookPort;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ImportBookTest {

    @Mock
    private ImportBookPort importBookPort;

    @InjectMocks
    private ImportBookUseCase importBookUseCase;

    @Test
    @DisplayName("UTCID01 - Import dữ liệu Book từ Excel thành công")
    void UTCID01_ImportBookDataFromExcel_Success() {
        InputStream is = new ByteArrayInputStream(new byte[]{1, 2, 3});

        importBookUseCase.importBookDataFromExcel(is);

        verify(importBookPort, times(1)).importBookDataFromExcel(is);
    }
}
