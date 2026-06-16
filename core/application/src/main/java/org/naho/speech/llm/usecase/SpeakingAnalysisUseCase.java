package org.naho.speech.llm.usecase;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import org.naho.file.command.FileUploadCommand;
import org.naho.file.port.in.FileStorageInputPort;
import org.naho.file.port.out.FileRepositoryPort;
import org.naho.file.result.FileResult;
import org.naho.furigana.port.out.FuriganaAnalysisPort;
import org.naho.i18n.message.speech.QuestionDetailMessageKey;
import org.naho.i18n.message.user.UserDetailMessageKey;
import org.naho.shared.exception.ApplicationException;
import org.naho.speech.azure.command.SpeechAssessmentCommand;
import org.naho.speech.azure.port.out.AzureSpeechServicePort;
import org.naho.speech.llm.command.SpeakingAnalysisCommand;
import org.naho.speech.llm.port.in.SpeakingAnalysisInputPort;
import org.naho.speech.llm.port.out.AiAnalysisPort;
import org.naho.speech.llm.port.out.AnswerHistoryRepositoryPort;
import org.naho.speech.llm.result.SpeakingAnalysisResult;
import org.naho.speech.llm.result.SpeakingHistoryDetailResult;
import org.naho.speech.model.AnswerHistory;
import org.naho.speech.model.ContentAssessment;
import org.naho.speech.model.SpeechAssessment;
import org.naho.speech.model.WordAssessment;
import org.naho.speech.question.port.out.QuestionRepositoryPort;
import org.naho.speech.question.port.out.exeption.QuestionErrorCode;
import org.naho.speech.topic.port.out.TopicRepositoryPort;
import org.naho.speech.type.SpeechAssessmentErrorType;
import org.naho.topic.model.Question;
import org.naho.topic.model.Topic;
import org.naho.user.exception.UserErrorCode;
import org.naho.user.model.User;
import org.naho.user.port.out.UserRepositoryPort;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
public class SpeakingAnalysisUseCase implements SpeakingAnalysisInputPort {

