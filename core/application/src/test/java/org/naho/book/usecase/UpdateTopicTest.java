package org.naho.book.usecase;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.naho.book.command.UpdateTopicCommand;
import org.naho.book.exception.TopicErrorCode;
import org.naho.book.model.Topic;
import org.naho.book.port.out.TopicRepositoryPort;
import org.naho.book.result.TopicDetailResult;
import org.naho.book.type.TopicStatus;
import org.naho.furigana.port.out.FuriganaGenerationPort;
import org.naho.shared.exception.ApplicationException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateTopicTest {

    @Mock
    private TopicRepositoryPort topicRepositoryPort;
    @Mock
    private FuriganaGenerationPort furiganaGenerationPort;

    @InjectMocks
    private UpdateTopicUseCase updateTopicUseCase;

    @Test
    @DisplayName("UTCID01 - Cập nhật Topic thành công")
    void UTCID01_UpdateTopic_Success() {
        UpdateTopicCommand command = new UpdateTopicCommand(1L, 1L, true, "New Topic", "New Desc", "Vi", "En", TopicStatus.DRAFT, 10L);
        Topic existingTopic = Topic.builder().id(1L).bookId(100L).japaneseName("Old Topic").orderIndex(1.0).build();

        when(topicRepositoryPort.findById(1L)).thenReturn(Optional.of(existingTopic));
        when(topicRepositoryPort.existsByJapaneseNameAndBookIdExcludeId("New Topic", 100L, 1L)).thenReturn(false);
        when(furiganaGenerationPort.generateFuriganaMarkup(any())).thenReturn("Markup");
        when(topicRepositoryPort.save(any(Topic.class))).thenReturn(existingTopic);

        TopicDetailResult result = updateTopicUseCase.updateTopic(command);

        assertNotNull(result);
        assertEquals(1L, result.id());
        verify(topicRepositoryPort, times(1)).save(any(Topic.class));
    }

    @Test
    @DisplayName("UTCID02 - Cập nhật Topic thất bại do không có quyền")
    void UTCID02_UpdateTopic_Forbidden() {
        UpdateTopicCommand command = new UpdateTopicCommand(1L, 1L, false, "Topic", "Desc", "Vi", "En", TopicStatus.DRAFT, null);
        Topic existingTopic = Topic.builder().id(1L).bookId(100L).japaneseName("Topic").orderIndex(1.0).build();

        when(topicRepositoryPort.findById(1L)).thenReturn(Optional.of(existingTopic));

        ApplicationException ex = assertThrows(ApplicationException.class, () -> updateTopicUseCase.updateTopic(command));
        assertEquals(TopicErrorCode.TOPIC_UPDATE_FORBIDDEN, ex.getErrorCode());
    }

    @Test
    @DisplayName("UTCID03 - Cập nhật Topic thất bại do trùng tên JapaneseName")
    void UTCID03_UpdateTopic_AlreadyExists() {
        UpdateTopicCommand command = new UpdateTopicCommand(1L, 1L, true, "Dup", "Desc", "Vi", "En", TopicStatus.DRAFT, null);
        Topic existingTopic = Topic.builder().id(1L).bookId(100L).japaneseName("Topic").orderIndex(1.0).build();

        when(topicRepositoryPort.findById(1L)).thenReturn(Optional.of(existingTopic));
        when(topicRepositoryPort.existsByJapaneseNameAndBookIdExcludeId("Dup", 100L, 1L)).thenReturn(true);

        ApplicationException ex = assertThrows(ApplicationException.class, () -> updateTopicUseCase.updateTopic(command));
        assertEquals(TopicErrorCode.TOPIC_ALREADY_EXISTS, ex.getErrorCode());
    }
}
