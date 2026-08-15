package org.naho.speech.llm.usecase;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.naho.book.model.Book;
import org.naho.book.model.Lesson;
import org.naho.book.model.Objective;
import org.naho.book.model.Topic;
import org.naho.book.port.out.BookRepositoryPort;
import org.naho.book.port.out.LessonRepositoryPort;
import org.naho.book.port.out.ObjectiveRepositoryPort;
import org.naho.book.port.out.TopicRepositoryPort;
import org.naho.file.constant.FileAccessStatus;
import org.naho.file.model.File;
import org.naho.file.port.in.UploadFileInputPort;
import org.naho.file.port.out.FileRepositoryPort;
import org.naho.file.port.out.FileResultMapperPort;
import org.naho.file.result.FileResult;
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
import org.naho.question.command.CompleteSpeakingQuestionCommand;
import org.naho.question.exception.SpeakingQuestionErrorCode;
import org.naho.question.model.Grammar;
import org.naho.question.model.SpeakingQuestion;
import org.naho.question.port.in.CompleteSpeakingQuestionInputPort;
import org.naho.question.port.out.AnswerHistoryRepositoryPort;
import org.naho.question.port.out.SpeakingQuestionRepositoryPort;
import org.naho.shared.constant.SystemZoneId;
import org.naho.shared.exception.ApplicationException;
import org.naho.shared.port.out.TransactionPort;
import org.naho.speech.azure.command.SpeechAssessmentCommand;
import org.naho.speech.azure.model.AnswerHistory;
import org.naho.speech.azure.model.ContentAssessment;
import org.naho.speech.azure.model.SpeechAssessment;
import org.naho.speech.azure.model.WordAssessment;
import org.naho.speech.azure.port.out.AzureSpeechServicePort;
import org.naho.speech.llm.command.SpeakingAnalysisCommand;
import org.naho.speech.llm.port.in.SpeakingAnalysisInputPort;
import org.naho.speech.llm.port.out.AiAnalysisPort;
import org.naho.speech.llm.result.SpeakingAnalysisReportResult;
import org.naho.speech.llm.result.SpeakingAnalysisResult;
import org.naho.speech.llm.result.WordPronunciationResult;
import org.naho.subscription.model.UserDailyAiUsage;
import org.naho.subscription.port.out.UserDailyAiUsageRepositoryPort;
import org.naho.user.exception.UserErrorCode;
import org.naho.user.model.User;
import org.naho.user.port.out.UserRepositoryPort;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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
    private final TransactionPort transactionPort;
    private final CompleteSpeakingQuestionInputPort completeSpeakingQuestionInputPort;
    private final UserLearningProgressRepositoryPort userLearningProgressRepositoryPort;
    private final FileResultMapperPort fileResultMapperPort;
    private final UploadFileInputPort uploadFileInputPort;
    private final FuriganaGenerationPort furiganaGenerationPort;
    private final UserDailyAiUsageRepositoryPort userDailyAiUsageRepositoryPort;
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
            TransactionPort transactionPort,
            CompleteSpeakingQuestionInputPort completeSpeakingQuestionInputPort,
            UserLearningProgressRepositoryPort userLearningProgressRepositoryPort,
            FileResultMapperPort fileResultMapperPort,
            UploadFileInputPort uploadFileInputPort,
            UserDailyAiUsageRepositoryPort userDailyAiUsageRepositoryPort,
            FuriganaGenerationPort furiganaGenerationPort
    ) {
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
        this.transactionPort = transactionPort;
        this.completeSpeakingQuestionInputPort = completeSpeakingQuestionInputPort;
        this.userLearningProgressRepositoryPort = userLearningProgressRepositoryPort;
        this.fileResultMapperPort = fileResultMapperPort;
        this.uploadFileInputPort = uploadFileInputPort;
        this.userDailyAiUsageRepositoryPort = userDailyAiUsageRepositoryPort;
        this.furiganaGenerationPort = furiganaGenerationPort;
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
        AnalysisContext ctx = prepareAnalysis(command);

        // EXT: Gọi mạng ngoài Azure Speech Assessment (Không giữ DB Transaction)
        SpeechAssessment azureAssessment = assessSpeech(command.audioBytes());

        // CALC: Tính toán thuần chuẩn bị context cho AI
        AiAnalysisPort.Context evalContext = buildEvaluationContext(ctx, azureAssessment);

        // EXT: Gọi mạng ngoài OpenAI LLM (Không giữ DB Transaction)
        String rawLlmFeedback = aiAnalysisPort.analyzeSpeaking(evalContext);

        // CALC: Parse JSON và tính điểm
        ParsedScores parsedScores = parseLlmFeedback(rawLlmFeedback, azureAssessment, command.durationSec());

        // Tăng số lần đánh giá AI với speaking question của người dùng trong ngày hôm nay lên 1
        userDailyAiUsage.increaseSpeakingEvaluationCount();
        userDailyAiUsageRepositoryPort.save(userDailyAiUsage);

        // Lưu File, AnswerHistory, SpeechAssessment, ContentAssessment và Cập nhật tiến độ
        return persistResults(command, ctx, azureAssessment, parsedScores);
    }

    private AnalysisContext prepareAnalysis(SpeakingAnalysisCommand command) {
        // DB-R (Database Reading) Lấy learning path node hiện tại của user
        LearningPathNode learningPathNode = learningPathNodeRepositoryPort
                .findBySpeakingQuestionId(command.speakingQuestionId())
                .orElseThrow(() -> new ApplicationException(
                        LearningPathNodeErrorCode.LEARNING_PATH_NODE_NOT_FOUND,
                        LearningPathNodeDetailMessageKey.LEARNING_PATH_NODE_ID_NOT_FOUND,
                        command.speakingQuestionId()));

        // DB-R (Database Reading) - Lấy tiến trình học của user
        UserLearningProgress progress = userLearningProgressRepositoryPort
                .findByUserId(command.userId())
                .orElseThrow(() -> new ApplicationException(
                        UserLearningProgressErrorCode.USER_LEARNING_PROGRESS_NOT_FOUND,
                        UserLearningProgressDetailMessageKey.USER_LEARNING_PROGRESS_NOT_FOUND_BY_USER_ID,
                        command.userId()));

        // VALID Logic
        // nếu node xa nhất người dùng có thể học còn nhỏ hơn node đang định học
        if (progress.getFarthestAvailableNodeGlobalOrderIndex() < learningPathNode.getGlobalOrderIndex()) {
            throw new ApplicationException(
                    SpeakingQuestionErrorCode.SPEAKING_QUESTION_LOCKED,
                    SpeakingQuestionDetailMessageKey.SPEAKING_QUESTION_LOCKED);
        }

        // DB-R (Database-Reading) lay du lieu cua nguoi dung
        User user = userRepositoryPort.findById(command.userId())
                .orElseThrow(() -> new ApplicationException(
                        UserErrorCode.USER_NOT_FOUND,
                        UserDetailMessageKey.USER_ID_NOT_FOUND));

        // DB-R (Database-Reading) lay du lieu cua speaking question khi nguoi dung truy cap node speaking question
        SpeakingQuestion speakingQuestion = speakingQuestionRepositoryPort
                .findById(command.speakingQuestionId())
                .orElseThrow(() -> new ApplicationException(
                        SpeakingQuestionErrorCode.SPEAKING_QUESTION_NOT_FOUND,
                        SpeakingQuestionDetailMessageKey.SPEAKING_QUESTION_NOT_FOUND));

        // DB-R
        // Lay du lieu cua object (question speaking nay nam trong objective nao)
        Objective objective = objectiveRepositoryPort.findBySpeakingQuestionId(speakingQuestion.getId()).orElse(null);
        // DB-R
        // Lay du lieu cua lesson (question speaking nay nam trong lesson nao)
        Lesson lesson = lessonRepositoryPort.findBySpeakingQuestionId(speakingQuestion.getId()).orElse(null);
        // DB-R
        // Lay du lieu cua topic (question speaking nay nam trong topic nao)
        Topic topic = topicRepositoryPort.findBySpeakingQuestionId(speakingQuestion.getId()).orElse(null);
        // DB-R
        // Lay du lieu cua book (question speaking nay nam trong book nao)
        Book book = bookRepositoryPort.findBySpeakingQuestionId(speakingQuestion.getId()).orElse(null);

        return buildAnalysisContext(
                learningPathNode, progress, speakingQuestion, user,
                objective, lesson, topic, book
        );
    }

    private AnswerHistory createAnswerHistory(User user, SpeakingQuestion speakingQuestion, File audioFile, Integer durationSec) {
        AnswerHistory answerHistory = AnswerHistory.builder()
                .userId(user.getId())
                .speakingQuestionId(speakingQuestion.getId())
                .durationSec(durationSec)
                .build();
        answerHistory.setAudioFileId(audioFile.getId());
        return answerHistoryRepositoryPort.save(answerHistory);
    }

    private AnalysisContext buildAnalysisContext(
            LearningPathNode learningPathNode,
            UserLearningProgress progress,
            SpeakingQuestion speakingQuestion,
            User user,
            Objective objective,
            Lesson lesson,
            Topic topic,
            Book book) {

        // VALID LOGIC
        String curriculumVal = (book != null) ? book.getTitle() : "N/A";
        String levelVal = (book != null && book.getJlptLevel() != null) ? book.getJlptLevel().name() : "N/A";
        String sttVal = (objective != null && objective.getOrderIndex() != null)
                ? String.valueOf(objective.getOrderIndex()) : "N/A";
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

        // VALID LOGIC-Lay 1 chuoi grammar cua 1 cau hoi
        String grammarFocusVal = buildGrammarFocusText(speakingQuestion);
        // VALID LOGIC-Lay danh sach vocab cua speaking question
        String vocabFocusVal = buildVocabFocusText(speakingQuestion);

        // VALID LOGIC
        String questionTitleVal = speakingQuestion.getJapaneseName() != null
                ? speakingQuestion.getJapaneseName() : "N/A";
        String questionDescriptionVal = (speakingQuestion.getDescription() != null
                && !speakingQuestion.getDescription().isBlank())
                ? speakingQuestion.getDescription() : questionTitleVal;

        return new AnalysisContext(
                learningPathNode, progress, speakingQuestion, user,
                curriculumVal, levelVal, sttVal, topicVal, lessonVal,
                canDoObjectiveVal, grammarFocusVal, vocabFocusVal,
                questionTitleVal, questionDescriptionVal);
    }

    private String buildGrammarFocusText(SpeakingQuestion speakingQuestion) {
        StringBuilder sb = new StringBuilder();
        if (speakingQuestion.getGrammars() != null) {
            for (Grammar grammar : speakingQuestion.getGrammars()) {
                if (!sb.isEmpty()) {
                    sb.append("\n");
                }
                String ja = grammar.getJapanese() != null ? grammar.getJapanese() : "";
                String vi = grammar.getVietnameseMeaningText() != null ? grammar.getVietnameseMeaningText() : "";
                if (!ja.isEmpty() && !vi.isEmpty()) {
                    sb.append("- ").append(ja).append("（").append(vi).append("）");
                } else if (!ja.isEmpty()) {
                    sb.append("- ").append(ja);
                } else {
                    sb.append("- ").append(vi);
                }
            }
        }
        return sb.isEmpty() ? "N/A (no specific grammar focus for this lesson)" : sb.toString();
    }

    private String buildVocabFocusText(SpeakingQuestion speakingQuestion) {
        StringBuilder sb = new StringBuilder();
        if (speakingQuestion.getVocabularies() != null) {
            for (var vocab : speakingQuestion.getVocabularies()) {
                if (!sb.isEmpty()) {
                    sb.append("\n");
                }
                String japanese = vocab.getJapanese() != null ? vocab.getJapanese() : "";
                String reading = vocab.getReading() != null ? vocab.getReading() : "";
                String vi = vocab.getVietnameseMeaningText() != null ? vocab.getVietnameseMeaningText() : "";
                StringBuilder entry = new StringBuilder("- ").append(japanese);
                if (!reading.isEmpty()) {
                    entry.append("（").append(reading).append("）");
                }
                if (!vi.isEmpty()) {
                    entry.append(" = ").append(vi);
                }
                sb.append(entry);
            }
        }
        return sb.isEmpty() ? "N/A (no specific vocabulary focus for this lesson)" : sb.toString();
    }

    private SpeechAssessment assessSpeech(byte[] audioBytes) {
        // EXT - Gọi Azure Speech Assessment
        return azureSpeechServicePort.assess(new SpeechAssessmentCommand(audioBytes, null));
    }

    private AiAnalysisPort.Context buildEvaluationContext(AnalysisContext ctx, SpeechAssessment azureResult) {
        // CALC - Tính toán thuần
        double accuracy = azureResult.getAccuracyScore() != null ? azureResult.getAccuracyScore() : 0.0;
        double fluency = azureResult.getFluencyScore() != null ? azureResult.getFluencyScore() : 0.0;
        double completeness = azureResult.getCompletenessScore() != null ? azureResult.getCompletenessScore() : 0.0;
        double pronunciation = azureResult.getPronunciationScore() != null ? azureResult.getPronunciationScore() : 0.0;
        String transcript = azureResult.getTranscriptText() != null ? azureResult.getTranscriptText() : "";

        return new AiAnalysisPort.Context(
                ctx.curriculumVal(), ctx.levelVal(), ctx.sttVal(),
                ctx.topicVal(), ctx.lessonVal(), ctx.canDoObjectiveVal(),
                ctx.grammarFocusVal(), ctx.vocabFocusVal(),
                ctx.questionTitleVal(), ctx.questionDescriptionVal(),
                accuracy, fluency, completeness, pronunciation, transcript);
    }

    private ParsedScores parseLlmFeedback(String rawLlmFeedback, SpeechAssessment azureResult, Integer durationSec) {
        // CALC - Tính toán thuần & parse response AI
        double pronScore10 = azureResult.getPronunciationScore() != null
                ? azureResult.getPronunciationScore() / 10.0 : 0.0;
        double fluencyScore10 = azureResult.getFluencyScore() != null
                ? azureResult.getFluencyScore() / 10.0 : 0.0;

        double vocabScore;
        double grammarScore;
        double naturalnessScore;
        double overallScore;
        String enrichedFeedbackJson;

        try {
            JsonNode root = objectMapper.readTree(rawLlmFeedback);
            vocabScore = Math.min(extractLlmScore(root, "vocabulary") / 2.5, 10.0);
            grammarScore = Math.min(extractLlmScore(root, "grammar") / 2.5, 10.0);
            naturalnessScore = Math.min(extractLlmScore(root, "naturalness") / 2.5, 10.0);
            overallScore = Math.round(
                    ((pronScore10 + fluencyScore10 + vocabScore + grammarScore + naturalnessScore) / 5.0) * 10.0
            ) / 10.0;
            enrichedFeedbackJson = enrichFeedbackJson(
                    root, azureResult, vocabScore, grammarScore, naturalnessScore, overallScore, durationSec);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ApplicationException(
                    SpeakingQuestionErrorCode.SPEAKING_QUESTION_EVALUATION_FAILED,
                    SpeakingQuestionDetailMessageKey.SPEAKING_QUESTION_EVALUATION_FAILED
            );
        }

        return new ParsedScores(vocabScore, grammarScore, naturalnessScore, overallScore, enrichedFeedbackJson);
    }

    private double extractLlmScore(JsonNode root, String fieldName) {
        if (root.has(fieldName) && root.path(fieldName).has("score")) {
            return root.path(fieldName).path("score").asDouble(0.0);
        }
        if (root.has("scores")) {
            return root.path("scores").path(fieldName).asDouble(0.0);
        }
        return 0.0;
    }

    private String enrichFeedbackJson(JsonNode root, SpeechAssessment azureResult,
                                      double vocabScore, double grammarScore, double naturalnessScore,
                                      double overallScore, Integer durationSec) throws Exception {
        if (!(root instanceof ObjectNode objectNode)) {
            return objectMapper.writeValueAsString(root);
        }

        objectNode.put("durationSec", durationSec);
        objectNode.put("overallScore", overallScore);

        ObjectNode scoresNode = objectMapper.createObjectNode();
        scoresNode.put("vocabulary", vocabScore);
        scoresNode.put("grammar", grammarScore);
        scoresNode.put("naturalness", naturalnessScore);
        objectNode.set("scores", scoresNode);

        String correctedJa = root.path("overall").path("correctedAnswerJa").asText("");
        String correctedVi = root.path("overall").path("correctedAnswerVi").asText("");
        ObjectNode sugNode = objectMapper.createObjectNode();
        sugNode.put("jp", correctedJa);
        sugNode.put("furigana", correctedJa);
        sugNode.put("vi", correctedVi);
        objectNode.set("aiSuggestion", sugNode);

        ArrayNode utNode = objectMapper.createArrayNode();
        JsonNode mistakes = root.path("grammar").path("mistakes");
        if (mistakes.isArray() && !mistakes.isEmpty()) {
            for (JsonNode mistake : mistakes) {
                ObjectNode errItem = objectMapper.createObjectNode();
                errItem.put("text", mistake.path("original").asText(""));
                ObjectNode errDetail = objectMapper.createObjectNode();
                errDetail.put("type", "Ngữ pháp / Sự tự nhiên");
                errDetail.put("explanation", mistake.path("explanationVi").asText(""));
                errDetail.put("suggestion", mistake.path("corrected").asText(""));
                errItem.set("error", errDetail);
                utNode.add(errItem);
            }
        } else {
            ObjectNode errItem = objectMapper.createObjectNode();
            errItem.put("text", azureResult.getTranscriptText() != null ? azureResult.getTranscriptText() : "");
            errItem.set("error", null);
            utNode.add(errItem);
        }
        objectNode.set("userTranscript", utNode);
        objectNode.put("pronunciationNote", root.path("overall").path("summaryVi").asText(""));

        if (!objectNode.has("wordNotes")) {
            objectNode.putObject("wordNotes");
        }
        if (!objectNode.has("expressions")) {
            objectNode.putArray("expressions");
        }
        if (!objectNode.has("itVocab")) {
            objectNode.putArray("itVocab");
        }

        return objectMapper.writeValueAsString(objectNode);
    }

