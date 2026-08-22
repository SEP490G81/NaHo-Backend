package org.naho.question.usecase;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.naho.file.model.File;
import org.naho.file.port.out.FileRepositoryPort;
import org.naho.file.port.out.FileStorageServicePort;
import org.naho.question.port.out.AnswerHistoryRepositoryPort;
import org.naho.question.port.out.AnswerHistoryResultMapper;
import org.naho.question.result.AnswerHistoryListItemResult;
import org.naho.shared.exception.ApplicationException;
import org.naho.speech.azure.model.AnswerHistory;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CrudAnswerHistoryTest {

    @Mock
    private AnswerHistoryRepositoryPort answerHistoryRepositoryPort;
    @Mock
    private FileRepositoryPort fileRepositoryPort;
    @Mock
    private FileStorageServicePort fileStorageServicePort;
    @Mock
    private AnswerHistoryResultMapper answerHistoryResultMapper;

    @InjectMocks
    private CrudAnswerHistoryUseCase crudAnswerHistoryUseCase;

    @Test
    @DisplayName("UTCID01 - Tạo presigned url cho audio file thành công")
    void UTCID01_GenerateAudioFilePresignedUrl_Success() {
        AnswerHistory history = mock(AnswerHistory.class);
        File file = mock(File.class);

        when(history.getUserId()).thenReturn(1L);
        when(history.getAudioFileId()).thenReturn(10L);
        when(answerHistoryRepositoryPort.findById(100L)).thenReturn(Optional.of(history));
        when(fileRepositoryPort.findById(10L)).thenReturn(Optional.of(file));
        when(fileStorageServicePort.generatePresignedUrl(file)).thenReturn("http://presigned.url");

        String url = crudAnswerHistoryUseCase.generateAudioFilePresignedUrl(100L, 1L);

        assertNotNull(url);
        assertEquals("http://presigned.url", url);
    }

    @Test
    @DisplayName("UTCID02 - Thất bại khi answerHistoryId null")
    void UTCID02_GenerateAudioFilePresignedUrl_IdNull() {
        assertThrows(ApplicationException.class, () -> crudAnswerHistoryUseCase.generateAudioFilePresignedUrl(null, 1L));
    }

    @Test
    @DisplayName("UTCID03 - Lấy lịch sử trả lời theo speakingQuestionId thành công")
    void UTCID03_FindAllBySpeakingQuestionIdAndUserId_Success() {
        AnswerHistory history = mock(AnswerHistory.class);
        AnswerHistoryListItemResult itemResult = mock(AnswerHistoryListItemResult.class);

        when(answerHistoryRepositoryPort.findAllBySpeakingQuestionIdAndUserId(10L, 1L)).thenReturn(List.of(history));
        when(answerHistoryResultMapper.domainToListItemResult(any())).thenReturn(itemResult);

        List<AnswerHistoryListItemResult> results = crudAnswerHistoryUseCase.findAllBySpeakingQuestionIdAndUserId(10L, 1L);

        assertNotNull(results);
        assertEquals(1, results.size());
    }
}
