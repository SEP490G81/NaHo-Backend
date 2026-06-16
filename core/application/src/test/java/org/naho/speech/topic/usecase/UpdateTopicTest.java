package org.naho.speech.topic.usecase;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.naho.i18n.message.speech.TopicDetailMessageKey;
import org.naho.shared.exception.ApplicationException;
import org.naho.shared.exception.DomainException;
import org.naho.speech.topic.command.UpdateTopicCommand;
import org.naho.speech.topic.exception.TopicErrorCode;
import org.naho.speech.topic.port.out.TopicRepositoryPort;
import org.naho.speech.topic.result.TopicDetailResult;
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
class UpdateTopicTest {

    @Mock
    private TopicRepositoryPort topicRepositoryPort;

    @InjectMocks
    private UpdateTopicUseCase updateTopicUseCase;

    @Test
    void UTCID01_Should_UpdateTopic_Successfully() {
        // Arrange (Given)
        UpdateTopicCommand command = new UpdateTopicCommand(
                1L,
                10L,
                true, // Admin/Manager
                "Updated Name",
                "Updated Description",
                "name,tokens",
                "desc,tokens",
                TopicStatus.DRAFT,
                JLPTLevel.N5,
                5.0,
                2L
        );

        Topic topic = spy(Topic.builder()
                .id(1L)
                .userId(10L)
                .japaneseName("Old Name")
                .description("Old Description")
                .jlptLevel(JLPTLevel.N5)
                .orderIndex(1.0)
                .coverImageFileId(2L)
                .status(TopicStatus.DRAFT)
                .build());

        Topic savedTopic = Topic.builder()
                .id(1L)
                .userId(10L)
                .japaneseName("Updated Name")
                .description("Updated Description")
                .japaneseNameTokens("name,tokens")
                .japaneseDescriptionTokens("desc,tokens")
                .status(TopicStatus.DRAFT)
                .jlptLevel(JLPTLevel.N5)
                .orderIndex(5.0)
                .coverImageFileId(2L)
                .build();

        when(topicRepositoryPort.findById(command.id()))
                .thenReturn(Optional.of(topic));

        when(topicRepositoryPort.existsByJapaneseNameAndJlptLevelExcludeId(command.japaneseName(), command.jlptLevel(), command.id()))
                .thenReturn(false);

        when(topicRepositoryPort.save(any(Topic.class)))
                .thenReturn(savedTopic);

        // Act (When)
        TopicDetailResult result = updateTopicUseCase.updateTopic(command);

        // Assert (Then)
        assertEquals(savedTopic.getId(), result.id());
        assertEquals(savedTopic.getUserId(), result.userId());
        assertEquals(savedTopic.getJapaneseName(), result.japaneseName());
        assertEquals(savedTopic.getDescription(), result.description());
        assertEquals(savedTopic.getJapaneseNameTokens(), result.japaneseNameTokens());
        assertEquals(savedTopic.getJapaneseDescriptionTokens(), result.japaneseDescriptionTokens());
        assertEquals(savedTopic.getStatus(), result.status());
        assertEquals(savedTopic.getJlptLevel(), result.jlptLevel());
        assertEquals(savedTopic.getOrderIndex(), result.orderIndex());
        assertEquals(savedTopic.getCoverImageFileId(), result.coverImageFileId());

        verify(topicRepositoryPort, times(1))
                .findById(command.id());

        verify(topicRepositoryPort, times(1))
                .existsByJapaneseNameAndJlptLevelExcludeId(command.japaneseName(), command.jlptLevel(), command.id());

        verify(topic, times(1))
                .update(
                        "Updated Name",
                        "Updated Description",
                        "name,tokens",
                        "desc,tokens",
                        TopicStatus.DRAFT,
                        JLPTLevel.N5,
                        5.0,
                        2L
                );

        verify(topicRepositoryPort, times(1))
                .save(topic);

        verifyNoMoreInteractions(topicRepositoryPort);
    }

