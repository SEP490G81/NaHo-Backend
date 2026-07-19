package org.naho.speech.llm.usecase;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.naho.book.model.Book;
import org.naho.book.model.Lesson;
import org.naho.book.model.Objective;
import org.naho.book.model.Topic;
import org.naho.book.port.out.BookRepositoryPort;
import org.naho.book.port.out.LessonRepositoryPort;
import org.naho.book.port.out.ObjectiveRepositoryPort;
import org.naho.book.port.out.TopicRepositoryPort;
import org.naho.file.command.FileUploadCommand;
import org.naho.file.port.in.FileStorageInputPort;
import org.naho.file.port.out.FileRepositoryPort;
import org.naho.file.result.FileResult;
import org.naho.furigana.port.out.FuriganaGenerationPort;
import org.naho.i18n.message.question.SpeakingQuestionDetailMessageKey;
import org.naho.i18n.message.user.UserDetailMessageKey;
import org.naho.question.exception.SpeakingQuestionErrorCode;
import org.naho.question.model.Grammar;
import org.naho.question.model.SpeakingQuestion;
import org.naho.question.port.out.SpeakingQuestionRepositoryPort;
import org.naho.shared.exception.ApplicationException;
import org.naho.shared.port.out.TransactionPort;
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
import org.naho.speech.type.SpeechAssessmentErrorType;
import org.naho.user.exception.UserErrorCode;
import org.naho.user.model.User;
import org.naho.user.port.out.UserRepositoryPort;

