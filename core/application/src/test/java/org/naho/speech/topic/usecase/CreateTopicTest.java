package org.naho.speech.topic.usecase;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.naho.i18n.message.speech.TopicDetailMessageKey;
import org.naho.shared.exception.ApplicationException;
import org.naho.shared.exception.DomainException;
import org.naho.speech.topic.command.CreateTopicCommand;
import org.naho.speech.topic.exception.TopicErrorCode;
import org.naho.speech.topic.port.out.TopicRepositoryPort;
import org.naho.speech.topic.result.CreateTopicResult;
import org.naho.topic.exception.TopicDomainErrorCode;
import org.naho.topic.model.Topic;
import org.naho.topic.type.TopicStatus;
import org.naho.user.type.JLPTLevel;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateTopicTest {

    @Mock
    private TopicRepositoryPort topicRepositoryPort;

    @InjectMocks
    private CreateTopicUseCase createTopicUseCase;

    @Test
    void UTCID01_Should_CreateTopic_Successfully() {
        // Arrange (Given)
        CreateTopicCommand command = new CreateTopicCommand(
                1L,
                "Topic Japanese Name",
                "Topic Description",
                "name,tokens",
                "desc,tokens",
                JLPTLevel.N5,
                5.0,
                2L
        );

        Topic savedTopic = Topic.builder()
                .id(100L)
                .userId(command.userId())
                .japaneseName(command.japaneseName())
                .description(command.description())
                .japaneseNameTokens(command.nameTokens())
                .japaneseDescriptionTokens(command.descriptionTokens())
                .jlptLevel(command.jlptLevel())
                .orderIndex(5.0)
                .coverImageFileId(command.coverImageFileId())
                .status(TopicStatus.DRAFT)
                .build();

        when(topicRepositoryPort.existsByJapaneseNameAndJlptLevel(command.japaneseName(), command.jlptLevel()))
                .thenReturn(false);

        when(topicRepositoryPort.save(any(Topic.class)))
                .thenReturn(savedTopic);

        // Act (When)
        CreateTopicResult result = createTopicUseCase.createTopic(command);

        // Assert (Then)
        assertEquals(savedTopic.getId(), result.id());
        assertEquals(savedTopic.getUserId(), result.userId());
        assertEquals(savedTopic.getJapaneseName(), result.japaneseName());
        assertEquals(savedTopic.getDescription(), result.japaneseDescription());
        assertEquals(savedTopic.getJapaneseNameTokens(), result.japaneseNameTokens());
        assertEquals(savedTopic.getJapaneseDescriptionTokens(), result.japaneseDescriptionTokens());
        assertEquals(savedTopic.getStatus(), result.status());
        assertEquals(savedTopic.getJlptLevel(), result.jlptLevel());
        assertEquals(savedTopic.getOrderIndex(), result.orderIndex());
        assertEquals(savedTopic.getCoverImageFileId(), result.coverImageFileId());

        verify(topicRepositoryPort, times(1))
                .existsByJapaneseNameAndJlptLevel(command.japaneseName(), command.jlptLevel());

        verify(topicRepositoryPort, times(1))
                .save(argThat(topic -> 
                        topic.getUserId().equals(1L) &&
                        topic.getJapaneseName().equals("Topic Japanese Name") &&
                        topic.getDescription().equals("Topic Description") &&
                        topic.getJapaneseNameTokens().equals("name,tokens") &&
                        topic.getJapaneseDescriptionTokens().equals("desc,tokens") &&
                        topic.getJlptLevel() == JLPTLevel.N5 &&
                        topic.getOrderIndex().equals(5.0) &&
                        topic.getCoverImageFileId().equals(2L) &&
                        topic.getStatus() == TopicStatus.DRAFT
                ));

        verifyNoMoreInteractions(topicRepositoryPort);
    }

    @Test
    void UTCID02_Should_CreateTopic_Successfully_When_OrderIndexNull_And_MaxOrderIndexExists() {
        // Arrange (Given)
        CreateTopicCommand command = new CreateTopicCommand(
                1L,
                "Topic Japanese Name",
                "Topic Description",
                "name,tokens",
                "desc,tokens",
                JLPTLevel.N5,
                null,
                2L
        );

        Topic savedTopic = Topic.builder()
                .id(100L)
                .userId(command.userId())
                .japaneseName(command.japaneseName())
                .description(command.description())
                .japaneseNameTokens(command.nameTokens())
                .japaneseDescriptionTokens(command.descriptionTokens())
                .jlptLevel(command.jlptLevel())
                .orderIndex(11.0) // 10.0 + 1.0
                .coverImageFileId(command.coverImageFileId())
                .status(TopicStatus.DRAFT)
                .build();

        when(topicRepositoryPort.existsByJapaneseNameAndJlptLevel(command.japaneseName(), command.jlptLevel()))
                .thenReturn(false);

        when(topicRepositoryPort.getMaxOrderIndex())
                .thenReturn(10.0);

        when(topicRepositoryPort.save(any(Topic.class)))
                .thenReturn(savedTopic);

        // Act (When)
        CreateTopicResult result = createTopicUseCase.createTopic(command);

        // Assert (Then)
        assertEquals(11.0, result.orderIndex());

        verify(topicRepositoryPort, times(1))
                .existsByJapaneseNameAndJlptLevel(command.japaneseName(), command.jlptLevel());

        verify(topicRepositoryPort, times(1))
                .getMaxOrderIndex();

        verify(topicRepositoryPort, times(1))
                .save(argThat(topic -> topic.getOrderIndex().equals(11.0)));

        verifyNoMoreInteractions(topicRepositoryPort);
    }

    @Test
    void UTCID03_Should_CreateTopic_Successfully_When_OrderIndexNull_And_MaxOrderIndexNull() {
        // Arrange (Given)
        CreateTopicCommand command = new CreateTopicCommand(
                1L,
                "Topic Japanese Name",
                "Topic Description",
                "name,tokens",
                "desc,tokens",
                JLPTLevel.N5,
                null,
                2L
        );

        Topic savedTopic = Topic.builder()
                .id(100L)
                .userId(command.userId())
                .japaneseName(command.japaneseName())
                .description(command.description())
                .japaneseNameTokens(command.nameTokens())
                .japaneseDescriptionTokens(command.descriptionTokens())
                .jlptLevel(command.jlptLevel())
                .orderIndex(1.0) // fallback to 1.0
                .coverImageFileId(command.coverImageFileId())
                .status(TopicStatus.DRAFT)
                .build();

        when(topicRepositoryPort.existsByJapaneseNameAndJlptLevel(command.japaneseName(), command.jlptLevel()))
                .thenReturn(false);

        when(topicRepositoryPort.getMaxOrderIndex())
                .thenReturn(null);

        when(topicRepositoryPort.save(any(Topic.class)))
                .thenReturn(savedTopic);

        // Act (When)
        CreateTopicResult result = createTopicUseCase.createTopic(command);

        // Assert (Then)
        assertEquals(1.0, result.orderIndex());

        verify(topicRepositoryPort, times(1))
                .existsByJapaneseNameAndJlptLevel(command.japaneseName(), command.jlptLevel());

        verify(topicRepositoryPort, times(1))
                .getMaxOrderIndex();

        verify(topicRepositoryPort, times(1))
                .save(argThat(topic -> topic.getOrderIndex().equals(1.0)));

        verifyNoMoreInteractions(topicRepositoryPort);
    }

    @Test
    void UTCID04_Should_ThrowException_When_TopicAlreadyExists() {
        // Arrange (Given)
        CreateTopicCommand command = new CreateTopicCommand(
                1L,
                "Existing Japanese Name",
                "Topic Description",
                "name,tokens",
                "desc,tokens",
                JLPTLevel.N5,
                5.0,
                2L
        );

        when(topicRepositoryPort.existsByJapaneseNameAndJlptLevel(command.japaneseName(), command.jlptLevel()))
                .thenReturn(true);

        // Act (When) & Assert (Then)
        ApplicationException exception = assertThrows(
                ApplicationException.class,
                () -> createTopicUseCase.createTopic(command)
        );

        assertEquals(TopicErrorCode.TOPIC_ALREADY_EXISTS, exception.getErrorCode());
        assertEquals(TopicDetailMessageKey.TOPIC_ALREADY_EXISTS_IN_LEVEL, exception.getMessage());

        verify(topicRepositoryPort, times(1))
                .existsByJapaneseNameAndJlptLevel(command.japaneseName(), command.jlptLevel());

        verifyNoMoreInteractions(topicRepositoryPort);
    }

    @Test
    void UTCID05_Should_ThrowException_When_OrderIndexNegative() {
        // Arrange (Given)
        CreateTopicCommand command = new CreateTopicCommand(
                1L,
                "Topic Japanese Name",
                "Topic Description",
                "name,tokens",
                "desc,tokens",
                JLPTLevel.N5,
                -1.0,
                2L
        );

        when(topicRepositoryPort.existsByJapaneseNameAndJlptLevel(command.japaneseName(), command.jlptLevel()))
                .thenReturn(false);

        // Act (When) & Assert (Then)
        ApplicationException exception = assertThrows(
                ApplicationException.class,
                () -> createTopicUseCase.createTopic(command)
        );

        assertEquals(TopicErrorCode.TOPIC_ORDER_INDEX_INVALID, exception.getErrorCode());
        assertEquals(TopicDetailMessageKey.TOPIC_ORDER_INDEX_INVALID, exception.getMessage());

        verify(topicRepositoryPort, times(1))
                .existsByJapaneseNameAndJlptLevel(command.japaneseName(), command.jlptLevel());

        verifyNoMoreInteractions(topicRepositoryPort);
    }

    @Test
    void UTCID06_Should_CreateTopic_Successfully_When_OrderIndexZero() {
        // Arrange (Given)
        CreateTopicCommand command = new CreateTopicCommand(
                1L,
                "Topic Japanese Name",
                "Topic Description",
                "name,tokens",
                "desc,tokens",
                JLPTLevel.N5,
                0.0,
                2L
        );

        Topic savedTopic = Topic.builder()
                .id(100L)
                .userId(command.userId())
                .japaneseName(command.japaneseName())
                .description(command.description())
                .japaneseNameTokens(command.nameTokens())
                .japaneseDescriptionTokens(command.descriptionTokens())
                .jlptLevel(command.jlptLevel())
                .orderIndex(0.0)
                .coverImageFileId(command.coverImageFileId())
                .status(TopicStatus.DRAFT)
                .build();

        when(topicRepositoryPort.existsByJapaneseNameAndJlptLevel(command.japaneseName(), command.jlptLevel()))
                .thenReturn(false);

        when(topicRepositoryPort.save(any(Topic.class)))
                .thenReturn(savedTopic);

        // Act (When)
        CreateTopicResult result = createTopicUseCase.createTopic(command);

        // Assert (Then)
        assertEquals(0.0, result.orderIndex());

        verify(topicRepositoryPort, times(1))
                .existsByJapaneseNameAndJlptLevel(command.japaneseName(), command.jlptLevel());

        verify(topicRepositoryPort, times(1))
                .save(argThat(topic -> topic.getOrderIndex().equals(0.0)));

        verifyNoMoreInteractions(topicRepositoryPort);
    }

    @Test
    void UTCID07_Should_ThrowException_When_JapaneseNameEmpty() {
        // Arrange (Given)
        CreateTopicCommand command = new CreateTopicCommand(
                1L,
                "", // Blank name
                "Topic Description",
                "name,tokens",
                "desc,tokens",
                JLPTLevel.N5,
                5.0,
                2L
        );

        when(topicRepositoryPort.existsByJapaneseNameAndJlptLevel(command.japaneseName(), command.jlptLevel()))
                .thenReturn(false);

        // Act (When) & Assert (Then)
        DomainException exception = assertThrows(
                DomainException.class,
                () -> createTopicUseCase.createTopic(command)
        );

        assertEquals(TopicDomainErrorCode.TOPIC_NAME_EMPTY, exception.getErrorCode());
        assertEquals(TopicDetailMessageKey.TOPIC_NAME_EMPTY, exception.getMessage());

        verify(topicRepositoryPort, times(1))
                .existsByJapaneseNameAndJlptLevel(command.japaneseName(), command.jlptLevel());

        verifyNoMoreInteractions(topicRepositoryPort);
    }
}
