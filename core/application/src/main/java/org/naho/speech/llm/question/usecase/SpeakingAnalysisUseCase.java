package org.naho.speech.llm.question.usecase;

import org.naho.file.constant.FileAccessStatus;
import org.naho.file.model.File;
import org.naho.file.port.in.UploadFileInputPort;
import org.naho.file.port.out.FileRepositoryPort;
import org.naho.file.result.FileResult;
import org.naho.i18n.message.learning.LearningPathNodeDetailMessageKey;
import org.naho.i18n.message.learning.UserLearningProgressDetailMessageKey;
import org.naho.i18n.message.question.SpeakingQuestionDetailMessageKey;
import org.naho.learning.exception.LearningPathNodeErrorCode;
import org.naho.learning.exception.UserLearningProgressErrorCode;
import org.naho.learning.model.LearningPathNode;
import org.naho.learning.model.UserLearningProgress;
import org.naho.learning.port.out.LearningPathNodeRepositoryPort;
import org.naho.learning.port.out.UserLearningProgressRepositoryPort;
import org.naho.question.exception.SpeakingQuestionErrorCode;
import org.naho.question.port.out.AnswerHistoryRepositoryPort;
import org.naho.question.port.out.AnswerHistoryResultMapper;
import org.naho.question.result.AnswerHistoryResult;
import org.naho.shared.constant.SystemZoneId;
import org.naho.shared.exception.ApplicationException;
import org.naho.shared.port.out.TransactionPort;
import org.naho.speech.azure.command.SpeechAssessmentCommand;
import org.naho.speech.azure.model.AnswerHistory;
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
    private final AnswerHistoryRepositoryPort answerHistoryRepositoryPort;
    private final AnswerHistoryResultMapper answerHistoryResultMapper;
    private final FileRepositoryPort fileRepositoryPort;
    private final LearningPathNodeRepositoryPort learningPathNodeRepositoryPort;
    private final UserLearningProgressRepositoryPort userLearningProgressRepositoryPort;

    public SpeakingAnalysisUseCase(
            UserDailyAiUsageRepositoryPort userDailyAiUsageRepositoryPort,
            UploadFileInputPort uploadFileInputPort,
            TransactionPort transactionPort,
            AzureSpeechServicePort azureSpeechServicePort,
            AiFeedbackRepositoryPort aiFeedbackRepositoryPort,
            AiQuestionAnalysisPort aiQuestionAnalysisPort,
            SpeakingAnalysisHelper speakingAnalysisHelper,
            SpeechAssessmentRepositoryPort speechAssessmentRepositoryPort,
            AnswerHistoryRepositoryPort answerHistoryRepositoryPort,
            AnswerHistoryResultMapper answerHistoryResultMapper,
            FileRepositoryPort fileRepositoryPort,
            LearningPathNodeRepositoryPort learningPathNodeRepositoryPort,
            UserLearningProgressRepositoryPort userLearningProgressRepositoryPort
    ) {
        this.userDailyAiUsageRepositoryPort = userDailyAiUsageRepositoryPort;
        this.uploadFileInputPort = uploadFileInputPort;
        this.transactionPort = transactionPort;
        this.azureSpeechServicePort = azureSpeechServicePort;
        this.aiFeedbackRepositoryPort = aiFeedbackRepositoryPort;
        this.aiQuestionAnalysisPort = aiQuestionAnalysisPort;
        this.speakingAnalysisHelper = speakingAnalysisHelper;
        this.speechAssessmentRepositoryPort = speechAssessmentRepositoryPort;
        this.answerHistoryRepositoryPort = answerHistoryRepositoryPort;
        this.answerHistoryResultMapper = answerHistoryResultMapper;
        this.fileRepositoryPort = fileRepositoryPort;
        this.learningPathNodeRepositoryPort = learningPathNodeRepositoryPort;
        this.userLearningProgressRepositoryPort = userLearningProgressRepositoryPort;
    }

    /**
     * Method đánh giá câu trả lời của người dùng (luồng duolingo)
     *
     * @param command SpeakingAnalysisCommand
     * @return AnswerHistoryResult
     */
    @Override
    public AnswerHistoryResult analyzeSpeaking(SpeakingAnalysisCommand command) {
        AnswerHistoryResult result = transactionPort.execute(() -> doAnalyzeSpeaking(command));

        // Upload file audio lên cloud
        FileResult uploadedFile = uploadFileInputPort.uploadFileToCloud(command.storedFile());
        result.setAudioFile(uploadedFile);

        return result;
    }

    private AnswerHistoryResult doAnalyzeSpeaking(SpeakingAnalysisCommand command) {
        // DB-R (Database Reading) Lấy learning path node hiện tại của user
        LearningPathNode learningPathNode = learningPathNodeRepositoryPort
                .findBySpeakingQuestionId(command.speakingQuestionId())
                .orElseThrow(() -> new ApplicationException(
                        LearningPathNodeErrorCode.LEARNING_PATH_NODE_NOT_FOUND,
                        LearningPathNodeDetailMessageKey.LEARNING_PATH_NODE_ID_NOT_FOUND,
                        command.speakingQuestionId()
                ));

        // DB-R (Database Reading) - Lấy tiến trình học của user
        UserLearningProgress progress = userLearningProgressRepositoryPort
                .findByUserId(command.userId())
                .orElseThrow(() -> new ApplicationException(
                        UserLearningProgressErrorCode.USER_LEARNING_PROGRESS_NOT_FOUND,
                        UserLearningProgressDetailMessageKey.USER_LEARNING_PROGRESS_NOT_FOUND_BY_USER_ID,
                        command.userId()
                ));

        // VALIDATE LOGIC
        // nếu node xa nhất người dùng có thể học chưa tới
        // thì ném ra lỗi
        if (progress.getFarthestAvailableNodeGlobalOrderIndex() < learningPathNode.getGlobalOrderIndex()) {
            throw new ApplicationException(
                    SpeakingQuestionErrorCode.SPEAKING_QUESTION_LOCKED,
                    SpeakingQuestionDetailMessageKey.SPEAKING_QUESTION_LOCKED
            );
        }

        LocalDate today = LocalDate.now(SystemZoneId.HO_CHI_MINH_ZONE_ID);

        UserDailyAiUsage userDailyAiUsage = userDailyAiUsageRepositoryPort
                .findByUserIdAndUsageDateCreateIfNotExists(command.userId(), today);

        // VALIDATE LOGIC
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

        System.out.println("thằng azure trả: [" + speechAssessment.getTranscriptText() + "]");

        // Lưu vào db
        SpeechAssessment savedSpeechAssessment = speechAssessmentRepositoryPort.createNew(speechAssessment);

        // DB-R: Lấy SpeakingQuestion và context curriculum để build prompt
        QuestionContextCommand contextCommand = speakingAnalysisHelper.buildContextCommand(
                command.speakingQuestionId(),
                speechAssessment.getTranscriptText()
        );

        // EXT: Gọi OpenAI LLM (ngoài DB transaction)
        String rawLlmResponse = aiQuestionAnalysisPort.analyzeSpeaking(contextCommand);
        System.out.println("AI RAW RESPONSE: " + rawLlmResponse);

        // Parse OpenAI Response
        AiFeedback aiFeedback = speakingAnalysisHelper.parseLlmResponse(rawLlmResponse);

        // Lưu vào db
        AiFeedback savedAiFeedback = aiFeedbackRepositoryPort.createNew(aiFeedback);

        // Tính điểm tổng kết dựa trên điểm phát âm trung bình và điểm nội dung trung bình
        double overallScore = (savedSpeechAssessment.getAverageScore()
                + savedAiFeedback.getAverageScore()) / 2.0;

        // Lưu file vào database để phục cho chức năng upload
        File audioFile = fileRepositoryPort.createNewForUpload(
                command.storedFile(),
                FileAccessStatus.PRIVATE
        );

        AnswerHistory answerHistory = AnswerHistory.builder()
                .userId(command.userId())
                .speakingQuestionId(command.speakingQuestionId())
                .speechAssessmentId(savedSpeechAssessment.getId())
                .aiFeedbackId(savedAiFeedback.getId())
                .audioFileId(audioFile.getId())
                .duration(command.duration())
                .overallScore(overallScore)
                .build();

        // Lưu answer history vào db
        AnswerHistory savedAnswerHistory = answerHistoryRepositoryPort.createNew(answerHistory);

        // tăng số lần đánh giá AI với speaking question của người dùng lên 1 (today)
        userDailyAiUsage.increaseSpeakingEvaluationCount();
        userDailyAiUsageRepositoryPort.save(userDailyAiUsage);

        return answerHistoryResultMapper.domainToResult(savedAnswerHistory);
    }
}