import java.io.ByteArrayInputStream;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SpeakingAnalysisUseCase implements SpeakingAnalysisInputPort {

    private final UserRepositoryPort userRepositoryPort;
    private final SpeakingQuestionRepositoryPort speakingQuestionRepositoryPort;
    private final FileStorageInputPort fileStorageInputPort;
    private final FileRepositoryPort fileRepositoryPort;
    private final AnswerHistoryRepositoryPort answerHistoryRepositoryPort;
    private final AzureSpeechServicePort azureSpeechServicePort;
    private final TopicRepositoryPort topicRepositoryPort;
    private final LessonRepositoryPort lessonRepositoryPort;
    private final ObjectiveRepositoryPort objectiveRepositoryPort;
    private final BookRepositoryPort bookRepositoryPort;
    private final AiAnalysisPort aiAnalysisPort;
    private final FuriganaGenerationPort furiganaGenerationPort;
    private final TransactionPort transactionPort;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public SpeakingAnalysisUseCase(
            UserRepositoryPort userRepositoryPort,
            SpeakingQuestionRepositoryPort speakingQuestionRepositoryPort,
            FileStorageInputPort fileStorageInputPort,
            FileRepositoryPort fileRepositoryPort,
            AnswerHistoryRepositoryPort answerHistoryRepositoryPort,
            AzureSpeechServicePort azureSpeechServicePort,
            TopicRepositoryPort topicRepositoryPort,
            LessonRepositoryPort lessonRepositoryPort,
            ObjectiveRepositoryPort objectiveRepositoryPort,
            BookRepositoryPort bookRepositoryPort,
            AiAnalysisPort aiAnalysisPort,
            FuriganaGenerationPort furiganaGenerationPort,
            TransactionPort transactionPort
    ) {
        this.userRepositoryPort = userRepositoryPort;
        this.speakingQuestionRepositoryPort = speakingQuestionRepositoryPort;
        this.fileStorageInputPort = fileStorageInputPort;
        this.fileRepositoryPort = fileRepositoryPort;
        this.answerHistoryRepositoryPort = answerHistoryRepositoryPort;
        this.azureSpeechServicePort = azureSpeechServicePort;
        this.topicRepositoryPort = topicRepositoryPort;
        this.lessonRepositoryPort = lessonRepositoryPort;
        this.objectiveRepositoryPort = objectiveRepositoryPort;
        this.bookRepositoryPort = bookRepositoryPort;
        this.aiAnalysisPort = aiAnalysisPort;
        this.furiganaGenerationPort = furiganaGenerationPort;
        this.transactionPort = transactionPort;
    }

    public static final String RECORDS_FORDER_NAME = "recordings";

    @Override
    public SpeakingAnalysisResult analyzeSpeaking(SpeakingAnalysisCommand command) {
        return transactionPort.execute(() -> doAnalyzeSpeaking(command));
    }

    private SpeakingAnalysisResult doAnalyzeSpeaking(SpeakingAnalysisCommand command) {
        User user = userRepositoryPort.findById(command.userId())
                .orElseThrow(() -> new ApplicationException(
                        UserErrorCode.USER_NOT_FOUND,
                        UserDetailMessageKey.USER_ID_NOT_FOUND
                ));

        SpeakingQuestion speakingQuestion = speakingQuestionRepositoryPort.findById(command.speakingQuestionId())
                .orElseThrow(() -> new ApplicationException(
                        SpeakingQuestionErrorCode.SPEAKING_QUESTION_NOT_FOUND,
                        SpeakingQuestionDetailMessageKey.SPEAKING_QUESTION_NOT_FOUND
                ));

        // Upload file
        FileUploadCommand uploadCommand = FileUploadCommand.builder()
                .folderName(RECORDS_FORDER_NAME)
                .originalName(command.originalFilename() != null ?
                        command.originalFilename() :
                        "recording.wav"
                )
                .inputStream(new ByteArrayInputStream(command.audioBytes()))
                .contentType(command.contentType())
                .size((long) command.audioBytes().length)
                .build();

        FileResult uploadResult = fileStorageInputPort.uploadFile(uploadCommand);

        // Save answer history
        AnswerHistory answerHistory = AnswerHistory.builder()
                .userId(user.getId())
                .speakingQuestionId(speakingQuestion.getId())
                .audioFileId(uploadResult.id())
                .build();

        answerHistory = answerHistoryRepositoryPort.saveAnswerHistory(answerHistory);

        // Speech Assessment
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

        // Word Assessment
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

        Objective objective = objectiveRepositoryPort.findBySpeakingQuestionId(speakingQuestion.getId())
                .orElse(null);

        Lesson lesson = lessonRepositoryPort.findBySpeakingQuestionId(speakingQuestion.getId())
                .orElse(null);

        Topic topic = topicRepositoryPort.findBySpeakingQuestionId(speakingQuestion.getId())
                .orElse(null);

        Book book = bookRepositoryPort.findBySpeakingQuestionId(speakingQuestion.getId())
                .orElse(null);

        String curriculumVal = (book != null) ? book.getTitle() : "N/A";
        String levelVal = (book != null && book.getJlptLevel() != null) ? book.getJlptLevel().name() : "N/A";
        String sttVal = (objective != null && objective.getOrderIndex() != null) ? String.valueOf(objective.getOrderIndex()) : "N/A";
        String topicVal = (topic != null) ? topic.getJapaneseName() : "N/A";
        String lessonVal = (lesson != null) ? lesson.getJapaneseName() : "N/A";
        String canDoObjectiveVal = (objective != null) ? (objective.getJapaneseDescription() == null ? objective.getJapaneseName() : objective.getJapaneseDescription()) : "N/A";

        StringBuilder grammarFocusSb = new StringBuilder();
        if (speakingQuestion.getGrammars() != null) {
            for (Grammar grammar : speakingQuestion.getGrammars()) {
                if (!grammarFocusSb.isEmpty()) {
                    grammarFocusSb.append(", ");
                }
                grammarFocusSb.append(grammar.getVietnameseMeaningText());
            }
        }

        String grammarFocusVal = grammarFocusSb.isEmpty() ? "N/A" : grammarFocusSb.toString();

        StringBuilder vocabFocusSb = new StringBuilder();
        if (speakingQuestion.getVocabularies() != null) {
            for (var v : speakingQuestion.getVocabularies()) {
                if (!vocabFocusSb.isEmpty()) {
                    vocabFocusSb.append(", ");
                }
                String wordText = v.getJapanese() != null ? v.getJapanese() : (v.getReading() != null ? v.getReading() : "");
                vocabFocusSb.append(wordText);
            }
        }
        String vocabFocusVal = vocabFocusSb.isEmpty() ? "N/A" : vocabFocusSb.toString();

        double accuracy = azureAssessment.getAccuracyScore() != null ? azureAssessment.getAccuracyScore() : 0.0;
        double fluency = azureAssessment.getFluencyScore() != null ? azureAssessment.getFluencyScore() : 0.0;
        double completeness = azureAssessment.getCompletenessScore() != null ? azureAssessment.getCompletenessScore() : 0.0;
        double prosody = azureAssessment.getPronunciationScore() != null ? azureAssessment.getPronunciationScore() : 0.0;
        String studentTranscript = azureAssessment.getTranscriptText() != null ? azureAssessment.getTranscriptText() : "";

        var evaluationContext = new AiAnalysisPort.Context(
                curriculumVal,
                levelVal,
                sttVal,
                topicVal,
                lessonVal,
                canDoObjectiveVal,
                grammarFocusVal,
                vocabFocusVal,
                speakingQuestion.getTitle(),
                accuracy,
                fluency,
                completeness,
                prosody,
                studentTranscript
        );

        String rawLlmFeedback = aiAnalysisPort.analyzeSpeaking(evaluationContext);

        double vocabScore = 0.0;
        double grammarScore = 0.0;
        double naturalnessScore = 0.0;
        double pronScore10 = azureAssessment.getPronunciationScore() != null ? azureAssessment.getPronunciationScore() / 10.0 : 0.0;
        double fluencyScore10 = azureAssessment.getFluencyScore() != null ? azureAssessment.getFluencyScore() / 10.0 : 0.0;
        double overallScore = 0.0;

        try {
            JsonNode rootNode = objectMapper.readTree(rawLlmFeedback);

            // Extract vocabulary score
            double rawVocab = 0.0;
            if (rootNode.has("vocabulary") && rootNode.path("vocabulary").has("score")) {
                rawVocab = rootNode.path("vocabulary").path("score").asDouble(0.0);
            } else if (rootNode.has("scores")) {
                rawVocab = rootNode.path("scores").path("vocabulary").asDouble(0.0);
            }
            vocabScore = rawVocab > 10.0 ? rawVocab / 2.5 : rawVocab;

            // Extract grammar score
            double rawGrammar = 0.0;
            if (rootNode.has("grammar") && rootNode.path("grammar").has("score")) {
                rawGrammar = rootNode.path("grammar").path("score").asDouble(0.0);
            } else if (rootNode.has("scores")) {
                rawGrammar = rootNode.path("scores").path("grammar").asDouble(0.0);
            }
            grammarScore = rawGrammar > 10.0 ? rawGrammar / 2.5 : rawGrammar;

            // Extract naturalness score
            double rawNaturalness = 0.0;
            if (rootNode.has("naturalness") && rootNode.path("naturalness").has("score")) {
                rawNaturalness = rootNode.path("naturalness").path("score").asDouble(0.0);
            } else if (rootNode.has("scores")) {
                rawNaturalness = rootNode.path("scores").path("naturalness").asDouble(0.0);
            }
            naturalnessScore = rawNaturalness > 10.0 ? rawNaturalness / 2.5 : rawNaturalness;

            overallScore = (pronScore10 + fluencyScore10 + vocabScore + grammarScore + naturalnessScore) / 5.0;
            overallScore = Math.round(overallScore * 10.0) / 10.0;

            if (rootNode instanceof ObjectNode objectNode) {
                objectNode.put("durationSec", command.durationSec());
                objectNode.put("overallScore", overallScore);

                // Add backward-compatible scores node
                ObjectNode scoresNode = objectMapper.createObjectNode();
                scoresNode.put("vocabulary", vocabScore);
                scoresNode.put("grammar", grammarScore);
                scoresNode.put("naturalness", naturalnessScore);
                objectNode.set("scores", scoresNode);

                // Add backward-compatible aiSuggestion node
                ObjectNode sugNode = objectMapper.createObjectNode();
                String correctedJa = rootNode.path("overall").path("correctedAnswerJa").asText("");
                String correctedVi = rootNode.path("overall").path("correctedAnswerVi").asText("");
                sugNode.put("jp", correctedJa);
                sugNode.put("furigana", correctedJa); // Can generate furigana or keep as is
                sugNode.put("vi", correctedVi);
                objectNode.set("aiSuggestion", sugNode);

                // Add backward-compatible userTranscript node
                var utNode = objectMapper.createArrayNode();
                var mistakes = rootNode.path("grammar").path("mistakes");
                if (mistakes.isArray() && !mistakes.isEmpty()) {
                    for (JsonNode m : mistakes) {
                        var errItem = objectMapper.createObjectNode();
                        errItem.put("text", m.path("original").asText(""));
                        var errDetail = objectMapper.createObjectNode();
                        errDetail.put("type", "Ngữ pháp / Sự tự nhiên");
                        errDetail.put("explanation", m.path("explanationVi").asText(""));
                        errDetail.put("suggestion", m.path("corrected").asText(""));
                        errItem.set("error", errDetail);
                        utNode.add(errItem);
                    }
                } else {
                    var errItem = objectMapper.createObjectNode();
                    errItem.put("text", azureAssessment.getTranscriptText() != null ? azureAssessment.getTranscriptText() : "");
                    errItem.set("error", null);
                    utNode.add(errItem);
                }
                objectNode.set("userTranscript", utNode);

                // Add backward-compatible pronunciationNote
                objectNode.put("pronunciationNote", rootNode.path("overall").path("summaryVi").asText(""));

                // Add empty placeholders for wordNotes, expressions, itVocab if they are missing
                if (!objectNode.has("wordNotes")) {
                    objectNode.putObject("wordNotes");
                }
                if (!objectNode.has("expressions")) {
                    objectNode.putArray("expressions");
                }
                if (!objectNode.has("itVocab")) {
                    objectNode.putArray("itVocab");
                }

                rawLlmFeedback = objectMapper.writeValueAsString(objectNode);
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (vocabScore > 0.0 || grammarScore > 0.0 || naturalnessScore > 0.0) {
                overallScore = (pronScore10 + fluencyScore10 + vocabScore + grammarScore + naturalnessScore) / 5.0;
                overallScore = Math.round(overallScore * 10.0) / 10.0;
            } else {
                overallScore = Math.round(((pronScore10 + fluencyScore10) / 2.0) * 10.0) / 10.0;
            }
            try {
                ObjectNode fallbackNode = objectMapper.createObjectNode();
                ObjectNode scoresNode = objectMapper.createObjectNode();
                scoresNode.put("vocabulary", vocabScore);
                scoresNode.put("grammar", grammarScore);
                scoresNode.put("naturalness", naturalnessScore);
                fallbackNode.set("scores", scoresNode);
                fallbackNode.putArray("userTranscript");
                ObjectNode suggestionNode = objectMapper.createObjectNode();
                suggestionNode.put("jp", "");
                suggestionNode.put("furigana", "");
                suggestionNode.put("vi", "");
                fallbackNode.set("aiSuggestion", suggestionNode);
                fallbackNode.put("pronunciationNote", "");
                fallbackNode.putObject("wordNotes");
                fallbackNode.putArray("expressions");
                fallbackNode.putArray("itVocab");
                fallbackNode.put("durationSec", command.durationSec());
                fallbackNode.put("overallScore", overallScore);
                rawLlmFeedback = objectMapper.writeValueAsString(fallbackNode);
            } catch (Exception ex) {
                rawLlmFeedback = "{}";
            }
        }

        ContentAssessment contentAssessment = ContentAssessment.builder()
                .vocabularyScore(vocabScore)
                .grammarScore(grammarScore)
                .aiFeedback(rawLlmFeedback)
                .translationText("")
                .answerHistoryId(answerHistory.getId())
                .build();
        answerHistoryRepositoryPort.saveContentAssessment(contentAssessment);


        return new SpeakingAnalysisResult(answerHistory.getId(), overallScore);
    }

    @Override
    public SpeakingHistoryDetailResult getHistoryDetail(Long historyId) {
        System.out.println("[SpeakingAnalysis] Loading history detail for id: " + historyId);

        AnswerHistory history = answerHistoryRepositoryPort.findAnswerHistoryById(historyId)
                .orElseThrow(() -> new ApplicationException(
                        SpeakingQuestionErrorCode.SPEAKING_QUESTION_NOT_FOUND,
                        SpeakingQuestionDetailMessageKey.ANSWER_HISTORY_NOT_FOUND,
                        historyId
                ));

        SpeakingQuestion speakingQuestion = speakingQuestionRepositoryPort.findById(history.getSpeakingQuestionId())
                .orElseThrow(() -> new ApplicationException(
                        SpeakingQuestionErrorCode.SPEAKING_QUESTION_NOT_FOUND,
                        SpeakingQuestionDetailMessageKey.SPEAKING_QUESTION_NOT_FOUND,
                        history.getSpeakingQuestionId()
                ));

        SpeechAssessment speech = answerHistoryRepositoryPort.findSpeechAssessmentByAnswerHistoryId(historyId)
                .orElseThrow(() -> new ApplicationException(
                        SpeakingQuestionErrorCode.SPEAKING_QUESTION_NOT_FOUND,
                        SpeakingQuestionDetailMessageKey.SPEECH_ASSESSMENT_NOT_FOUND,
                        historyId
                ));

        ContentAssessment content = answerHistoryRepositoryPort.findContentAssessmentByAnswerHistoryId(historyId)
                .orElseThrow(() -> new ApplicationException(
                        SpeakingQuestionErrorCode.SPEAKING_QUESTION_NOT_FOUND,
                        SpeakingQuestionDetailMessageKey.CONTENT_ASSESSMENT_NOT_FOUND,
                        historyId
                ));

        JsonNode root;
        try {
            root = objectMapper.readTree(content.getAiFeedback());
        } catch (Exception e) {
            throw new ApplicationException(
                    SpeakingQuestionErrorCode.SPEAKING_QUESTION_NOT_FOUND,
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
                    var furiganaText = furiganaGenerationPort.generateFurigana(wordText);
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

        String objectKey = fileRepositoryPort.findObjectKeyById(history.getAudioFileId());

        Long topicId = null;

        return new SpeakingHistoryDetailResult(
                history.getId(),
                topicId,
                history.getSpeakingQuestionId(),
                history.getCreatedTime() != null ? history.getCreatedTime() : Instant.now(),
                durationSec,
                overallScore,
                objectKey,
                report
        );
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record FeedbackResponse(Scores scores) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record Scores(Double vocabulary, Double grammar, Double naturalness) {
    }
}
