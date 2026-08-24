package org.naho.speech.llm.question.usecase;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.naho.file.model.File;
import org.naho.file.port.in.UploadFileInputPort;
import org.naho.file.port.out.FileRepositoryPort;
import org.naho.file.result.FileResult;
import org.naho.file.result.StoredFile;
import org.naho.learning.model.LearningPathNode;
import org.naho.learning.model.UserLearningProgress;
import org.naho.learning.port.out.LearningPathNodeRepositoryPort;
import org.naho.learning.port.out.UserLearningProgressRepositoryPort;
import org.naho.question.port.in.CompleteSpeakingQuestionInputPort;
import org.naho.question.port.out.AnswerHistoryRepositoryPort;
import org.naho.question.port.out.AnswerHistoryResultMapper;
import org.naho.question.result.AnswerHistoryResult;
import org.naho.shared.port.out.TransactionPort;
import org.naho.speech.azure.model.AnswerHistory;
import org.naho.speech.azure.model.SpeechAssessment;
import org.naho.speech.azure.port.out.AzureSpeechServicePort;
import org.naho.speech.azure.port.out.SpeechAssessmentRepositoryPort;
import org.naho.speech.llm.model.question.AiFeedback;
import org.naho.speech.llm.question.command.SpeakingAnalysisCommand;
import org.naho.speech.llm.question.helper.SpeakingAnalysisHelper;
import org.naho.speech.llm.question.port.out.AiFeedbackRepositoryPort;
import org.naho.speech.llm.question.port.out.AiQuestionAnalysisPort;
import org.naho.subscription.model.UserDailyAiUsage;
import org.naho.subscription.port.out.UserDailyAiUsageRepositoryPort;

import java.util.Optional;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SpeakingAnalysisTest {

    @Mock
    private UserDailyAiUsageRepositoryPort userDailyAiUsageRepositoryPort;
    @Mock
    private UploadFileInputPort uploadFileInputPort;
    @Mock
    private TransactionPort transactionPort;
    @Mock
    private AzureSpeechServicePort azureSpeechServicePort;
    @Mock
    private AiFeedbackRepositoryPort aiFeedbackRepositoryPort;
    @Mock
    private AiQuestionAnalysisPort aiQuestionAnalysisPort;
    @Mock
    private SpeakingAnalysisHelper speakingAnalysisHelper;
    @Mock
    private SpeechAssessmentRepositoryPort speechAssessmentRepositoryPort;
    @Mock
    private AnswerHistoryRepositoryPort answerHistoryRepositoryPort;
    @Mock
    private AnswerHistoryResultMapper answerHistoryResultMapper;
    @Mock
    private FileRepositoryPort fileRepositoryPort;
    @Mock
    private LearningPathNodeRepositoryPort learningPathNodeRepositoryPort;
    @Mock
    private UserLearningProgressRepositoryPort userLearningProgressRepositoryPort;
    @Mock
    private CompleteSpeakingQuestionInputPort completeSpeakingQuestionInputPort;

    @InjectMocks
    private SpeakingAnalysisUseCase speakingAnalysisUseCase;

    @BeforeEach
    @SuppressWarnings("unchecked")
    void setUp() {
        lenient().when(transactionPort.execute(any(Supplier.class))).thenAnswer(inv -> {
            Supplier<?> s = inv.getArgument(0);
            return s.get();
        });
    }

    @Test
    @DisplayName("UTCID01 - Phân tích bài nói thành công")
    void UTCID01_AnalyzeSpeaking_Success() {
        SpeakingAnalysisCommand command = mock(SpeakingAnalysisCommand.class);
        LearningPathNode node = mock(LearningPathNode.class);
        UserLearningProgress progress = mock(UserLearningProgress.class);
        UserDailyAiUsage aiUsage = mock(UserDailyAiUsage.class);
        SpeechAssessment speechAssessment = mock(SpeechAssessment.class);
        AiFeedback aiFeedback = mock(AiFeedback.class);
        File file = mock(File.class);
        AnswerHistory answerHistory = mock(AnswerHistory.class);
        AnswerHistoryResult answerHistoryResult = mock(AnswerHistoryResult.class);
        StoredFile storedFile = mock(StoredFile.class);
        FileResult uploadedFile = mock(FileResult.class);

        lenient().when(node.getId()).thenReturn(100L);
        lenient().when(node.getGlobalOrderIndex()).thenReturn(1.0);
        lenient().when(command.speakingQuestionId()).thenReturn(10L);
        lenient().when(command.userId()).thenReturn(1L);
        lenient().when(command.dailySpeakingQuestionEvaluationLimit()).thenReturn(10);
        lenient().when(command.storedFile()).thenReturn(storedFile);

        lenient().when(learningPathNodeRepositoryPort.findBySpeakingQuestionId(10L)).thenReturn(Optional.of(node));
        lenient().when(userLearningProgressRepositoryPort.findByUserId(1L)).thenReturn(Optional.of(progress));
        lenient().when(progress.getFarthestAvailableNodeGlobalOrderIndex()).thenReturn(5.0);
        lenient().when(userDailyAiUsageRepositoryPort.findByUserIdAndUsageDateCreateIfNotExists(any(), any())).thenReturn(aiUsage);
        lenient().when(aiUsage.getSpeakingEvaluationCount()).thenReturn(0);
        lenient().when(azureSpeechServicePort.assessAudio(any())).thenReturn(speechAssessment);
        lenient().when(speechAssessmentRepositoryPort.createNew(speechAssessment)).thenReturn(speechAssessment);
        lenient().when(speechAssessment.getTranscriptText()).thenReturn("Transcript");
        lenient().when(aiQuestionAnalysisPort.analyzeSpeaking(any())).thenReturn("Raw Response");
        lenient().when(speakingAnalysisHelper.parseLlmResponse("Raw Response")).thenReturn(aiFeedback);
        lenient().when(aiFeedbackRepositoryPort.createNew(aiFeedback)).thenReturn(aiFeedback);
        lenient().when(speechAssessment.getAverageScore()).thenReturn(80.0);
        lenient().when(aiFeedback.getAverageScore()).thenReturn(90.0);
        lenient().when(fileRepositoryPort.createNewForUpload(any(), anyBoolean())).thenReturn(file);
        lenient().when(file.getId()).thenReturn(50L);
        lenient().when(answerHistory.getId()).thenReturn(100L);
        lenient().when(answerHistoryRepositoryPort.createNew(any())).thenReturn(answerHistory);
        lenient().when(answerHistoryResultMapper.domainToResult(any())).thenReturn(answerHistoryResult);
        lenient().when(uploadFileInputPort.uploadFileToCloud(any())).thenReturn(uploadedFile);

        AnswerHistoryResult result = speakingAnalysisUseCase.analyzeSpeaking(command);

        assertNotNull(result);
        verify(uploadFileInputPort, times(1)).uploadFileToCloud(storedFile);
    }
}
