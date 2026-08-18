package org.naho.speech.llm.question.usecase;

import org.naho.file.port.in.UploadFileInputPort;
import org.naho.file.result.FileResult;
import org.naho.i18n.message.question.SpeakingQuestionDetailMessageKey;
import org.naho.question.exception.SpeakingQuestionErrorCode;
import org.naho.shared.constant.SystemZoneId;
import org.naho.shared.exception.ApplicationException;
import org.naho.shared.port.out.TransactionPort;
import org.naho.speech.azure.command.SpeechAssessmentCommand;
import org.naho.speech.azure.port.in.SpeechAssessmentInputPort;
import org.naho.speech.azure.result.SpeechAssessmentResult;
import org.naho.speech.llm.model.question.AiFeedback;
import org.naho.speech.llm.question.command.SpeakingAnalysisCommand;
import org.naho.speech.llm.question.port.in.SpeakingAnalysisInputPort;
import org.naho.speech.llm.question.port.out.AiFeedbackRepositoryPort;
import org.naho.speech.llm.question.result.SpeakingAnalysisResult;
import org.naho.subscription.model.UserDailyAiUsage;
import org.naho.subscription.port.out.UserDailyAiUsageRepositoryPort;

import java.time.LocalDate;

public class SpeakingAnalysisUseCase implements SpeakingAnalysisInputPort {
    private final UserDailyAiUsageRepositoryPort userDailyAiUsageRepositoryPort;
    private final UploadFileInputPort uploadFileInputPort;
    private final TransactionPort transactionPort;
    private final SpeechAssessmentInputPort speechAssessmentInputPort;
    private final AiFeedbackRepositoryPort aiFeedbackRepositoryPort;

    public SpeakingAnalysisUseCase(
            UserDailyAiUsageRepositoryPort userDailyAiUsageRepositoryPort,
            UploadFileInputPort uploadFileInputPort,
            TransactionPort transactionPort,
            SpeechAssessmentInputPort speechAssessmentInputPort,
            AiFeedbackRepositoryPort aiFeedbackRepositoryPort
    ) {
        this.userDailyAiUsageRepositoryPort = userDailyAiUsageRepositoryPort;
        this.uploadFileInputPort = uploadFileInputPort;
        this.transactionPort = transactionPort;
        this.speechAssessmentInputPort = speechAssessmentInputPort;
        this.aiFeedbackRepositoryPort = aiFeedbackRepositoryPort;
    }

    @Override
    public SpeakingAnalysisResult analyzeSpeaking(SpeakingAnalysisCommand command) {
        SpeakingAnalysisResult result = transactionPort.execute(() -> doAnalyzeSpeaking(command));

        // Upload file audio lên cloud
        FileResult uploadedFile = uploadFileInputPort.uploadFileToCloud(command.storedFile());
        result.setAudioFileResult(uploadedFile);

        return result;
    }

    private SpeakingAnalysisResult doAnalyzeSpeaking(SpeakingAnalysisCommand command) {
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
        SpeechAssessmentResult speechAssessmentResult =
                speechAssessmentInputPort.assessAudio(SpeechAssessmentCommand.builder()
                        .audioBytes(command.audioBytes())
                        .duration(command.duration())
                        .userId(command.userId())
                        .build()
                );

        // Parse OpenAI ở đây
        AiFeedback aiFeedback = null;

        // tăng số lần đánh giá AI với speaking question của người dùng lên 1 (today)
        userDailyAiUsage.increaseSpeakingEvaluationCount();
        userDailyAiUsageRepositoryPort.save(userDailyAiUsage);

        AiFeedback savedAiFeedback = aiFeedbackRepositoryPort.createNew(aiFeedback);
        return null;
    }
}
