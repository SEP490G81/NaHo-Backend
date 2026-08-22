package org.naho.book.usecase;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.naho.book.command.DeleteTopicCommand;
import org.naho.book.exception.TopicErrorCode;
import org.naho.book.model.Topic;
import org.naho.book.port.out.TopicRepositoryPort;
import org.naho.book.type.TopicStatus;
import org.naho.question.port.out.SpeakingQuestionRepositoryPort;
import org.naho.question.type.QuestionStatus;
import org.naho.shared.exception.ApplicationException;
import org.naho.shared.port.out.TransactionPort;

import java.util.Optional;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeleteTopicTest {

    @Mock
    private TopicRepositoryPort topicRepositoryPort;
    @Mock
    private SpeakingQuestionRepositoryPort questionRepositoryPort;
    @Mock
    private TransactionPort transactionPort;

    @InjectMocks
    private DeleteTopicUseCase deleteTopicUseCase;

    @BeforeEach
    @SuppressWarnings("unchecked")
    void setUp() {
        lenient().when(transactionPort.execute(any(Supplier.class))).thenAnswer(inv -> {
            Supplier<?> s = inv.getArgument(0);
            return s.get();
        });
    }

    @Test
    @DisplayName("UTCID01 - Xóa Topic (Hard delete) thành công khi chưa có câu hỏi nào được trả lời")
    void UTCID01_DeleteTopic_HardDelete_Success() {
        DeleteTopicCommand command = new DeleteTopicCommand(1L, true);
        Topic topic = Topic.builder().id(1L).japaneseName("Name").bookId(10L).orderIndex(1.0).status(TopicStatus.DRAFT).build();

        when(topicRepositoryPort.findById(1L)).thenReturn(Optional.of(topic));
        when(questionRepositoryPort.hasAnySpeakingQuestionBeenAnsweredInTopic(1L)).thenReturn(false);

        deleteTopicUseCase.deleteTopic(command);

        verify(questionRepositoryPort, times(1)).deleteSpeakingQuestionsByTopicId(1L);
        verify(topicRepositoryPort, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("UTCID02 - Xóa Topic (Soft delete) thành công khi đã có câu hỏi được trả lời")
    void UTCID02_DeleteTopic_SoftDelete_Success() {
        DeleteTopicCommand command = new DeleteTopicCommand(1L, true);
        Topic topic = Topic.builder().id(1L).japaneseName("Name").bookId(10L).orderIndex(1.0).status(TopicStatus.DRAFT).build();

        when(topicRepositoryPort.findById(1L)).thenReturn(Optional.of(topic));
        when(questionRepositoryPort.hasAnySpeakingQuestionBeenAnsweredInTopic(1L)).thenReturn(true);

        deleteTopicUseCase.deleteTopic(command);

        verify(topicRepositoryPort, times(1)).save(any(Topic.class));
        verify(questionRepositoryPort, times(1)).updateSpeakingQuestionsStatusByTopicId(1L, QuestionStatus.ARCHIVE);
    }

    @Test
    @DisplayName("UTCID03 - Xóa Topic thất bại do người dùng không phải Admin/Manager")
    void UTCID03_DeleteTopic_Forbidden() {
        DeleteTopicCommand command = new DeleteTopicCommand(1L, false);

        ApplicationException ex = assertThrows(ApplicationException.class, () -> deleteTopicUseCase.deleteTopic(command));
        assertEquals(TopicErrorCode.TOPIC_DELETE_FORBIDDEN, ex.getErrorCode());
    }

    @Test
    @DisplayName("UTCID04 - Xóa Topic thất bại do không tìm thấy Topic")
    void UTCID04_DeleteTopic_NotFound() {
        DeleteTopicCommand command = new DeleteTopicCommand(99L, true);
        when(topicRepositoryPort.findById(99L)).thenReturn(Optional.empty());

        ApplicationException ex = assertThrows(ApplicationException.class, () -> deleteTopicUseCase.deleteTopic(command));
        assertEquals(TopicErrorCode.TOPIC_NOT_FOUND, ex.getErrorCode());
    }
}
