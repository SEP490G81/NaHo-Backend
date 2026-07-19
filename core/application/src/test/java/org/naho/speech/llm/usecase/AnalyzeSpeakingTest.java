package org.naho.speech.llm.usecase;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.naho.book.model.Topic;
import org.naho.book.port.out.TopicRepositoryPort;
import org.naho.file.command.FileUploadCommand;
import org.naho.file.port.in.FileStorageInputPort;
import org.naho.file.port.out.FileRepositoryPort;
import org.naho.file.result.FileResult;
import org.naho.furigana.port.out.FuriganaGenerationPort;
import org.naho.i18n.message.question.SpeakingQuestionDetailMessageKey;
import org.naho.i18n.message.user.UserDetailMessageKey;
import org.naho.question.exception.SpeakingQuestionErrorCode;
import org.naho.question.model.SpeakingQuestion;
import org.naho.question.port.out.SpeakingQuestionRepositoryPort;
import org.naho.shared.exception.ApplicationException;
import org.naho.speech.azure.command.SpeechAssessmentCommand;
import org.naho.speech.azure.port.out.AzureSpeechServicePort;
import org.naho.speech.llm.command.SpeakingAnalysisCommand;
import org.naho.speech.llm.port.out.AiAnalysisPort;
import org.naho.speech.llm.port.out.AnswerHistoryRepositoryPort;
import org.naho.speech.llm.result.SpeakingAnalysisResult;
import org.naho.speech.model.AnswerHistory;
import org.naho.speech.model.SpeechAssessment;
import org.naho.speech.model.WordAssessment;
import org.naho.speech.type.SpeechAssessmentErrorType;
import org.naho.user.exception.UserErrorCode;
import org.naho.user.model.User;
import org.naho.user.port.out.UserRepositoryPort;
import org.naho.user.type.UserStatus;