//    private String buildFallbackFeedbackJson(double vocabScore, double grammarScore,
//                                             double naturalnessScore, double overallScore,
//                                             Integer durationSec) {
//        try {
//            ObjectNode node = objectMapper.createObjectNode();
//            ObjectNode scores = objectMapper.createObjectNode();
//            scores.put("vocabulary", vocabScore);
//            scores.put("grammar", grammarScore);
//            scores.put("naturalness", naturalnessScore);
//            node.set("scores", scores);
//            node.putArray("userTranscript");
//            ObjectNode sug = objectMapper.createObjectNode();
//            sug.put("jp", "");
//            sug.put("furigana", "");
//            sug.put("vi", "");
//            node.set("aiSuggestion", sug);
//            node.put("pronunciationNote", "");
//            node.putObject("wordNotes");
//            node.putArray("expressions");
//            node.putArray("itVocab");
//            node.put("durationSec", durationSec);
//            node.put("overallScore", overallScore);
//            return objectMapper.writeValueAsString(node);
//        } catch (Exception ex) {
//            return "{}";
//        }
//    }

    private List<WordPronunciationResult> buildWordPronunciations(List<WordAssessment> words) {
        if (words == null || words.isEmpty()) {
            return List.of();
        }
        return words.stream()
                .map(w -> WordPronunciationResult.from(
                        w.getWord(),
                        w.getAccuracyScore(),
                        w.getErrorType() != null ? w.getErrorType().name() : "None"
                ))
                .toList();
    }

    private SpeakingAnalysisResult persistResults(SpeakingAnalysisCommand command,
                                                  AnalysisContext ctx,
                                                  SpeechAssessment azureAssessment,
                                                  ParsedScores parsedScores) {
        // DB-W => Lưu file vào database với operation type là Upload
        File audioFile = fileRepositoryPort.createNewForUpload(
                command.storedFile(),
                FileAccessStatus.PRIVATE
        );

        // DB-W => Lưu lịch sử trả lời Speaking Question
        AnswerHistory savedAnswerHistory = createAnswerHistory(ctx.user(), ctx.speakingQuestion(), audioFile, command.durationSec());

        // DB-W => Lưu đánh giá phát âm tổng quan của azure speech
        SpeechAssessment savedSpeechAssessment = persistSpeechAssessment(
                azureAssessment, savedAnswerHistory.getId());

        // DB-W => Lưu đánh giá từng chữ (phát âm, điểm chính xác, lỗi phát âm) từ azure speech
        persistWordAssessments(azureAssessment.getWords(), savedSpeechAssessment.getId());

        // DB-W => Content Assessment (phản hồi từ AI)
        ContentAssessment contentAssessment = ContentAssessment.builder()
                .vocabularyScore(parsedScores.vocabScore())
                .grammarScore(parsedScores.grammarScore())
                .aiFeedback(parsedScores.enrichedFeedbackJson())
                .translationText("")
                .answerHistoryId(savedAnswerHistory.getId())
                .build();
        answerHistoryRepositoryPort.saveContentAssessment(contentAssessment);

        // DB-W => Hoàn thành câu hỏi nói và lưu tiến độ người dùng
        completeSpeakingQuestionInputPort.completeSpeakingQuestion(
                CompleteSpeakingQuestionCommand.builder()
                        .userLearningProgress(ctx.progress())
                        .speakingQuestionLearningPathNode(ctx.learningPathNode())
                        .userId(command.userId())
                        .overallScore(parsedScores.overallScore())
                        .build());

        // Gắn report đầy đủ bao gồm tô màu phát âm từng từ vào result
        List<WordPronunciationResult> wordPronunciations = buildWordPronunciations(azureAssessment.getWords());

        SpeakingAnalysisReportResult report = new SpeakingAnalysisReportResult(
                parsedScores.overallScore(),
                new SpeakingAnalysisReportResult.Scores(
                        azureAssessment.getPronunciationScore(),
                        azureAssessment.getFluencyScore(),
                        parsedScores.vocabScore(),
                        parsedScores.grammarScore(),
                        parsedScores.naturalnessScore()
                ),
                wordPronunciations,
                parsedScores.enrichedFeedbackJson()
        );

        SpeakingAnalysisResult result = SpeakingAnalysisResult.builder()
                .overallScore(parsedScores.overallScore())
                .answerHistoryId(savedAnswerHistory.getId())
                .report(report)
                .build();

        result.setAudioFile(fileResultMapperPort.domainToResult(audioFile));

        return result;
    }

    private SpeechAssessment persistSpeechAssessment(SpeechAssessment azureResult, Long answerHistoryId) {
        SpeechAssessment speechAssessment = SpeechAssessment.builder()
                .transcriptText(azureResult.getTranscriptText())
                .accuracyScore(azureResult.getAccuracyScore())
                .fluencyScore(azureResult.getFluencyScore())
                .completenessScore(azureResult.getCompletenessScore())
                .pronunciationScore(azureResult.getPronunciationScore())
                .answerHistoryId(answerHistoryId)
                .build();
        return answerHistoryRepositoryPort.saveSpeechAssessment(speechAssessment);
    }

    private void persistWordAssessments(List<WordAssessment> azureWords, Long speechAssessmentId) {
        if (azureWords == null || azureWords.isEmpty()) {
            return;
        }
        List<WordAssessment> wordList = new ArrayList<>();
        for (WordAssessment word : azureWords) {
            wordList.add(WordAssessment.builder()
                    .word(word.getWord())
                    .accuracyScore(word.getAccuracyScore())
                    .errorType(word.getErrorType())
                    .speechAssessmentId(speechAssessmentId)
                    .wordMarkup(furiganaGenerationPort.generateFuriganaMarkup(word.getWord()))
                    .build());
        }
        answerHistoryRepositoryPort.saveAllWordAssessment(wordList);
    }

    private record AnalysisContext(
            LearningPathNode learningPathNode,
            UserLearningProgress progress,
            SpeakingQuestion speakingQuestion,
            User user,
            String curriculumVal,
            String levelVal,
            String sttVal,
            String topicVal,
            String lessonVal,
            String canDoObjectiveVal,
            String grammarFocusVal,
            String vocabFocusVal,
            String questionTitleVal,
            String questionDescriptionVal
    ) {
    }

    private record ParsedScores(
            double vocabScore,
            double grammarScore,
            double naturalnessScore,
            double overallScore,
            String enrichedFeedbackJson
    ) {
    }
}
