package org.naho.question.usecase;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.naho.book.model.Book;
import org.naho.book.model.Topic;
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
import org.naho.learning.model.LearningPathNode;
import org.naho.learning.port.out.LearningPathNodeRepositoryPort;
import org.naho.pagination.PageData;
import org.naho.question.command.SpeakingHistoryFilterCommand;
import org.naho.question.exception.SpeakingQuestionErrorCode;
import org.naho.question.model.SpeakingQuestion;
import org.naho.question.port.in.CrudAnswerHistoryInputPort;
import org.naho.question.port.out.AnswerHistoryRepositoryPort;
import org.naho.question.port.out.SpeakingQuestionRepositoryPort;
import org.naho.question.result.SpeakingHistoryDetailResult;
import org.naho.question.result.SpeakingHistoryListItemResult;
import org.naho.shared.exception.ApplicationException;
import org.naho.speech.model.AnswerHistory;
import org.naho.speech.model.ContentAssessment;
import org.naho.speech.model.SpeechAssessment;
import org.naho.speech.model.WordAssessment;
import org.naho.speech.type.SpeechAssessmentErrorType;
import org.naho.user.exception.UserErrorCode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public SpeakingHistoryDetailResult getSpeakingQuestionAnswerHistoryById(
            Long answerHistoryId
    ) {
        AnswerHistory answerHistory = answerHistoryRepositoryPort.findById(answerHistoryId)
                .orElseThrow(() -> new ApplicationException(
                        SpeakingQuestionErrorCode.SPEAKING_QUESTION_NOT_FOUND,
                        SpeakingQuestionDetailMessageKey.ANSWER_HISTORY_NOT_FOUND,
                        answerHistoryId
                ));

        SpeakingQuestion speakingQuestion = speakingQuestionRepositoryPort
                .findById(answerHistory.getSpeakingQuestionId())
                .orElseThrow(() -> new ApplicationException(
                        SpeakingQuestionErrorCode.SPEAKING_QUESTION_NOT_FOUND,
                        SpeakingQuestionDetailMessageKey.SPEAKING_QUESTION_NOT_FOUND,
                        answerHistory.getSpeakingQuestionId()
                ));

        SpeechAssessment speech = answerHistoryRepositoryPort
                .findSpeechAssessmentByAnswerHistoryId(answerHistoryId)
                .orElseThrow(() -> new ApplicationException(
                        SpeakingQuestionErrorCode.SPEAKING_QUESTION_NOT_FOUND,
                        SpeakingQuestionDetailMessageKey.SPEECH_ASSESSMENT_NOT_FOUND,
                        answerHistoryId
                ));

        ContentAssessment content = answerHistoryRepositoryPort
                .findContentAssessmentByAnswerHistoryId(answerHistoryId)
                .orElseThrow(() -> new ApplicationException(
                        SpeakingQuestionErrorCode.SPEAKING_QUESTION_NOT_FOUND,
                        SpeakingQuestionDetailMessageKey.CONTENT_ASSESSMENT_NOT_FOUND,
                        answerHistoryId
                ));

        JsonNode root;
        try {
            root = objectMapper.readTree(content.getAiFeedback());
        } catch (Exception e) {
            throw new ApplicationException(
                    SpeakingQuestionErrorCode.SPEAKING_QUESTION_NOT_FOUND,
                    "Corrupted AI feedback data");
        }

        Integer durationSec = (answerHistory.getDurationSec() != null && answerHistory.getDurationSec() > 0)
                ? answerHistory.getDurationSec()
                : root.path("durationSec").asInt(0);

        Double overallScore = root.path("overallScore").asDouble(0.0);

        JsonNode scoresNode = root.path("scores");
        Double vocabScore = scoresNode.path("vocabulary").asDouble(0.0);
        Double grammarScore = scoresNode.path("grammar").asDouble(0.0);
        Double naturalnessScore = scoresNode.path("naturalness").asDouble(0.0);
        Double pronunciationScore = Math.round((speech.getPronunciationScore() / 10.0) * 10.0) / 10.0;

        SpeakingHistoryDetailResult.Scores scores = new SpeakingHistoryDetailResult.Scores(
                pronunciationScore,
                vocabScore,
                grammarScore,
                naturalnessScore);

        List<SpeakingHistoryDetailResult.UserTranscriptItem> userTranscript = new ArrayList<>();
        JsonNode utNode = root.path("userTranscript");
        if (utNode.isArray()) {
            for (JsonNode item : utNode) {
                String text = item.path("text").asText("");
                SpeakingHistoryDetailResult.ErrorDetail error = null;
                JsonNode errNode = item.path("error");
                if (errNode.isObject()) {
                    error = new SpeakingHistoryDetailResult.ErrorDetail(
                            errNode.path("type").asText(""),
                            errNode.path("explanation").asText(""),
                            errNode.path("suggestion").asText(""));
                }
                userTranscript.add(new SpeakingHistoryDetailResult.UserTranscriptItem(text, error));
            }
        }

        JsonNode sugNode = root.path("aiSuggestion");
        SpeakingHistoryDetailResult.AiSuggestion aiSuggestion = new SpeakingHistoryDetailResult.AiSuggestion(
                sugNode.path("jp").asText(""),
                sugNode.path("furigana").asText(""),
                sugNode.path("vi").asText(""));

        Map<String, String> wordNotes = new HashMap<>();
        JsonNode wordNotesNode = root.path("wordNotes");
        if (wordNotesNode.isObject()) {
            wordNotesNode.fields().forEachRemaining(entry -> {
                wordNotes.put(entry.getKey().toLowerCase(), entry.getValue().asText());
            });
        }

        List<SpeakingHistoryDetailResult.PronunciationItem> pronunciation = new ArrayList<>();
        if (speech.getWords() != null) {
            for (WordAssessment wordEntity : speech.getWords()) {
                String wordText = wordEntity.getWord();
                double accScore = wordEntity.getAccuracyScore();
                SpeechAssessmentErrorType errType = wordEntity.getErrorType();

                String severity = "ok";
                if (errType == SpeechAssessmentErrorType.MISPRONUNCIATION || accScore < 50) {
                    severity = "error";
                } else if (errType == SpeechAssessmentErrorType.OMISSION || accScore < 80) {
                    severity = "warn";
                }

                String furigana = "";
//                try {
//                    var furiganaText = furiganaGenerationPort.generateFurigana(wordText);
//                    if (furiganaText != null && furiganaText.getTokens() != null) {
//                        StringBuilder sb = new StringBuilder();
//                        for (var token : furiganaText.getTokens()) {
//                            sb.append(
//                                    token.getFurigana() != null && !token.getFurigana().isBlank() ? token.getFurigana()
//                                            : token.getKanji());
//                        }
//                        furigana = sb.toString();
//                    }
//                } catch (Exception e) {
//                    furigana = wordText;
//                }

                String note = wordNotes.get(wordText.toLowerCase());
                if (note == null || note.isBlank()) {
                    if ("ok".equals(severity)) {
                        note = "Phát âm tốt.";
                    } else if ("warn".equals(severity)) {
                        note = "Cần phát âm rõ ràng hơn.";
                    } else {
                        note = "Chú ý phát âm chuẩn âm tiết.";
                    }
                }

                pronunciation.add(new SpeakingHistoryDetailResult.PronunciationItem(
                        wordText,
                        furigana,
                        severity,
                        note));
            }
        }

        String pronunciationNote = root.path("pronunciationNote").asText("Chú ý cải thiện phát âm theo hướng dẫn.");

        List<SpeakingHistoryDetailResult.ExpressionItem> expressions = new ArrayList<>();
        JsonNode exprsNode = root.path("expressions");
        if (exprsNode.isArray()) {
            for (JsonNode item : exprsNode) {
                expressions.add(new SpeakingHistoryDetailResult.ExpressionItem(
                        item.path("jp").asText(""),
                        item.path("furigana").asText(""),
                        item.path("vi").asText(""),
                        item.path("note").asText("")));
            }
        }

        List<SpeakingHistoryDetailResult.ItVocabItem> itVocab = new ArrayList<>();
        JsonNode itNode = root.path("itVocab");
        if (itNode.isArray()) {
            for (JsonNode item : itNode) {
                itVocab.add(new SpeakingHistoryDetailResult.ItVocabItem(
                        item.path("term").asText(""),
                        item.path("reading").asText(""),
                        item.path("meaning").asText("")));
            }
        }

        SpeakingHistoryDetailResult.Report report = new SpeakingHistoryDetailResult.Report(
                overallScore,
                scores,
                userTranscript,
                aiSuggestion,
                pronunciation,
                pronunciationNote,
                expressions,
                itVocab
        );

        String audioUrl = null;
        Long audioFileId = answerHistory.getAudioFileId();
        if (audioFileId != null) {
            File audioFile = fileRepositoryPort.findById(audioFileId).orElse(null);
            if (audioFile != null) {
                audioUrl = fileStorageServicePort.generatePresignedUrl(audioFile);
            }
        }

        Topic topic = topicRepositoryPort.findBySpeakingQuestionId(speakingQuestion.getId()).orElse(null);
        Long topicId = topic != null ? topic.getId() : null;
        String topicName = topic != null ? topic.getJapaneseName() : null;

        Book book = bookRepositoryPort.findBySpeakingQuestionId(speakingQuestion.getId()).orElse(null);
        Long bookId = book != null ? book.getId() : null;

        LearningPathNode learningPathNode = learningPathNodeRepositoryPort
                .findBySpeakingQuestionId(speakingQuestion.getId()).orElse(null);
        Long learningPathNodeId = learningPathNode != null ? learningPathNode.getId() : null;

        return new SpeakingHistoryDetailResult(
                answerHistory.getId(),
                topicId,
                answerHistory.getSpeakingQuestionId(),
                speakingQuestion.getJapaneseName(),
                topicName,
                learningPathNodeId,
                bookId,
                answerHistory.getCreatedTime(),
                durationSec,
                overallScore,
                report,
                audioUrl
        );
    }

    @Override
    public PageData<SpeakingHistoryListItemResult> getUserHistoryList(SpeakingHistoryFilterCommand command) {
        return answerHistoryRepositoryPort.findUserAnswerHistories(command);
    }
}