    private final UserRepositoryPort userRepositoryPort;
    private final QuestionRepositoryPort questionRepositoryPort;
    private final FileStorageInputPort fileStorageInputPort;
    private final FileRepositoryPort fileRepositoryPort;
    private final AnswerHistoryRepositoryPort answerHistoryRepositoryPort;
    private final AzureSpeechServicePort azureSpeechServicePort;
    private final TopicRepositoryPort topicRepositoryPort;
    private final AiAnalysisPort aiAnalysisPort;
    private final FuriganaAnalysisPort furiganaAnalysisPort;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public SpeakingAnalysisResult analyze(SpeakingAnalysisCommand command) {
        System.out.println("[SpeakingAnalysis] Starting analysis for user: " + command.userId() + ", question: " + command.questionId());

        User user = userRepositoryPort.findById(command.userId())
                .orElseThrow(() -> new ApplicationException(UserErrorCode.USER_NOT_FOUND, UserDetailMessageKey.USER_ID_NOT_FOUND));

        Question question = questionRepositoryPort.findById(command.questionId())
                .orElseThrow(() -> new ApplicationException(QuestionErrorCode.QUESTION_NOT_FOUND, QuestionDetailMessageKey.QUESTION_NOT_FOUND));

        FileUploadCommand uploadCommand = new FileUploadCommand(
                "recordings",
                command.originalFilename() != null ? command.originalFilename() : "recording.wav",
                new ByteArrayInputStream(command.audioBytes()),
                command.contentType(),
                (long) command.audioBytes().length
        );
        FileResult uploadResult = fileStorageInputPort.upload(uploadCommand);

        AnswerHistory answerHistory = AnswerHistory.builder()
                .userId(user.getId())
                .questionId(question.getId())
                .audioFileId(uploadResult.id())
                .build();
        answerHistory = answerHistoryRepositoryPort.saveAnswerHistory(answerHistory);

        SpeechAssessmentCommand speechAssessmentCommand = new SpeechAssessmentCommand(command.audioBytes(), null);
        SpeechAssessment azureAssessment = azureSpeechServicePort.assess(speechAssessmentCommand);

        SpeechAssessment speechAssessment = SpeechAssessment.builder()
                .transcriptText(azureAssessment.getTranscriptText())
                .accuracyScore(azureAssessment.getAccuracyScore())
                .fluencyScore(azureAssessment.getFluencyScore())
                .completenessScore(azureAssessment.getCompletenessScore())
                .pronunciationScore(azureAssessment.getPronunciationScore())
                .answerHistoryId(answerHistory.getId())
                .build();
        speechAssessment = answerHistoryRepositoryPort.saveSpeechAssessment(speechAssessment);

        List<WordAssessment> wordList = new ArrayList<>();
        List<Map<String, Object>> serializedWordList = new ArrayList<>();
        for (WordAssessment word : azureAssessment.getWords()) {
            WordAssessment domainWord = WordAssessment.builder()
                    .word(word.getWord())
                    .accuracyScore(word.getAccuracyScore())
                    .errorType(word.getErrorType())
                    .speechAssessmentId(speechAssessment.getId())
                    .build();
            wordList.add(domainWord);

            serializedWordList.add(Map.of(
                    "word", word.getWord(),
                    "score", word.getAccuracyScore(),
                    "error", word.getErrorType().name()
            ));
        }
        answerHistoryRepositoryPort.saveAllWordAssessment(wordList);

        String azureWordFeedbackJson;
        try {
            azureWordFeedbackJson = objectMapper.writeValueAsString(serializedWordList);
        } catch (IOException e) {
            azureWordFeedbackJson = "[]";
        }

        Topic topic = null;
        if (question.getTopicId() != null) {
            topic = topicRepositoryPort.findById(question.getTopicId()).orElse(null);
        }
        String topicName = topic != null ? topic.getJapaneseName() : "General conversation";

        String rawLlmFeedback = aiAnalysisPort.analyzeSpeaking(
                topicName,
                question.getTitle(),
                azureAssessment.getTranscriptText(),
                azureWordFeedbackJson
        );

        double vocabScore = 0.0;
        double grammarScore = 0.0;
        double naturalnessScore = 0.0;

        try {
            FeedbackResponse feedback = objectMapper.readValue(rawLlmFeedback, FeedbackResponse.class);
            if (feedback != null && feedback.scores() != null) {
                vocabScore = feedback.scores().vocabulary();
                grammarScore = feedback.scores().grammar();
                naturalnessScore = feedback.scores().naturalness();
            }

            double pronScore10 = azureAssessment.getPronunciationScore() / 10.0;
            double avgScore = (pronScore10 + vocabScore + grammarScore + naturalnessScore) / 4.0;
            avgScore = Math.round(avgScore * 10.0) / 10.0;

            JsonNode rootNode = objectMapper.readTree(rawLlmFeedback);
            if (rootNode instanceof ObjectNode objectNode) {
                objectNode.put("durationSec", command.durationSec());
                objectNode.put("overallScore", avgScore);
                rawLlmFeedback = objectMapper.writeValueAsString(objectNode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        ContentAssessment contentAssessment = ContentAssessment.builder()
                .vocabularyScore(vocabScore)
                .grammarScore(grammarScore)
                .aiFeedback(rawLlmFeedback)
                .translationText("")
                .answerHistoryId(answerHistory.getId())
                .build();
        answerHistoryRepositoryPort.saveContentAssessment(contentAssessment);

        double pronScore10 = azureAssessment.getPronunciationScore() / 10.0;
        double overallScore = (pronScore10 + vocabScore + grammarScore + naturalnessScore) / 4.0;
        overallScore = Math.round(overallScore * 10.0) / 10.0;

        return new SpeakingAnalysisResult(answerHistory.getId(), overallScore);
    }

    @Override
    public SpeakingHistoryDetailResult getHistoryDetail(Long historyId) {
        System.out.println("[SpeakingAnalysis] Loading history detail for id: " + historyId);

        AnswerHistory history = answerHistoryRepositoryPort.findAnswerHistoryById(historyId)
                .orElseThrow(() -> new ApplicationException(
                        QuestionErrorCode.QUESTION_NOT_FOUND,
                        "Answer history not found with id: " + historyId
                ));

        SpeechAssessment speech = answerHistoryRepositoryPort.findSpeechAssessmentByAnswerHistoryId(historyId)
                .orElseThrow(() -> new ApplicationException(
                        QuestionErrorCode.QUESTION_NOT_FOUND,
                        "Speech assessment not found for history id: " + historyId
                ));

        ContentAssessment content = answerHistoryRepositoryPort.findContentAssessmentByAnswerHistoryId(historyId)
                .orElseThrow(() -> new ApplicationException(
                        QuestionErrorCode.QUESTION_NOT_FOUND,
                        "Content assessment not found for history id: " + historyId
                ));

        JsonNode root;
        try {
            root = objectMapper.readTree(content.getAiFeedback());
        } catch (Exception e) {
            throw new ApplicationException(
                    QuestionErrorCode.QUESTION_NOT_FOUND,
                    "Corrupted AI feedback data"
            );
        }

        Integer durationSec = root.path("durationSec").asInt(0);
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
                naturalnessScore
        );

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
                            errNode.path("suggestion").asText("")
                    );
                }
                userTranscript.add(new SpeakingHistoryDetailResult.UserTranscriptItem(text, error));
            }
        }

        JsonNode sugNode = root.path("aiSuggestion");
        SpeakingHistoryDetailResult.AiSuggestion aiSuggestion = new SpeakingHistoryDetailResult.AiSuggestion(
                sugNode.path("jp").asText(""),
                sugNode.path("furigana").asText(""),
                sugNode.path("vi").asText("")
        );

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
                try {
                    var furiganaText = furiganaAnalysisPort.analyze(wordText);
                    if (furiganaText != null && furiganaText.getTokens() != null) {
                        StringBuilder sb = new StringBuilder();
                        for (var token : furiganaText.getTokens()) {
                            sb.append(token.getFurigana() != null && !token.getFurigana().isBlank() ? token.getFurigana() : token.getKanji());
                        }
                        furigana = sb.toString();
                    }
                } catch (Exception e) {
                    furigana = wordText;
                }

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
                        note
                ));
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
                        item.path("note").asText("")
                ));
            }
        }

        List<SpeakingHistoryDetailResult.ItVocabItem> itVocab = new ArrayList<>();
        JsonNode itNode = root.path("itVocab");
        if (itNode.isArray()) {
            for (JsonNode item : itNode) {
                itVocab.add(new SpeakingHistoryDetailResult.ItVocabItem(
                        item.path("term").asText(""),
                        item.path("reading").asText(""),
                        item.path("meaning").asText("")
                ));
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

        String objectKey = fileRepositoryPort.findObjectKeyById(history.getAudioFileId());

        return new SpeakingHistoryDetailResult(
                history.getId(),
                history.getQuestionId(),
                history.getQuestionId(),
                history.getCreatedTime() != null ? history.getCreatedTime() : Instant.now(),
                durationSec,
                overallScore,
                objectKey,
                report
        );
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record FeedbackResponse(Scores scores) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record Scores(Double vocabulary, Double grammar, Double naturalness) {}
}
