package org.naho.learning.usecase;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.naho.chest.exception.ChestErrorCode;
import org.naho.chest.mapper.ChestResultMapper;
import org.naho.chest.model.Chest;
import org.naho.chest.port.out.ChestRepositoryPort;
import org.naho.chest.result.ChestResult;
import org.naho.i18n.message.chest.ChestDetailMessageKey;
import org.naho.i18n.message.learning.LearningPathNodeDetailMessageKey;
import org.naho.i18n.message.question.SpeakingQuestionDetailMessageKey;
import org.naho.i18n.message.question.VocabularyQuestionDetailMessageKey;
import org.naho.learning.command.GetLearningPathNodeDetailCommand;
import org.naho.learning.exception.LearningPathNodeErrorCode;
import org.naho.learning.model.LearningPathNode;
import org.naho.learning.port.out.LearningPathNodeRepositoryPort;
import org.naho.learning.result.LearningPathNodeDetailResult;
import org.naho.learning.type.NodeType;
import org.naho.question.command.FindSpeakingQuestionCommand;
import org.naho.question.exception.SpeakingQuestionErrorCode;
import org.naho.question.exception.VocabularyQuestionErrorCode;
import org.naho.question.mapper.SpeakingQuestionResultMapper;
import org.naho.question.model.VocabularyQuestion;
import org.naho.question.port.in.GetSpeakingQuestionInputPort;
import org.naho.question.port.out.SpeakingQuestionRepositoryPort;
import org.naho.question.port.out.VocabularyQuestionRepositoryPort;
import org.naho.question.result.SpeakingQuestionResult;
import org.naho.shared.exception.ApplicationException;
import org.naho.subscription.port.in.GetActiveSubscriptionInputPort;
import org.naho.vocabulary.mapper.VocabularyResultMapper;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetLearningPathNodeDetailTest {

    @Mock
    private LearningPathNodeRepositoryPort learningPathNodeRepositoryPort;

    @Mock
    private SpeakingQuestionRepositoryPort speakingQuestionRepositoryPort;

    @Mock
    private VocabularyQuestionRepositoryPort vocabularyQuestionRepositoryPort;

    @Mock
    private ChestRepositoryPort chestRepositoryPort;

    @Mock
    private GetActiveSubscriptionInputPort getActiveSubscriptionInputPort;

    @Mock
    private SpeakingQuestionResultMapper speakingQuestionResultMapper;

    @Mock
    private VocabularyResultMapper vocabularyResultMapper;

    @Mock
    private ChestResultMapper chestResultMapper;

    @Mock
    private GetSpeakingQuestionInputPort getSpeakingQuestionInputPort;

    @InjectMocks
    private GetLearningPathNodeDetailUseCase getLearningPathNodeDetailUseCase;

    @Test
    @DisplayName("UTCID01 - Lấy chi tiết node loại SPEAKING_QUESTION thành công")
    void UTCID01_GetSpeakingQuestionNodeSuccess() {
        // Arrange
        Long nodeId = 1L;
        Long sqId = 10L;
        Long userId = 1L;
        LearningPathNode node = mock(LearningPathNode.class);
        when(node.getId()).thenReturn(nodeId);
        when(node.getNodeType()).thenReturn(NodeType.SPEAKING_QUESTION);
        when(node.getSpeakingQuestionId()).thenReturn(sqId);
        when(node.getObjectiveId()).thenReturn(100L);
        when(node.getGlobalOrderIndex()).thenReturn(1.0);
        when(node.getOrderIndex()).thenReturn(1.0);

        SpeakingQuestionResult sqResult = mock(SpeakingQuestionResult.class);

        when(learningPathNodeRepositoryPort.findById(nodeId)).thenReturn(Optional.of(node));
        when(getSpeakingQuestionInputPort.findById(any(FindSpeakingQuestionCommand.class))).thenReturn(sqResult);

        GetLearningPathNodeDetailCommand command = new GetLearningPathNodeDetailCommand(nodeId, userId);

        // Act
        LearningPathNodeDetailResult result = getLearningPathNodeDetailUseCase.getLearningPathNodeDetail(command);

        // Assert
        assertNotNull(result);
        assertEquals(nodeId, result.id());
        assertEquals(NodeType.SPEAKING_QUESTION, result.nodeType());
        assertNotNull(result.speakingQuestion());
        verify(learningPathNodeRepositoryPort, times(1)).findById(nodeId);
        verify(getSpeakingQuestionInputPort, times(1)).findById(any(FindSpeakingQuestionCommand.class));
    }

    @Test
    @DisplayName("UTCID02 - Lấy chi tiết node loại VOCABULARY_QUESTION thành công")
    void UTCID02_GetVocabularyQuestionNodeSuccess() {
        // Arrange
        Long nodeId = 2L;
        Long vqId = 20L;
        LearningPathNode node = mock(LearningPathNode.class);
        when(node.getId()).thenReturn(nodeId);
        when(node.getNodeType()).thenReturn(NodeType.VOCABULARY_QUESTION);
        when(node.getVocabularyQuestionId()).thenReturn(vqId);

        VocabularyQuestion vq = mock(VocabularyQuestion.class);
        when(vq.getId()).thenReturn(vqId);
        when(vq.getVocabularies()).thenReturn(Collections.emptyList());

        when(learningPathNodeRepositoryPort.findById(nodeId)).thenReturn(Optional.of(node));
        when(vocabularyQuestionRepositoryPort.findById(vqId)).thenReturn(Optional.of(vq));

        GetLearningPathNodeDetailCommand command = new GetLearningPathNodeDetailCommand(nodeId, 1L);

        // Act
        LearningPathNodeDetailResult result = getLearningPathNodeDetailUseCase.getLearningPathNodeDetail(command);

        // Assert
        assertNotNull(result);
        assertEquals(nodeId, result.id());
        assertEquals(NodeType.VOCABULARY_QUESTION, result.nodeType());
        assertNotNull(result.vocabularyQuestion());
        verify(learningPathNodeRepositoryPort, times(1)).findById(nodeId);
        verify(vocabularyQuestionRepositoryPort, times(1)).findById(vqId);
    }

    @Test
    @DisplayName("UTCID03 - Lấy chi tiết node loại CHEST thành công")
    void UTCID03_GetChestNodeSuccess() {
        // Arrange
        Long nodeId = 3L;
        Long chestId = 30L;
        LearningPathNode node = mock(LearningPathNode.class);
        when(node.getId()).thenReturn(nodeId);
        when(node.getNodeType()).thenReturn(NodeType.CHEST);
        when(node.getChestId()).thenReturn(chestId);

        Chest chest = mock(Chest.class);
        ChestResult chestResult = mock(ChestResult.class);

        when(learningPathNodeRepositoryPort.findById(nodeId)).thenReturn(Optional.of(node));
        when(chestRepositoryPort.findById(chestId)).thenReturn(Optional.of(chest));
        when(chestResultMapper.domainToResult(chest)).thenReturn(chestResult);

        GetLearningPathNodeDetailCommand command = new GetLearningPathNodeDetailCommand(nodeId, 1L);

        // Act
        LearningPathNodeDetailResult result = getLearningPathNodeDetailUseCase.getLearningPathNodeDetail(command);

        // Assert
        assertNotNull(result);
        assertEquals(nodeId, result.id());
        assertEquals(NodeType.CHEST, result.nodeType());
        assertNotNull(result.chest());
        verify(learningPathNodeRepositoryPort, times(1)).findById(nodeId);
        verify(chestRepositoryPort, times(1)).findById(chestId);
        verify(chestResultMapper, times(1)).domainToResult(chest);
    }

    @Test
    @DisplayName("UTCID04 - Lấy chi tiết node thất bại khi không tìm thấy node với ID tương ứng")
    void UTCID04_LearningPathNodeNotFound() {
        // Arrange
        Long nodeId = 99L;
        when(learningPathNodeRepositoryPort.findById(nodeId)).thenReturn(Optional.empty());

        GetLearningPathNodeDetailCommand command = new GetLearningPathNodeDetailCommand(nodeId, 1L);

        // Act & Assert
        ApplicationException exception = assertThrows(
                ApplicationException.class,
                () -> getLearningPathNodeDetailUseCase.getLearningPathNodeDetail(command)
        );

        assertEquals(LearningPathNodeErrorCode.LEARNING_PATH_NODE_NOT_FOUND, exception.getErrorCode());
        assertEquals(LearningPathNodeDetailMessageKey.LEARNING_PATH_NODE_ID_NOT_FOUND, exception.getMessage());
        verify(learningPathNodeRepositoryPort, times(1)).findById(nodeId);
    }

    @Test
    @DisplayName("UTCID05 - Lấy chi tiết node thất bại khi không tìm thấy câu hỏi nói tương ứng với ID trong node")
    void UTCID05_SpeakingQuestionNotFound() {
        // Arrange
        Long nodeId = 1L;
        Long sqId = 10L;
        LearningPathNode node = mock(LearningPathNode.class);
        when(node.getNodeType()).thenReturn(NodeType.SPEAKING_QUESTION);
        when(node.getSpeakingQuestionId()).thenReturn(sqId);

        when(learningPathNodeRepositoryPort.findById(nodeId)).thenReturn(Optional.of(node));
        when(getSpeakingQuestionInputPort.findById(any(FindSpeakingQuestionCommand.class)))
                .thenThrow(new ApplicationException(
                        SpeakingQuestionErrorCode.SPEAKING_QUESTION_NOT_FOUND,
                        SpeakingQuestionDetailMessageKey.SPEAKING_QUESTION_NOT_FOUND
                ));

        GetLearningPathNodeDetailCommand command = new GetLearningPathNodeDetailCommand(nodeId, 1L);

        // Act & Assert
        ApplicationException exception = assertThrows(
                ApplicationException.class,
                () -> getLearningPathNodeDetailUseCase.getLearningPathNodeDetail(command)
        );

        assertEquals(SpeakingQuestionErrorCode.SPEAKING_QUESTION_NOT_FOUND, exception.getErrorCode());
        assertEquals(SpeakingQuestionDetailMessageKey.SPEAKING_QUESTION_NOT_FOUND, exception.getMessage());
        verify(getSpeakingQuestionInputPort, times(1)).findById(any(FindSpeakingQuestionCommand.class));
    }

    @Test
    @DisplayName("UTCID06 - Lấy chi tiết node thất bại khi không tìm thấy câu hỏi từ vựng tương ứng với ID trong node")
    void UTCID06_VocabularyQuestionNotFound() {
        // Arrange
        Long nodeId = 2L;
        Long vqId = 20L;
        LearningPathNode node = mock(LearningPathNode.class);
        when(node.getNodeType()).thenReturn(NodeType.VOCABULARY_QUESTION);
        when(node.getVocabularyQuestionId()).thenReturn(vqId);

        when(learningPathNodeRepositoryPort.findById(nodeId)).thenReturn(Optional.of(node));
        when(vocabularyQuestionRepositoryPort.findById(vqId)).thenReturn(Optional.empty());

        GetLearningPathNodeDetailCommand command = new GetLearningPathNodeDetailCommand(nodeId, 1L);

        // Act & Assert
        ApplicationException exception = assertThrows(
                ApplicationException.class,
                () -> getLearningPathNodeDetailUseCase.getLearningPathNodeDetail(command)
        );

        assertEquals(VocabularyQuestionErrorCode.VOCABULARY_QUESTION_NOT_FOUND, exception.getErrorCode());
        assertEquals(VocabularyQuestionDetailMessageKey.VOCABULARY_QUESTION_NOT_FOUND, exception.getMessage());
        verify(vocabularyQuestionRepositoryPort, times(1)).findById(vqId);
    }

    @Test
    @DisplayName("UTCID07 - Lấy chi tiết node thất bại khi không tìm thấy rương tương ứng với ID trong node")
    void UTCID07_ChestNotFound() {
        // Arrange
        Long nodeId = 3L;
        Long chestId = 30L;
        LearningPathNode node = mock(LearningPathNode.class);
        when(node.getNodeType()).thenReturn(NodeType.CHEST);
        when(node.getChestId()).thenReturn(chestId);

        when(learningPathNodeRepositoryPort.findById(nodeId)).thenReturn(Optional.of(node));
        when(chestRepositoryPort.findById(chestId)).thenReturn(Optional.empty());

        GetLearningPathNodeDetailCommand command = new GetLearningPathNodeDetailCommand(nodeId, 1L);

        // Act & Assert
        ApplicationException exception = assertThrows(
                ApplicationException.class,
                () -> getLearningPathNodeDetailUseCase.getLearningPathNodeDetail(command)
        );

        assertEquals(ChestErrorCode.CHEST_NOT_FOUND, exception.getErrorCode());
        assertEquals(ChestDetailMessageKey.CHEST_NOT_FOUND, exception.getMessage());
        verify(chestRepositoryPort, times(1)).findById(chestId);
    }
}