    @Test
    void UTCID02_Should_UpdateTopic_Successfully_When_OrderIndexNull_And_KeepOld() {
        // Arrange (Given)
        UpdateTopicCommand command = new UpdateTopicCommand(
                1L,
                10L, // owner
                false, // not Admin
                "Updated Name",
                "Updated Description",
                "name,tokens",
                "desc,tokens",
                TopicStatus.DRAFT,
                JLPTLevel.N5,
                null, // null orderIndex
                2L
        );

        Topic topic = spy(Topic.builder()
                .id(1L)
                .userId(10L)
                .japaneseName("Old Name")
                .description("Old Description")
                .jlptLevel(JLPTLevel.N5)
                .orderIndex(3.0) // old orderIndex
                .coverImageFileId(2L)
                .status(TopicStatus.DRAFT)
                .build());

        Topic savedTopic = Topic.builder()
                .id(1L)
                .userId(10L)
                .japaneseName("Updated Name")
                .description("Updated Description")
                .japaneseNameTokens("name,tokens")
                .japaneseDescriptionTokens("desc,tokens")
                .status(TopicStatus.DRAFT)
                .jlptLevel(JLPTLevel.N5)
                .orderIndex(3.0) // old orderIndex kept
                .coverImageFileId(2L)
                .build();

        when(topicRepositoryPort.findById(command.id()))
                .thenReturn(Optional.of(topic));

        when(topicRepositoryPort.existsByJapaneseNameAndJlptLevelExcludeId(command.japaneseName(), command.jlptLevel(), command.id()))
                .thenReturn(false);

        when(topicRepositoryPort.save(any(Topic.class)))
                .thenReturn(savedTopic);

        // Act (When)
        TopicDetailResult result = updateTopicUseCase.updateTopic(command);

        // Assert (Then)
        assertEquals(3.0, result.orderIndex());

        verify(topicRepositoryPort, times(1))
                .findById(command.id());

        verify(topicRepositoryPort, times(1))
                .existsByJapaneseNameAndJlptLevelExcludeId(command.japaneseName(), command.jlptLevel(), command.id());

        verify(topic, times(1))
                .update(
                        eq("Updated Name"),
                        eq("Updated Description"),
                        eq("name,tokens"),
                        eq("desc,tokens"),
                        eq(TopicStatus.DRAFT),
                        eq(JLPTLevel.N5),
                        eq(3.0), // checks old index kept
                        eq(2L)
                );

        verify(topicRepositoryPort, times(1))
                .save(topic);

        verifyNoMoreInteractions(topicRepositoryPort);
    }

    @Test
    void UTCID03_Should_ThrowException_When_TopicNotFound() {
        // Arrange (Given)
        UpdateTopicCommand command = new UpdateTopicCommand(
                999L,
                10L,
                true,
                "Updated Name",
                "Updated Description",
                "name,tokens",
                "desc,tokens",
                TopicStatus.DRAFT,
                JLPTLevel.N5,
                5.0,
                2L
        );

        when(topicRepositoryPort.findById(command.id()))
                .thenReturn(Optional.empty());

        // Act (When) & Assert (Then)
        ApplicationException exception = assertThrows(
                ApplicationException.class,
                () -> updateTopicUseCase.updateTopic(command)
        );

        assertEquals(TopicErrorCode.TOPIC_NOT_FOUND, exception.getErrorCode());
        assertEquals(TopicDetailMessageKey.TOPIC_ID_NOT_FOUND, exception.getMessage());

        verify(topicRepositoryPort, times(1))
                .findById(command.id());

        verifyNoMoreInteractions(topicRepositoryPort);
    }

