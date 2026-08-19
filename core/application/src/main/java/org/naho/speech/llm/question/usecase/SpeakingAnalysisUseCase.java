package org.naho.speech.llm.question.usecase;

import org.naho.file.port.in.UploadFileInputPort;
import org.naho.file.result.FileResult;
import org.naho.i18n.message.question.SpeakingQuestionDetailMessageKey;
import org.naho.question.exception.SpeakingQuestionErrorCode;
import org.naho.question.result.AnswerHistoryResult;
import org.naho.shared.constant.SystemZoneId;
import org.naho.shared.exception.ApplicationException;
import org.naho.shared.port.out.TransactionPort;
import org.naho.speech.azure.command.SpeechAssessmentCommand;
import org.naho.speech.azure.model.SpeechAssessment;
import org.naho.speech.azure.port.out.AzureSpeechServicePort;
import org.naho.speech.azure.port.out.SpeechAssessmentRepositoryPort;
import org.naho.speech.llm.model.question.AiFeedback;
import org.naho.speech.llm.question.command.QuestionContextCommand;
import org.naho.speech.llm.question.command.SpeakingAnalysisCommand;
import org.naho.speech.llm.question.helper.SpeakingAnalysisHelper;
import org.naho.speech.llm.question.port.in.SpeakingAnalysisInputPort;
import org.naho.speech.llm.question.port.out.AiFeedbackRepositoryPort;
import org.naho.speech.llm.question.port.out.AiQuestionAnalysisPort;
import org.naho.subscription.model.UserDailyAiUsage;
import org.naho.subscription.port.out.UserDailyAiUsageRepositoryPort;

import java.time.LocalDate;

public class SpeakingAnalysisUseCase implements SpeakingAnalysisInputPort {
    private final UserDailyAiUsageRepositoryPort userDailyAiUsageRepositoryPort;
    private final UploadFileInputPort uploadFileInputPort;
    private final TransactionPort transactionPort;
    private final AzureSpeechServicePort azureSpeechServicePort;
    private final AiFeedbackRepositoryPort aiFeedbackRepositoryPort;
    private final AiQuestionAnalysisPort aiQuestionAnalysisPort;
    private final SpeakingAnalysisHelper speakingAnalysisHelper;
    private final SpeechAssessmentRepositoryPort speechAssessmentRepositoryPort;

    public SpeakingAnalysisUseCase(
            UserDailyAiUsageRepositoryPort userDailyAiUsageRepositoryPort,
            UploadFileInputPort uploadFileInputPort,
            TransactionPort transactionPort,
            AzureSpeechServicePort azureSpeechServicePort,
            AiFeedbackRepositoryPort aiFeedbackRepositoryPort,
            AiQuestionAnalysisPort aiQuestionAnalysisPort,
            SpeakingAnalysisHelper speakingAnalysisHelper,
            SpeechAssessmentRepositoryPort speechAssessmentRepositoryPort
    ) {
        this.userDailyAiUsageRepositoryPort = userDailyAiUsageRepositoryPort;
        this.uploadFileInputPort = uploadFileInputPort;
        this.transactionPort = transactionPort;
        this.azureSpeechServicePort = azureSpeechServicePort;
        this.aiFeedbackRepositoryPort = aiFeedbackRepositoryPort;
        this.aiQuestionAnalysisPort = aiQuestionAnalysisPort;
        this.speakingAnalysisHelper = speakingAnalysisHelper;
        this.speechAssessmentRepositoryPort = speechAssessmentRepositoryPort;
    }

    @Override
    public AnswerHistoryResult analyzeSpeaking(SpeakingAnalysisCommand command) {
        AnswerHistoryResult result = transactionPort.execute(() -> doAnalyzeSpeaking(command));

        // Upload file audio lên cloud
        FileResult uploadedFile = uploadFileInputPort.uploadFileToCloud(command.storedFile());
        result.setAudioFile(uploadedFile);

        return result;
    }

    private AnswerHistoryResult doAnalyzeSpeaking(SpeakingAnalysisCommand command) {
        LocalDate today = LocalDate.now(SystemZoneId.HO_CHI_MINH_ZONE_ID);

        UserDailyAiUsage userDailyAiUsage = userDailyAiUsageRepositoryPort
                .findByUserIdAndUsageDateCreateIfNotExists(command.userId(), today);

        // nếu người dùng đã sử dụng hết lượt đánh giá trong ngày hôm nay
        if (userDailyAiUsage.getSpeakingEvaluationCount() >= command.dailySpeakingQuestionEvaluationLimit()) {
            throw new ApplicationException(
                    SpeakingQuestionErrorCode.SPEAKING_QUESTION_DAILY_LIMIT_EXCEEDED,
                    SpeakingQuestionDetailMessageKey.SPEAKING_QUESTION_DAILY_LIMIT_EXCEEDED
            );
        }

        // Lấy đánh giá từ Azure Speech AI
        SpeechAssessment speechAssessment =
                azureSpeechServicePort.assessAudio(SpeechAssessmentCommand.builder()
                        .audioBytes(command.audioBytes())
                        .duration(command.duration())
                        .userId(command.userId())
                        .build()
                );

        // Lưu vào db
        SpeechAssessment savedSpeechAssessment = speechAssessmentRepositoryPort.createNew(speechAssessment);

        // DB-R: Lấy SpeakingQuestion và context curriculum để build prompt
        QuestionContextCommand contextCommand = speakingAnalysisHelper.buildContextCommand(
                command.speakingQuestionId(),
                speechAssessment.getTranscriptText()
        );

        // EXT: Gọi OpenAI LLM (ngoài DB transaction)
        String rawLlmResponse = aiQuestionAnalysisPort.analyzeSpeaking(contextCommand);

        // Parse OpenAI Response
        AiFeedback aiFeedback = speakingAnalysisHelper.parseLlmResponse(rawLlmResponse);

        // Lưu vào db
        AiFeedback savedAiFeedback = aiFeedbackRepositoryPort.createNew(aiFeedback);

        // tăng số lần đánh giá AI với speaking question của người dùng lên 1 (today)
        userDailyAiUsage.increaseSpeakingEvaluationCount();
        userDailyAiUsageRepositoryPort.save(userDailyAiUsage);

        return null;
    }
}
