package org.naho.question.usecase;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.naho.book.port.out.BookRepositoryPort;
import org.naho.book.port.out.TopicRepositoryPort;
import org.naho.file.exception.FileErrorCode;
import org.naho.file.model.File;
import org.naho.file.port.out.FileRepositoryPort;
import org.naho.file.port.out.FileStorageServicePort;
import org.naho.furigana.port.out.FuriganaGenerationPort;
import org.naho.i18n.message.file.FileDetailMessageKey;
import org.naho.i18n.message.question.SpeakingQuestionDetailMessageKey;
import org.naho.i18n.message.user.UserDetailMessageKey;
import org.naho.learning.port.out.LearningPathNodeRepositoryPort;
import org.naho.question.exception.SpeakingQuestionErrorCode;
import org.naho.question.port.in.CrudAnswerHistoryInputPort;
import org.naho.question.port.out.AnswerHistoryRepositoryPort;
import org.naho.question.port.out.SpeakingQuestionRepositoryPort;
import org.naho.question.result.AnswerHistoryListItemResult;
import org.naho.shared.exception.ApplicationException;
import org.naho.speech.azure.model.AnswerHistory;
import org.naho.user.exception.UserErrorCode;

import java.util.List;

public class CrudAnswerHistoryUseCase implements CrudAnswerHistoryInputPort {
    private final AnswerHistoryRepositoryPort answerHistoryRepositoryPort;
    private final SpeakingQuestionRepositoryPort speakingQuestionRepositoryPort;
    private final FileRepositoryPort fileRepositoryPort;
    private final FileStorageServicePort fileStorageServicePort;
    private final TopicRepositoryPort topicRepositoryPort;
    private final BookRepositoryPort bookRepositoryPort;
    private final LearningPathNodeRepositoryPort learningPathNodeRepositoryPort;
    private final FuriganaGenerationPort furiganaGenerationPort;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public CrudAnswerHistoryUseCase(
            AnswerHistoryRepositoryPort answerHistoryRepositoryPort,
            SpeakingQuestionRepositoryPort speakingQuestionRepositoryPort,
            FileRepositoryPort fileRepositoryPort,
            FileStorageServicePort fileStorageServicePort,
            TopicRepositoryPort topicRepositoryPort,
            BookRepositoryPort bookRepositoryPort,
            LearningPathNodeRepositoryPort learningPathNodeRepositoryPort,
            FuriganaGenerationPort furiganaGenerationPort
    ) {
        this.answerHistoryRepositoryPort = answerHistoryRepositoryPort;
        this.speakingQuestionRepositoryPort = speakingQuestionRepositoryPort;
        this.fileRepositoryPort = fileRepositoryPort;
        this.fileStorageServicePort = fileStorageServicePort;
        this.topicRepositoryPort = topicRepositoryPort;
        this.bookRepositoryPort = bookRepositoryPort;
        this.learningPathNodeRepositoryPort = learningPathNodeRepositoryPort;
        this.furiganaGenerationPort = furiganaGenerationPort;
    }

    /**
     * Lấy presigned url từ s3 của audio file trong 1 answer history
     *
     * @param id     answerHistoryId
     * @param userId user id
     * @return presigned url
     */
    @Override
    public String generateAudioFilePresignedUrl(Long id, Long userId) {
        if (id == null) {
            throw new ApplicationException(
                    SpeakingQuestionErrorCode.SPEAKING_QUESTION_NOT_FOUND,
                    SpeakingQuestionDetailMessageKey.ANSWER_HISTORY_ID_NULL
            );
        }

        AnswerHistory answerHistory = answerHistoryRepositoryPort.findById(id)
                .orElseThrow(() -> new ApplicationException(
                        SpeakingQuestionErrorCode.SPEAKING_QUESTION_NOT_FOUND,
                        SpeakingQuestionDetailMessageKey.ANSWER_HISTORY_NOT_FOUND,
                        id
                ));

        // nếu câu trả lời không phải của người dùng thì ném ra lỗi
        if (!userId.equals(answerHistory.getUserId())) {
            throw new ApplicationException(
                    UserErrorCode.USER_UNAUTHORIZED,
                    UserDetailMessageKey.USER_UNAUTHORIZED
            );
        }

        // nếu câu trả lời không có file (tức là trả lời ở tài khoản FREE)
        if (answerHistory.getAudioFileId() == null) {
            throw new ApplicationException(
                    FileErrorCode.FILE_NOT_VALID,
                    FileDetailMessageKey.FILE_ID_NULL
            );
        }

        File file = fileRepositoryPort.findById(answerHistory.getAudioFileId())
                .orElseThrow(() -> new ApplicationException(
                        FileErrorCode.FILE_NOT_FOUND,
                        FileDetailMessageKey.FILE_NOT_FOUND,
                        answerHistory.getAudioFileId()
                ));

        return fileStorageServicePort.generatePresignedUrl(file);
    }


    @Override
    public List<AnswerHistoryListItemResult> findAllBySpeakingQuestionId(Long speakingQuestionId) {

    }
}
