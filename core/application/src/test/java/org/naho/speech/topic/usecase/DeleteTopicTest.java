package org.naho.speech.topic.usecase;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.naho.i18n.message.speech.TopicDetailMessageKey;
import org.naho.shared.exception.ApplicationException;
import org.naho.shared.port.out.TransactionPort;
import org.naho.speech.question.port.out.QuestionRepositoryPort;
import org.naho.speech.topic.command.DeleteTopicCommand;
import org.naho.speech.topic.exception.TopicErrorCode;
import org.naho.speech.topic.port.out.TopicRepositoryPort;
import org.naho.topic.model.Topic;
import org.naho.topic.type.QuestionStatus;
import org.naho.topic.type.TopicStatus;
import org.naho.user.type.JLPTLevel;

import java.util.Optional;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeleteTopicTest {

    @Mock
    private TopicRepositoryPort topicRepositoryPort;

    @Mock
    private QuestionRepositoryPort questionRepositoryPort;

    @Mock
    private TransactionPort transactionPort;

    @InjectMocks
    private DeleteTopicUseCase deleteTopicUseCase;

    @BeforeEach
    void setUp() {
        when(transactionPort.execute(any()))
                .thenAnswer(invocation -> {
                    Supplier<?> supplier = invocation.getArgument(0);
                    return supplier.get();
                });
    }

    @Test
    void UTCID01_Should_HardDeleteTopic_Successfully() {
        // Arrange (Given)
        DeleteTopicCommand command = new DeleteTopicCommand(1L, true);

        Topic topic = Topic.builder()
                .id(1L)
                .japaneseName("Topic Japanese Name")
                .description("Topic Description")
                .jlptLevel(JLPTLevel.N5)
                .build();

        when(topicRepositoryPort.findById(command.id()))
                .thenReturn(Optional.of(topic));

        when(questionRepositoryPort.hasAnyQuestionBeenAnsweredInTopic(command.id()))
                .thenReturn(false);

        // Act (When)
        deleteTopicUseCase.deleteTopic(command);

        // Assert (Then)
        verify(topicRepositoryPort, times(1))
                .findById(command.id());

        verify(questionRepositoryPort, times(1))
                .hasAnyQuestionBeenAnsweredInTopic(command.id());

        verify(questionRepositoryPort, times(1))
                .deleteQuestionsByTopicId(command.id());

        verify(topicRepositoryPort, times(1))
                .deleteById(command.id());

        verifyNoMoreInteractions(topicRepositoryPort, questionRepositoryPort);
    }

    @Test
    void UTCID02_Should_SoftDeleteTopic_Successfully() {
        // Arrange (Given)
        DeleteTopicCommand command = new DeleteTopicCommand(1L, true);

        Topic topic = spy(Topic.builder()
                .id(1L)
                .japaneseName("Topic Japanese Name")
                .description("Topic Description")
                .jlptLevel(JLPTLevel.N5)
                .orderIndex(1.0)
                .coverImageFileId(2L)
                .status(TopicStatus.DRAFT)
                .build());

        when(topicRepositoryPort.findById(command.id()))
                .thenReturn(Optional.of(topic));

        when(questionRepositoryPort.hasAnyQuestionBeenAnsweredInTopic(command.id()))
                .thenReturn(true);

        when(topicRepositoryPort.save(any(Topic.class)))
                .thenReturn(topic);

        // Act (When)
        deleteTopicUseCase.deleteTopic(command);

        // Assert (Then)
        verify(topicRepositoryPort, times(1))
                .findById(command.id());

        verify(questionRepositoryPort, times(1))
                .hasAnyQuestionBeenAnsweredInTopic(command.id());

        verify(topic, times(1))
                .update(
                        eq("Topic Japanese Name"),
                        eq("Topic Description"),
                        any(),
                        any(),
                        eq(TopicStatus.ARCHIVE),
                        eq(JLPTLevel.N5),
                        eq(1.0),
                        eq(2L)
                );

        // Saved twice in code (lines 67 and 69)
        verify(topicRepositoryPort, times(2))
                .save(topic);

        verify(questionRepositoryPort, times(1))
                .updateQuestionsStatusByTopicId(command.id(), QuestionStatus.ARCHIVE);

        verifyNoMoreInteractions(topicRepositoryPort, questionRepositoryPort);
    }

    @Test
    void UTCID03_Should_ThrowException_When_ForbiddenUser() {
        // Arrange (Given)
        DeleteTopicCommand command = new DeleteTopicCommand(1L, false);

        // Act (When) & Assert (Then)
        ApplicationException exception = assertThrows(
                ApplicationException.class,
                () -> deleteTopicUseCase.deleteTopic(command)
        );

        assertEquals(TopicErrorCode.TOPIC_DELETE_FORBIDDEN, exception.getErrorCode());
        assertEquals(TopicDetailMessageKey.TOPIC_USER_NOT_HAVE_PERMISSION, exception.getMessage());

        verifyNoInteractions(topicRepositoryPort, questionRepositoryPort);
    }

    @Test
    void UTCID04_Should_ThrowException_When_TopicNotFound() {
        // Arrange (Given)
        DeleteTopicCommand command = new DeleteTopicCommand(999L, true);

        when(topicRepositoryPort.findById(command.id()))
                .thenReturn(Optional.empty());

        // Act (When) & Assert (Then)
        ApplicationException exception = assertThrows(
                ApplicationException.class,
                () -> deleteTopicUseCase.deleteTopic(command)
        );

        assertEquals(TopicErrorCode.TOPIC_NOT_FOUND, exception.getErrorCode());
        assertEquals(TopicDetailMessageKey.TOPIC_ID_NOT_FOUND, exception.getMessage());

        verify(topicRepositoryPort, times(1))
                .findById(command.id());

        verifyNoMoreInteractions(topicRepositoryPort);
        verifyNoInteractions(questionRepositoryPort);
    }

    @Test
    void UTCID05_Should_ThrowException_When_TopicIdNull() {
        // Arrange (Given)
        DeleteTopicCommand command = new DeleteTopicCommand(null, true);

        when(topicRepositoryPort.findById(null))
                .thenReturn(Optional.empty());

        // Act (When) & Assert (Then)
        ApplicationException exception = assertThrows(
                ApplicationException.class,
                () -> deleteTopicUseCase.deleteTopic(command)
        );

        assertEquals(TopicErrorCode.TOPIC_NOT_FOUND, exception.getErrorCode());
        assertEquals(TopicDetailMessageKey.TOPIC_ID_NOT_FOUND, exception.getMessage());

        verify(topicRepositoryPort, times(1))
                .findById(null);

        verifyNoMoreInteractions(topicRepositoryPort);
        verifyNoInteractions(questionRepositoryPort);
    }
}