import java.time.Instant;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AnalyzeSpeakingTest {

        @Mock
        private UserRepositoryPort userRepositoryPort;

        @Mock
        private SpeakingQuestionRepositoryPort questionRepositoryPort;

        @Mock
        private FileStorageInputPort fileStorageInputPort;

        @Mock
        private FileRepositoryPort fileRepositoryPort;

        @Mock
        private AnswerHistoryRepositoryPort answerHistoryRepositoryPort;

        @Mock
        private AzureSpeechServicePort azureSpeechServicePort;

        @Mock
        private TopicRepositoryPort topicRepositoryPort;

        @Mock
        private AiAnalysisPort aiAnalysisPort;

        @Mock
        private FuriganaGenerationPort furiganaGenerarationPort;

        @InjectMocks
        private SpeakingAnalysisUseCase speakingAnalysisUseCase;

        @Test
        void UTCID01_Should_AnalyzeSuccessfully_When_ValidRequestWithTopic() {
                // Arrange (Given)
                byte[] audioBytes = new byte[]{1, 2, 3};
                SpeakingAnalysisCommand command = new SpeakingAnalysisCommand(
                        1L,
                        3L,
                        2L,
                        audioBytes,
                        "audio/wav",
                        "test.wav",
                        10
                );

                User user = User.builder()
                        .id(1L)
                        .status(UserStatus.ACTIVE)
                        .build();

                SpeakingQuestion speakingQuestion = SpeakingQuestion.builder()
                        .id(2L)
                        .title("Question Title")
                        .description("Question Description")
                        .build();

                FileResult fileResult = new FileResult(10L, "recordings/key.wav", "recording.wav", "audio/wav", 100L);

                AnswerHistory initialHistory = AnswerHistory.builder()
                        .userId(1L)
                        .questionId(2L)
                        .audioFileId(10L)
                        .build();

                AnswerHistory savedHistory = AnswerHistory.builder()
                        .id(100L)
                        .userId(1L)
                        .questionId(2L)
                        .audioFileId(10L)
                        .createdTime(Instant.now())
                        .build();

                WordAssessment wordMock = WordAssessment.builder()
                        .word("こんにちは")
                        .accuracyScore(80.0)
                        .errorType(SpeechAssessmentErrorType.NONE)
                        .build();

                SpeechAssessment azureAssessment = SpeechAssessment.builder()
                        .transcriptText("こんにちは")
                        .accuracyScore(80.0)
                        .fluencyScore(80.0)
                        .completenessScore(80.0)
                        .pronunciationScore(80.0)
                        .words(Collections.singletonList(wordMock))
                        .build();

                SpeechAssessment savedSpeechAssessment = SpeechAssessment.builder()
                        .id(200L)
                        .answerHistoryId(100L)
                        .transcriptText("こんにちは")
                        .accuracyScore(80.0)
                        .fluencyScore(80.0)
                        .completenessScore(80.0)
                        .pronunciationScore(80.0)
                        .build();

                Topic topic = Topic.builder()
                        .id(3L)
                        .japaneseName("Topic Japanese")
                        .japaneseDescription("Topic Desc")
                        .bookId(1L)
                        .orderIndex(1.0)
                        .build();

                String rawLlmFeedback = "{\"scores\": {\"vocabulary\": 8.0, \"grammar\": 8.0, \"naturalness\": 8.0}, \"userTranscript\": []}";

                when(userRepositoryPort.findById(1L)).thenReturn(Optional.of(user));
                when(questionRepositoryPort.findById(2L)).thenReturn(Optional.of(speakingQuestion));
                when(fileStorageInputPort.uploadFile(any(FileUploadCommand.class))).thenReturn(fileResult);
                when(answerHistoryRepositoryPort.saveAnswerHistory(any(AnswerHistory.class))).thenReturn(savedHistory);
                when(azureSpeechServicePort.assess(any(SpeechAssessmentCommand.class))).thenReturn(azureAssessment);
                when(answerHistoryRepositoryPort.saveSpeechAssessment(any(SpeechAssessment.class))).thenReturn(savedSpeechAssessment);
                when(aiAnalysisPort.analyzeSpeaking(eq("General conversation"), eq("Question Title"), eq("こんにちは"), anyString())).thenReturn(rawLlmFeedback);

                // Act (When)
                SpeakingAnalysisResult result = speakingAnalysisUseCase.analyzeSpeaking(command);

                // Assert (Then)
                assertNotNull(result);
                assertEquals(100L, result.historyId());
                assertEquals(8.0, result.score());

                verify(userRepositoryPort, times(1)).findById(1L);
                verify(questionRepositoryPort, times(1)).findById(2L);
                verify(fileStorageInputPort, times(1)).uploadFile(argThat(cmd ->
                        cmd.getFolderName().equals("recordings") &&
                                cmd.getOriginalName().equals("test.wav") &&
                                cmd.getContentType().equals("audio/wav") &&
                                cmd.getSize() == 3
                ));
                verify(answerHistoryRepositoryPort, times(1)).saveAnswerHistory(argThat(history ->
                        history.getUserId().equals(1L) &&
                                history.getQuestionId().equals(2L) &&
                                history.getAudioFileId().equals(10L)
                ));
                verify(azureSpeechServicePort, times(1)).assess(any(SpeechAssessmentCommand.class));
                verify(answerHistoryRepositoryPort, times(1)).saveSpeechAssessment(argThat(assessment ->
                        assessment.getAnswerHistoryId().equals(100L) &&
                                assessment.getTranscriptText().equals("こんにちは") &&
                                assessment.getPronunciationScore().equals(80.0)
                ));
                verify(answerHistoryRepositoryPort, times(1)).saveAllWordAssessment(anyList());
                verify(aiAnalysisPort, times(1)).analyzeSpeaking(eq("General conversation"), eq("Question Title"), eq("こんにちは"), anyString());
                verify(answerHistoryRepositoryPort, times(1)).saveContentAssessment(argThat(content ->
                        content.getAnswerHistoryId().equals(100L) &&
                                content.getVocabularyScore().equals(8.0) &&
                                content.getGrammarScore().equals(8.0)
                ));
        }

        @Test
        void UTCID02_Should_AnalyzeSuccessfully_When_ValidRequestNoTopic() {
                // Arrange (Given)
                byte[] audioBytes = new byte[]{1, 2, 3};
                SpeakingAnalysisCommand command = new SpeakingAnalysisCommand(
                        1L,
                        null,
                        2L,
                        audioBytes,
                        "audio/wav",
                        "test.wav",
                        10
                );

                User user = User.builder()
                        .id(1L)
                        .status(UserStatus.ACTIVE)
                        .build();

                SpeakingQuestion speakingQuestion = SpeakingQuestion.builder()
                        .id(2L)
                        .title("Question Title")
                        .description("Question Description")
                        .build();

                FileResult fileResult = new FileResult(10L, "recordings/key.wav", "recording.wav", "audio/wav", 100L);

                AnswerHistory savedHistory = AnswerHistory.builder()
                        .id(100L)
                        .userId(1L)
                        .questionId(2L)
                        .audioFileId(10L)
                        .build();

                WordAssessment wordMock = WordAssessment.builder()
                        .word("こんにちは")
                        .accuracyScore(80.0)
                        .errorType(SpeechAssessmentErrorType.NONE)
                        .build();

                SpeechAssessment azureAssessment = SpeechAssessment.builder()
                        .transcriptText("こんにちは")
                        .accuracyScore(80.0)
                        .fluencyScore(80.0)
                        .completenessScore(80.0)
                        .pronunciationScore(80.0)
                        .words(Collections.singletonList(wordMock))
                        .build();

                SpeechAssessment savedSpeechAssessment = SpeechAssessment.builder()
                        .id(200L)
                        .answerHistoryId(100L)
                        .transcriptText("こんにちは")
                        .accuracyScore(80.0)
                        .fluencyScore(80.0)
                        .completenessScore(80.0)
                        .pronunciationScore(80.0)
                        .build();

                String rawLlmFeedback = "{\"scores\": {\"vocabulary\": 8.0, \"grammar\": 8.0, \"naturalness\": 8.0}, \"userTranscript\": []}";

                when(userRepositoryPort.findById(1L)).thenReturn(Optional.of(user));
                when(questionRepositoryPort.findById(2L)).thenReturn(Optional.of(speakingQuestion));
                when(fileStorageInputPort.uploadFile(any(FileUploadCommand.class))).thenReturn(fileResult);
                when(answerHistoryRepositoryPort.saveAnswerHistory(any(AnswerHistory.class))).thenReturn(savedHistory);
                when(azureSpeechServicePort.assess(any(SpeechAssessmentCommand.class))).thenReturn(azureAssessment);
                when(answerHistoryRepositoryPort.saveSpeechAssessment(any(SpeechAssessment.class))).thenReturn(savedSpeechAssessment);
                when(aiAnalysisPort.analyzeSpeaking(eq("General conversation"), eq("Question Title"), eq("こんにちは"), anyString())).thenReturn(rawLlmFeedback);

                // Act (When)
                SpeakingAnalysisResult result = speakingAnalysisUseCase.analyzeSpeaking(command);

                // Assert (Then)
                assertNotNull(result);
                assertEquals(100L, result.historyId());
                assertEquals(8.0, result.score());

                verify(userRepositoryPort, times(1)).findById(1L);
                verify(questionRepositoryPort, times(1)).findById(2L);
                verify(topicRepositoryPort, never()).findById(anyLong());
                verify(aiAnalysisPort, times(1)).analyzeSpeaking(eq("General conversation"), eq("Question Title"), eq("こんにちは"), anyString());
        }

        @Test
        void UTCID03_Should_AnalyzeSuccessfully_When_LlmFeedbackCorrupted() {
                // Arrange (Given)
                byte[] audioBytes = new byte[]{1, 2, 3};
                SpeakingAnalysisCommand command = new SpeakingAnalysisCommand(
                        1L,
                        null,
                        2L,
                        audioBytes,
                        "audio/wav",
                        "test.wav",
                        10
                );

                User user = User.builder()
                        .id(1L)
                        .status(UserStatus.ACTIVE)
                        .build();

                SpeakingQuestion speakingQuestion = SpeakingQuestion.builder()
                        .id(2L)
                        .title("Question Title")
                        .description("Question Description")
                        .build();

                FileResult fileResult = new FileResult(10L, "recordings/key.wav", "recording.wav", "audio/wav", 100L);

                AnswerHistory savedHistory = AnswerHistory.builder()
                        .id(100L)
                        .userId(1L)
                        .questionId(2L)
                        .audioFileId(10L)
                        .build();

                WordAssessment wordMock = WordAssessment.builder()
                        .word("こんにちは")
                        .accuracyScore(80.0)
                        .errorType(SpeechAssessmentErrorType.NONE)
                        .build();

                SpeechAssessment azureAssessment = SpeechAssessment.builder()
                        .transcriptText("こんにちは")
                        .accuracyScore(80.0)
                        .fluencyScore(80.0)
                        .completenessScore(80.0)
                        .pronunciationScore(80.0)
                        .words(Collections.singletonList(wordMock))
                        .build();

                SpeechAssessment savedSpeechAssessment = SpeechAssessment.builder()
                        .id(200L)
                        .answerHistoryId(100L)
                        .transcriptText("こんにちは")
                        .accuracyScore(80.0)
                        .fluencyScore(80.0)
                        .completenessScore(80.0)
                        .pronunciationScore(80.0)
                        .build();

                // Corrupted JSON triggers exception
                String corruptedFeedback = "{ invalid json }";

                when(userRepositoryPort.findById(1L)).thenReturn(Optional.of(user));
                when(questionRepositoryPort.findById(2L)).thenReturn(Optional.of(speakingQuestion));
                when(fileStorageInputPort.uploadFile(any(FileUploadCommand.class))).thenReturn(fileResult);
                when(answerHistoryRepositoryPort.saveAnswerHistory(any(AnswerHistory.class))).thenReturn(savedHistory);
                when(azureSpeechServicePort.assess(any(SpeechAssessmentCommand.class))).thenReturn(azureAssessment);
                when(answerHistoryRepositoryPort.saveSpeechAssessment(any(SpeechAssessment.class))).thenReturn(savedSpeechAssessment);
                when(aiAnalysisPort.analyzeSpeaking(eq("General conversation"), eq("Question Title"), eq("こんにちは"), anyString())).thenReturn(corruptedFeedback);

                // Act (When)
                SpeakingAnalysisResult result = speakingAnalysisUseCase.analyzeSpeaking(command);

                // Assert (Then)
                assertNotNull(result);
                assertEquals(100L, result.historyId());
                // Since pronunciation score is 80.0 (pronScore10 = 8.0) and other scores fail back to 0.0
                // overallScore falls back to Math.round(pronScore10 * 10.0) / 10.0 = 8.0
                assertEquals(8.0, result.score());

                verify(answerHistoryRepositoryPort, times(1)).saveContentAssessment(argThat(content ->
                        content.getAnswerHistoryId().equals(100L) &&
                                content.getVocabularyScore().equals(0.0) &&
                                content.getGrammarScore().equals(0.0) &&
                                content.getAiFeedback().contains("\"overallScore\":8.0")
                ));
        }

        @Test
        void UTCID04_Should_ThrowException_When_UserNotFound() {
                // Arrange (Given)
                SpeakingAnalysisCommand command = new SpeakingAnalysisCommand(
                        1L,
                        null,
                        2L,
                        new byte[]{1, 2},
                        "audio/wav",
                        "test.wav",
                        5
                );

                when(userRepositoryPort.findById(1L)).thenReturn(Optional.empty());

                // Act (When) & Assert (Then)
                ApplicationException exception = assertThrows(
                        ApplicationException.class,
                        () -> speakingAnalysisUseCase.analyzeSpeaking(command)
                );

                assertEquals(UserErrorCode.USER_NOT_FOUND, exception.getErrorCode());
                assertEquals(UserDetailMessageKey.USER_ID_NOT_FOUND, exception.getMessage());

                verify(userRepositoryPort, times(1)).findById(1L);
                verifyNoMoreInteractions(userRepositoryPort);
                verifyNoInteractions(
                        questionRepositoryPort,
                        fileStorageInputPort,
                        answerHistoryRepositoryPort,
                        azureSpeechServicePort,
                        aiAnalysisPort
                );
        }

        @Test
        void UTCID05_Should_ThrowException_When_QuestionNotFound() {
                // Arrange (Given)
                SpeakingAnalysisCommand command = new SpeakingAnalysisCommand(
                        1L,
                        null,
                        2L,
                        new byte[]{1, 2},
                        "audio/wav",
                        "test.wav",
                        5
                );

                User user = User.builder()
                        .id(1L)
                        .status(UserStatus.ACTIVE)
                        .build();

                when(userRepositoryPort.findById(1L)).thenReturn(Optional.of(user));
                when(questionRepositoryPort.findById(2L)).thenReturn(Optional.empty());

                // Act (When) & Assert (Then)
                ApplicationException exception = assertThrows(
                        ApplicationException.class,
                        () -> speakingAnalysisUseCase.analyzeSpeaking(command)
                );

                assertEquals(SpeakingQuestionErrorCode.SPEAKING_QUESTION_NOT_FOUND, exception.getErrorCode());
                assertEquals(SpeakingQuestionDetailMessageKey.SPEAKING_QUESTION_NOT_FOUND, exception.getMessage());

                verify(userRepositoryPort, times(1)).findById(1L);
                verify(questionRepositoryPort, times(1)).findById(2L);
                verifyNoMoreInteractions(userRepositoryPort, questionRepositoryPort);
                verifyNoInteractions(
                        fileStorageInputPort,
                        answerHistoryRepositoryPort,
                        azureSpeechServicePort,
                        aiAnalysisPort
                );
        }
}