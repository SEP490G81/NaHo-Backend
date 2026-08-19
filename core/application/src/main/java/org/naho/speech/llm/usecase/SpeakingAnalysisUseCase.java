package org.naho.speech.llm.usecase;

import org.naho.file.port.in.UploadFileInputPort;
import org.naho.file.result.FileResult;
import org.naho.i18n.message.question.SpeakingQuestionDetailMessageKey;
import org.naho.question.exception.SpeakingQuestionErrorCode;
import org.naho.shared.constant.SystemZoneId;
import org.naho.shared.exception.ApplicationException;
import org.naho.shared.port.out.TransactionPort;
import org.naho.speech.azure.model.SpeechAssessment;
import org.naho.speech.llm.command.ContextCommand;
import org.naho.speech.llm.command.SpeakingAnalysisCommand;
import org.naho.speech.llm.helper.SpeakingAnalysisHelper;
import org.naho.speech.llm.internal.AnalysisContext;
import org.naho.speech.llm.internal.ParsedScores;
import org.naho.speech.llm.port.in.SpeakingAnalysisInputPort;
import org.naho.speech.llm.port.out.AiAnalysisPort;
import org.naho.speech.llm.result.SpeakingAnalysisResult;
import org.naho.subscription.model.UserDailyAiUsage;
import org.naho.subscription.port.out.UserDailyAiUsageRepositoryPort;

import java.time.LocalDate;

public class SpeakingAnalysisUseCase implements SpeakingAnalysisInputPort {

    private final AiAnalysisPort aiAnalysisPort;
    private final TransactionPort transactionPort;
    private final UploadFileInputPort uploadFileInputPort;
    private final UserDailyAiUsageRepositoryPort userDailyAiUsageRepositoryPort;
    private final SpeakingAnalysisHelper speakingAnalysisHelper;

    public SpeakingAnalysisUseCase(
            AiAnalysisPort aiAnalysisPort, TransactionPort transactionPort,
            UploadFileInputPort uploadFileInputPort,
            UserDailyAiUsageRepositoryPort userDailyAiUsageRepositoryPort,
            SpeakingAnalysisHelper speakingAnalysisHelper
    ) {
        this.aiAnalysisPort = aiAnalysisPort;
        this.transactionPort = transactionPort;
        this.uploadFileInputPort = uploadFileInputPort;
        this.userDailyAiUsageRepositoryPort = userDailyAiUsageRepositoryPort;
        this.speakingAnalysisHelper = speakingAnalysisHelper;
    }

    @Override
    public SpeakingAnalysisResult analyzeSpeaking(SpeakingAnalysisCommand command) {
        SpeakingAnalysisResult speakingAnalysisResult = transactionPort.execute(() -> doAnalyzeSpeaking(command));

        // Upload file audio lên cloud
        FileResult uploadedFile = uploadFileInputPort.uploadFileToCloud(command.storedFile());
        speakingAnalysisResult.setAudioFile(uploadedFile);

        return speakingAnalysisResult;
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

        // DB-R & VALIDATE: Chuẩn bị context (chỉ đọc DB, chưa ghi DB)
        AnalysisContext ctx = speakingAnalysisHelper.prepareAnalysis(command);

        // EXT: Gọi mạng ngoài Azure Speech Assessment (Không giữ DB Transaction)
        SpeechAssessment azureAssessment = speakingAnalysisHelper.assessSpeech(command.audioBytes(), command.duration(), command.userId());

        // CALC: Tính toán thuần chuẩn bị context cho AI
        ContextCommand evalContext = speakingAnalysisHelper.buildEvaluationContext(ctx, azureAssessment);

        // EXT: Gọi mạng ngoài OpenAI LLM (Không giữ DB Transaction)
        String rawLlmFeedback = aiAnalysisPort.analyzeSpeaking(evalContext);

        // CALC: Parse JSON và tính điểm
        ParsedScores parsedScores = speakingAnalysisHelper.parseLlmFeedback(rawLlmFeedback, azureAssessment, command.duration());

        // Tăng số lần đánh giá AI với speaking question của người dùng trong ngày hôm nay lên 1
        userDailyAiUsage.increaseSpeakingEvaluationCount();
        userDailyAiUsageRepositoryPort.save(userDailyAiUsage);

        // Lưu File, AnswerHistory, SpeechAssessment, ContentAssessment và Cập nhật tiến độ
        return speakingAnalysisHelper.persistResults(command, ctx, azureAssessment, parsedScores);
    }
}
