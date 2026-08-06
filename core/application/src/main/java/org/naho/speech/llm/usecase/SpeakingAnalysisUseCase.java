package org.naho.speech.llm.usecase;

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
import org.naho.file.model.File;
import org.naho.file.port.in.UploadFileInputPort;
import org.naho.file.port.out.FileRepositoryPort;
import org.naho.file.port.out.FileResultMapperPort;
import org.naho.file.result.FileResult;
import org.naho.file.result.StoredFile;
import org.naho.furigana.port.out.FuriganaGenerationPort;
import org.naho.i18n.message.learning.LearningPathNodeDetailMessageKey;
import org.naho.i18n.message.learning.UserLearningProgressDetailMessageKey;
import org.naho.i18n.message.question.SpeakingQuestionDetailMessageKey;
import org.naho.i18n.message.user.UserDetailMessageKey;
import org.naho.learning.exception.LearningPathNodeErrorCode;
import org.naho.learning.exception.UserLearningProgressErrorCode;
import org.naho.learning.model.LearningPathNode;
import org.naho.learning.model.UserLearningProgress;
import org.naho.learning.port.out.LearningPathNodeRepositoryPort;
import org.naho.learning.port.out.UserLearningProgressRepositoryPort;
import org.naho.pagination.PageData;
import org.naho.question.command.CompleteSpeakingQuestionCommand;
import org.naho.question.exception.SpeakingQuestionErrorCode;
import org.naho.question.model.Grammar;
import org.naho.question.model.SpeakingQuestion;
import org.naho.question.port.in.CompleteSpeakingQuestionInputPort;
import org.naho.question.port.out.AnswerHistoryRepositoryPort;
import org.naho.question.port.out.SpeakingQuestionRepositoryPort;
import org.naho.shared.exception.ApplicationException;
import org.naho.shared.port.out.TransactionPort;
import org.naho.speech.azure.command.SpeechAssessmentCommand;
import org.naho.speech.azure.port.out.AzureSpeechServicePort;
import org.naho.speech.llm.command.SpeakingAnalysisCommand;
import org.naho.speech.llm.port.in.SpeakingAnalysisInputPort;
import org.naho.speech.llm.port.out.AiAnalysisPort;
import org.naho.speech.llm.result.SpeakingAnalysisResult;
import org.naho.speech.model.AnswerHistory;
import org.naho.speech.model.ContentAssessment;
import org.naho.speech.model.SpeechAssessment;
import org.naho.speech.model.WordAssessment;
import org.naho.speech.type.SpeechAssessmentErrorType;
import org.naho.user.exception.UserErrorCode;
import org.naho.user.model.User;
import org.naho.user.port.out.UserRepositoryPort;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SpeakingAnalysisUseCase implements SpeakingAnalysisInputPort {

    private final UserRepositoryPort userRepositoryPort;
    private final SpeakingQuestionRepositoryPort speakingQuestionRepositoryPort;
    private final FileRepositoryPort fileRepositoryPort;
    private final AnswerHistoryRepositoryPort answerHistoryRepositoryPort;
    private final AzureSpeechServicePort azureSpeechServicePort;
    private final TopicRepositoryPort topicRepositoryPort;
    private final LessonRepositoryPort lessonRepositoryPort;
    private final ObjectiveRepositoryPort objectiveRepositoryPort;
    private final BookRepositoryPort bookRepositoryPort;
    private final LearningPathNodeRepositoryPort learningPathNodeRepositoryPort;
    private final AiAnalysisPort aiAnalysisPort;
    private final FuriganaGenerationPort furiganaGenerationPort;
    private final TransactionPort transactionPort;
    private final CompleteSpeakingQuestionInputPort completeSpeakingQuestionInputPort;
    private final UserLearningProgressRepositoryPort userLearningProgressRepositoryPort;
    private final FileResultMapperPort fileResultMapperPort;
    private final UploadFileInputPort uploadFileInputPort;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public SpeakingAnalysisUseCase(
            UserRepositoryPort userRepositoryPort,
            SpeakingQuestionRepositoryPort speakingQuestionRepositoryPort,
            FileRepositoryPort fileRepositoryPort,
            AnswerHistoryRepositoryPort answerHistoryRepositoryPort,
            AzureSpeechServicePort azureSpeechServicePort,
            TopicRepositoryPort topicRepositoryPort,
            LessonRepositoryPort lessonRepositoryPort,
            ObjectiveRepositoryPort objectiveRepositoryPort,
            BookRepositoryPort bookRepositoryPort,
            LearningPathNodeRepositoryPort learningPathNodeRepositoryPort,
            AiAnalysisPort aiAnalysisPort,
            FuriganaGenerationPort furiganaGenerationPort,
            TransactionPort transactionPort,
            CompleteSpeakingQuestionInputPort completeSpeakingQuestionInputPort,
            UserLearningProgressRepositoryPort userLearningProgressRepositoryPort,
            FileResultMapperPort fileResultMapperPort,
            UploadFileInputPort uploadFileInputPort) {
        this.userRepositoryPort = userRepositoryPort;
        this.speakingQuestionRepositoryPort = speakingQuestionRepositoryPort;
        this.fileRepositoryPort = fileRepositoryPort;
        this.answerHistoryRepositoryPort = answerHistoryRepositoryPort;
        this.azureSpeechServicePort = azureSpeechServicePort;
        this.topicRepositoryPort = topicRepositoryPort;
        this.lessonRepositoryPort = lessonRepositoryPort;
        this.objectiveRepositoryPort = objectiveRepositoryPort;
        this.bookRepositoryPort = bookRepositoryPort;
        this.learningPathNodeRepositoryPort = learningPathNodeRepositoryPort;
        this.aiAnalysisPort = aiAnalysisPort;
        this.furiganaGenerationPort = furiganaGenerationPort;
        this.transactionPort = transactionPort;
        this.completeSpeakingQuestionInputPort = completeSpeakingQuestionInputPort;
        this.userLearningProgressRepositoryPort = userLearningProgressRepositoryPort;
        this.fileResultMapperPort = fileResultMapperPort;
        this.uploadFileInputPort = uploadFileInputPort;
    }

    @Override
    public SpeakingAnalysisResult analyzeSpeaking(SpeakingAnalysisCommand command) {
        SpeakingAnalysisResult result = transactionPort.execute(() -> doAnalyzeSpeaking(command));

        StoredFile storedFile = command.storedFile();
        if (storedFile != null) {
            FileResult uploadedFile = uploadFileInputPort.uploadFileToCloud(storedFile);
            result.setAudioFile(uploadedFile);
        }

        return result;
    }

    private SpeakingAnalysisResult doAnalyzeSpeaking(SpeakingAnalysisCommand command) {
        LearningPathNode speakingQuestionLearningPathNode = learningPathNodeRepositoryPort
                .findBySpeakingQuestionId(command.speakingQuestionId())
                .orElseThrow(() -> new ApplicationException(
                        LearningPathNodeErrorCode.LEARNING_PATH_NODE_NOT_FOUND,
                        LearningPathNodeDetailMessageKey.LEARNING_PATH_NODE_ID_NOT_FOUND,
                        command.speakingQuestionId()));

        UserLearningProgress progress = userLearningProgressRepositoryPort
                .findByUserId(command.userId())
                .orElseThrow(() -> new ApplicationException(
                        UserLearningProgressErrorCode.USER_LEARNING_PROGRESS_NOT_FOUND,
                        UserLearningProgressDetailMessageKey.USER_LEARNING_PROGRESS_NOT_FOUND_BY_USER_ID,
                        command.userId()));

        // nếu node xa nhất người dùng có thể học còn nhỏ hơn node đang định học
        if (progress.getFarthestAvailableNodeGlobalOrderIndex() < speakingQuestionLearningPathNode
                .getGlobalOrderIndex()) {
            throw new ApplicationException(
                    SpeakingQuestionErrorCode.SPEAKING_QUESTION_LOCKED,
                    SpeakingQuestionDetailMessageKey.SPEAKING_QUESTION_LOCKED);
        }

        User user = userRepositoryPort.findById(command.userId())
                .orElseThrow(() -> new ApplicationException(
                        UserErrorCode.USER_NOT_FOUND,
                        UserDetailMessageKey.USER_ID_NOT_FOUND));

        SpeakingQuestion speakingQuestion = speakingQuestionRepositoryPort.findById(command.speakingQuestionId())
                .orElseThrow(() -> new ApplicationException(
                        SpeakingQuestionErrorCode.SPEAKING_QUESTION_NOT_FOUND,
                        SpeakingQuestionDetailMessageKey.SPEAKING_QUESTION_NOT_FOUND));

        File audioFile = null;

        // nếu đã lưu file trong local
        // (tức là đăng kí gói không phải FREE)
        // thì mới lưu lại file nói chuyện của người dùng
        StoredFile storedFile = command.storedFile();
        if (storedFile != null) {
            audioFile = fileRepositoryPort.createNewForUpload(storedFile, false);
        }

        AnswerHistory answerHistory = AnswerHistory.builder()
                .userId(user.getId())
                .speakingQuestionId(speakingQuestion.getId())
                .build();

        if (audioFile != null) {
            answerHistory.setAudioFileId(audioFile.getId());
        }

        AnswerHistory savedAnswerHistory = answerHistoryRepositoryPort
                .save(answerHistory);

        // Speech Assessment
        SpeechAssessmentCommand speechAssessmentCommand = new SpeechAssessmentCommand(
                command.audioBytes(),
                null);

        SpeechAssessment azureAssessment = azureSpeechServicePort.assess(speechAssessmentCommand);

        SpeechAssessment speechAssessment = SpeechAssessment.builder()
                .transcriptText(azureAssessment.getTranscriptText())
                .accuracyScore(azureAssessment.getAccuracyScore())
                .fluencyScore(azureAssessment.getFluencyScore())
                .completenessScore(azureAssessment.getCompletenessScore())
                .pronunciationScore(azureAssessment.getPronunciationScore())
                .answerHistoryId(savedAnswerHistory.getId())
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
                    "error", word.getErrorType().name()));
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
        String sttVal = (objective != null && objective.getOrderIndex() != null)
                ? String.valueOf(objective.getOrderIndex())
                : "N/A";
        String topicVal = (topic != null) ? topic.getJapaneseName() : "N/A";
        String lessonVal = (lesson != null) ? lesson.getJapaneseName() : "N/A";

        String canDoObjectiveVal;
        if (objective != null) {
            canDoObjectiveVal = (objective.getJapaneseDescription() != null
                    && !objective.getJapaneseDescription().isBlank())
                    ? objective.getJapaneseDescription()
                    : objective.getJapaneseName();
        } else {
            canDoObjectiveVal = "N/A";
        }

        StringBuilder grammarFocusSb = new StringBuilder();
        if (speakingQuestion.getGrammars() != null) {
            for (Grammar grammar : speakingQuestion.getGrammars()) {
                if (!grammarFocusSb.isEmpty()) {
                    grammarFocusSb.append("\n");
                }
                String ja = grammar.getJapanese() != null ? grammar.getJapanese() : "";
                String vi = grammar.getVietnameseMeaningText() != null ? grammar.getVietnameseMeaningText() : "";
                if (!ja.isEmpty() && !vi.isEmpty()) {
                    grammarFocusSb.append("- ").append(ja).append("（").append(vi).append("）");
                } else if (!ja.isEmpty()) {
                    grammarFocusSb.append("- ").append(ja);
                } else {
                    grammarFocusSb.append("- ").append(vi);
                }
            }
        }
        String grammarFocusVal = grammarFocusSb.isEmpty() ? "N/A (no specific grammar focus for this lesson)"
                : grammarFocusSb.toString();

        StringBuilder vocabFocusSb = new StringBuilder();
        if (speakingQuestion.getVocabularies() != null) {
            for (var v : speakingQuestion.getVocabularies()) {
                if (!vocabFocusSb.isEmpty()) {
                    vocabFocusSb.append("\n");
                }
                String japanese = v.getJapanese() != null ? v.getJapanese() : "";
                String reading = v.getReading() != null ? v.getReading() : "";
                String vi = v.getVietnameseMeaningText() != null ? v.getVietnameseMeaningText() : "";
                StringBuilder entry = new StringBuilder("- ");
                entry.append(japanese);
                if (!reading.isEmpty()) {
                    entry.append("（").append(reading).append("）");
                }
                if (!vi.isEmpty()) {
                    entry.append(" = ").append(vi);
                }
                vocabFocusSb.append(entry);
            }
        }
        String vocabFocusVal = vocabFocusSb.isEmpty() ? "N/A (no specific vocabulary focus for this lesson)"
                : vocabFocusSb.toString();

        String questionTitleVal = speakingQuestion.getJapaneseName() != null ? speakingQuestion.getJapaneseName() : "N/A";
        String questionDescriptionVal = speakingQuestion.getDescription() != null
                && !speakingQuestion.getDescription().isBlank()
                ? speakingQuestion.getDescription()
                : questionTitleVal;

        double accuracy = azureAssessment.getAccuracyScore() != null ? azureAssessment.getAccuracyScore() : 0.0;
        double fluency = azureAssessment.getFluencyScore() != null ? azureAssessment.getFluencyScore() : 0.0;
        double completeness = azureAssessment.getCompletenessScore() != null ? azureAssessment.getCompletenessScore()
                : 0.0;
        double overallPronunciation = azureAssessment.getPronunciationScore() != null
                ? azureAssessment.getPronunciationScore()
                : 0.0;
        String studentTranscript = azureAssessment.getTranscriptText() != null ? azureAssessment.getTranscriptText()
                : "";

        var evaluationContext = new AiAnalysisPort.Context(
                curriculumVal,
                levelVal,
                sttVal,
                topicVal,
                lessonVal,
                canDoObjectiveVal,
                grammarFocusVal,
                vocabFocusVal,
                questionTitleVal,
                questionDescriptionVal,
                accuracy,
                fluency,
                completeness,
                overallPronunciation,
                studentTranscript);

        String rawLlmFeedback = aiAnalysisPort.analyzeSpeaking(evaluationContext);

        double vocabScore = 0.0;
        double grammarScore = 0.0;
        double naturalnessScore = 0.0;
        double pronScore10 = azureAssessment.getPronunciationScore() != null
                ? azureAssessment.getPronunciationScore() / 10.0
                : 0.0;
        double fluencyScore10 = azureAssessment.getFluencyScore() != null ? azureAssessment.getFluencyScore() / 10.0
                : 0.0;
        double overallScore = 0.0;

        try {
            JsonNode rootNode = objectMapper.readTree(rawLlmFeedback);

            double rawVocab = 0.0;
            if (rootNode.has("vocabulary") && rootNode.path("vocabulary").has("score")) {
                rawVocab = rootNode.path("vocabulary").path("score").asDouble(0.0);
            } else if (rootNode.has("scores")) {
                rawVocab = rootNode.path("scores").path("vocabulary").asDouble(0.0);
            }
            vocabScore = Math.min(rawVocab / 2.5, 10.0);

            double rawGrammar = 0.0;
            if (rootNode.has("grammar") && rootNode.path("grammar").has("score")) {
                rawGrammar = rootNode.path("grammar").path("score").asDouble(0.0);
            } else if (rootNode.has("scores")) {
                rawGrammar = rootNode.path("scores").path("grammar").asDouble(0.0);
            }
            grammarScore = Math.min(rawGrammar / 2.5, 10.0);

            double rawNaturalness = 0.0;
            if (rootNode.has("naturalness") && rootNode.path("naturalness").has("score")) {
                rawNaturalness = rootNode.path("naturalness").path("score").asDouble(0.0);
            } else if (rootNode.has("scores")) {
                rawNaturalness = rootNode.path("scores").path("naturalness").asDouble(0.0);
            }
            naturalnessScore = Math.min(rawNaturalness / 2.5, 10.0);

            overallScore = (pronScore10 + fluencyScore10 + vocabScore + grammarScore + naturalnessScore) / 5.0;
            overallScore = Math.round(overallScore * 10.0) / 10.0;

            if (rootNode instanceof ObjectNode objectNode) {
                objectNode.put("durationSec", command.durationSec());
                objectNode.put("overallScore", overallScore);

                ObjectNode scoresNode = objectMapper.createObjectNode();
                scoresNode.put("vocabulary", vocabScore);
                scoresNode.put("grammar", grammarScore);
                scoresNode.put("naturalness", naturalnessScore);
                objectNode.set("scores", scoresNode);

                ObjectNode sugNode = objectMapper.createObjectNode();
                String correctedJa = rootNode.path("overall").path("correctedAnswerJa").asText("");
                String correctedVi = rootNode.path("overall").path("correctedAnswerVi").asText("");
                sugNode.put("jp", correctedJa);
                sugNode.put("furigana", correctedJa);
                sugNode.put("vi", correctedVi);
                objectNode.set("aiSuggestion", sugNode);

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
                    errItem.put("text",
                            azureAssessment.getTranscriptText() != null ? azureAssessment.getTranscriptText() : "");
                    errItem.set("error", null);
                    utNode.add(errItem);
                }
                objectNode.set("userTranscript", utNode);

                objectNode.put("pronunciationNote", rootNode.path("overall").path("summaryVi").asText(""));

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
                .answerHistoryId(savedAnswerHistory.getId())
                .build();

        answerHistoryRepositoryPort.saveContentAssessment(contentAssessment);

        completeSpeakingQuestionInputPort.completeSpeakingQuestion(
                CompleteSpeakingQuestionCommand.builder()
                        .userLearningProgress(progress)
                        .speakingQuestionLearningPathNode(speakingQuestionLearningPathNode)
                        .userId(command.userId())
                        .overallScore(overallScore)
                        .build());

        SpeakingAnalysisResult result = SpeakingAnalysisResult.builder()
                .overallScore(overallScore)
                .answerHistoryId(savedAnswerHistory.getId())
                .build();

        if (audioFile != null) {
            result.setAudioFile(fileResultMapperPort.domainToResult(audioFile));
        }

        return result;
    }
}