    @Test
    void UTCID04_Should_ThrowException_When_ForbiddenUser() {
        // Arrange (Given)
        UpdateTopicCommand command = new UpdateTopicCommand(
                1L,
                99L, // Not owner (owner is 10L)
                false, // Not admin
                "Updated Name",
                "Updated Description",
                "name,tokens",
                "desc,tokens",
                TopicStatus.DRAFT,
                JLPTLevel.N5,
                5.0,
                2L
        );

        Topic topic = Topic.builder()
                .id(1L)
                .userId(10L) // Owner is 10L
                .japaneseName("Old Name")
                .description("Old Description")
                .jlptLevel(JLPTLevel.N5)
                .build();

        when(topicRepositoryPort.findById(command.id()))
                .thenReturn(Optional.of(topic));

        // Act (When) & Assert (Then)
        ApplicationException exception = assertThrows(
                ApplicationException.class,
                () -> updateTopicUseCase.updateTopic(command)
        );

        assertEquals(TopicErrorCode.TOPIC_UPDATE_FORBIDDEN, exception.getErrorCode());
        assertEquals(TopicDetailMessageKey.TOPIC_USER_NOT_HAVE_PERMISSION, exception.getMessage());

        verify(topicRepositoryPort, times(1))
                .findById(command.id());

        verifyNoMoreInteractions(topicRepositoryPort);
    }

    @Test
    void UTCID05_Should_ThrowException_When_DuplicateTopicExists() {
        // Arrange (Given)
        UpdateTopicCommand command = new UpdateTopicCommand(
                1L,
                10L,
                true,
                "Duplicate Name",
                "Updated Description",
                "name,tokens",
                "desc,tokens",
                TopicStatus.DRAFT,
                JLPTLevel.N5,
                5.0,
                2L
        );

        Topic topic = Topic.builder()
                .id(1L)
                .userId(10L)
                .japaneseName("Old Name")
                .description("Old Description")
                .jlptLevel(JLPTLevel.N5)
                .build();

        when(topicRepositoryPort.findById(command.id()))
                .thenReturn(Optional.of(topic));

        when(topicRepositoryPort.existsByJapaneseNameAndJlptLevelExcludeId(command.japaneseName(), command.jlptLevel(), command.id()))
                .thenReturn(true);

        // Act (When) & Assert (Then)
        ApplicationException exception = assertThrows(
                ApplicationException.class,
                () -> updateTopicUseCase.updateTopic(command)
        );

        assertEquals(TopicErrorCode.TOPIC_ALREADY_EXISTS, exception.getErrorCode());
        assertEquals(TopicDetailMessageKey.TOPIC_ALREADY_EXISTS_IN_LEVEL, exception.getMessage());

        verify(topicRepositoryPort, times(1))
                .findById(command.id());

        verify(topicRepositoryPort, times(1))
                .existsByJapaneseNameAndJlptLevelExcludeId(command.japaneseName(), command.jlptLevel(), command.id());

        verifyNoMoreInteractions(topicRepositoryPort);
    }

    @Test
    void UTCID06_Should_ThrowException_When_OrderIndexNegative() {
        // Arrange (Given)
        UpdateTopicCommand command = new UpdateTopicCommand(
                1L,
                10L,
                true,
                "Updated Name",
                "Updated Description",
                "name,tokens",
                "desc,tokens",
                TopicStatus.DRAFT,
                JLPTLevel.N5,
                -1.0, // invalid orderIndex
                2L
        );

        Topic topic = Topic.builder()
                .id(1L)
                .userId(10L)
                .japaneseName("Old Name")
                .description("Old Description")
                .jlptLevel(JLPTLevel.N5)
                .build();

        when(topicRepositoryPort.findById(command.id()))
                .thenReturn(Optional.of(topic));

        when(topicRepositoryPort.existsByJapaneseNameAndJlptLevelExcludeId(command.japaneseName(), command.jlptLevel(), command.id()))
                .thenReturn(false);

        // Act (When) & Assert (Then)
        ApplicationException exception = assertThrows(
                ApplicationException.class,
                () -> updateTopicUseCase.updateTopic(command)
        );

        assertEquals(TopicErrorCode.TOPIC_ORDER_INDEX_INVALID, exception.getErrorCode());
        assertEquals(TopicDetailMessageKey.TOPIC_ORDER_INDEX_INVALID, exception.getMessage());

        verify(topicRepositoryPort, times(1))
                .findById(command.id());

        verify(topicRepositoryPort, times(1))
                .existsByJapaneseNameAndJlptLevelExcludeId(command.japaneseName(), command.jlptLevel(), command.id());

        verifyNoMoreInteractions(topicRepositoryPort);
    }

    @Test
    void UTCID07_Should_UpdateTopic_Successfully_When_OrderIndexZero() {
        // Arrange (Given)
        UpdateTopicCommand command = new UpdateTopicCommand(
                1L,
                10L,
                true,
                "Updated Name",
                "Updated Description",
                "name,tokens",
                "desc,tokens",
                TopicStatus.DRAFT,
                JLPTLevel.N5,
                0.0, // boundary orderIndex
                2L
        );

        Topic topic = spy(Topic.builder()
                .id(1L)
                .userId(10L)
                .japaneseName("Old Name")
                .description("Old Description")
                .jlptLevel(JLPTLevel.N5)
                .orderIndex(1.0)
                .coverImageFileId(2L)
                .status(TopicStatus.DRAFT)
                .build());

        Topic savedTopic = Topic.builder()
                .id(1L)
                .userId(10L)
                .japaneseName("Updated Name")
                .description("Updated Description")
                .japaneseNameTokens("name,tokens")
                .japaneseDescriptionTokens("desc,tokens")
                .status(TopicStatus.DRAFT)
                .jlptLevel(JLPTLevel.N5)
                .orderIndex(0.0)
                .coverImageFileId(2L)
                .build();

        when(topicRepositoryPort.findById(command.id()))
                .thenReturn(Optional.of(topic));

        when(topicRepositoryPort.existsByJapaneseNameAndJlptLevelExcludeId(command.japaneseName(), command.jlptLevel(), command.id()))
                .thenReturn(false);

        when(topicRepositoryPort.save(any(Topic.class)))
                .thenReturn(savedTopic);

        // Act (When)
        TopicDetailResult result = updateTopicUseCase.updateTopic(command);

        // Assert (Then)
        assertEquals(0.0, result.orderIndex());

        verify(topicRepositoryPort, times(1))
                .findById(command.id());

        verify(topicRepositoryPort, times(1))
                .existsByJapaneseNameAndJlptLevelExcludeId(command.japaneseName(), command.jlptLevel(), command.id());

        verify(topic, times(1))
                .update(
                        eq("Updated Name"),
                        eq("Updated Description"),
                        eq("name,tokens"),
                        eq("desc,tokens"),
                        eq(TopicStatus.DRAFT),
                        eq(JLPTLevel.N5),
                        eq(0.0),
                        eq(2L)
                );

        verify(topicRepositoryPort, times(1))
                .save(topic);

        verifyNoMoreInteractions(topicRepositoryPort);
    }

    @Test
    void UTCID08_Should_ThrowException_When_JapaneseNameEmpty() {
        // Arrange (Given)
        UpdateTopicCommand command = new UpdateTopicCommand(
                1L,
                10L,
                true,
                "", // Blank name
                "Updated Description",
                "name,tokens",
                "desc,tokens",
                TopicStatus.DRAFT,
                JLPTLevel.N5,
                5.0,
                2L
        );

        Topic topic = Topic.builder()
                .id(1L)
                .userId(10L)
                .japaneseName("Old Name")
                .description("Old Description")
                .jlptLevel(JLPTLevel.N5)
                .build();

        when(topicRepositoryPort.findById(command.id()))
                .thenReturn(Optional.of(topic));

        when(topicRepositoryPort.existsByJapaneseNameAndJlptLevelExcludeId(command.japaneseName(), command.jlptLevel(), command.id()))
                .thenReturn(false);

        // Act (When) & Assert (Then)
        DomainException exception = assertThrows(
                DomainException.class,
                () -> updateTopicUseCase.updateTopic(command)
        );

        assertEquals(TopicDomainErrorCode.TOPIC_NAME_EMPTY, exception.getErrorCode());
        assertEquals(TopicDetailMessageKey.TOPIC_NAME_EMPTY, exception.getMessage());

        verify(topicRepositoryPort, times(1))
                .findById(command.id());

        verify(topicRepositoryPort, times(1))
                .existsByJapaneseNameAndJlptLevelExcludeId(command.japaneseName(), command.jlptLevel(), command.id());

        verifyNoMoreInteractions(topicRepositoryPort);
    }
}
