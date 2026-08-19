package org.naho.speech.llm.question.helper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.naho.book.model.Book;
import org.naho.book.model.Lesson;
import org.naho.book.model.Objective;
import org.naho.book.model.Topic;
import org.naho.book.port.out.BookRepositoryPort;
import org.naho.book.port.out.LessonRepositoryPort;
import org.naho.book.port.out.ObjectiveRepositoryPort;
import org.naho.book.port.out.TopicRepositoryPort;
import org.naho.i18n.message.question.SpeakingQuestionDetailMessageKey;
import org.naho.question.exception.SpeakingQuestionErrorCode;
import org.naho.question.model.Grammar;
import org.naho.question.model.SpeakingQuestion;
import org.naho.question.port.out.SpeakingQuestionRepositoryPort;
import org.naho.shared.exception.ApplicationException;
import org.naho.speech.azure.result.SpeechAssessmentResult;
import org.naho.speech.llm.model.question.AiFeedback;
import org.naho.speech.llm.model.question.UsedVocabularyAndGrammar;
import org.naho.speech.llm.model.question.UserAnswerError;
import org.naho.speech.llm.question.command.QuestionContextCommand;
import org.naho.speech.llm.type.LanguageCategory;
import org.naho.vocabulary.model.Vocabulary;

import java.util.ArrayList;
import java.util.List;

public class SpeakingAnalysisHelper {
    private final SpeakingQuestionRepositoryPort speakingQuestionRepositoryPort;
    private final BookRepositoryPort bookRepositoryPort;
    private final TopicRepositoryPort topicRepositoryPort;
    private final LessonRepositoryPort lessonRepositoryPort;
    private final ObjectiveRepositoryPort objectiveRepositoryPort;
    private final ObjectMapper objectMapper;

    public SpeakingAnalysisHelper(
            SpeakingQuestionRepositoryPort speakingQuestionRepositoryPort,
            BookRepositoryPort bookRepositoryPort,
            TopicRepositoryPort topicRepositoryPort,
            LessonRepositoryPort lessonRepositoryPort,
            ObjectiveRepositoryPort objectiveRepositoryPort
    ) {
        this.speakingQuestionRepositoryPort = speakingQuestionRepositoryPort;
        this.bookRepositoryPort = bookRepositoryPort;
        this.topicRepositoryPort = topicRepositoryPort;
        this.lessonRepositoryPort = lessonRepositoryPort;
        this.objectiveRepositoryPort = objectiveRepositoryPort;
        this.objectMapper = new ObjectMapper();
    }

    public QuestionContextCommand buildContextCommand(Long speakingQuestionId, String transcript) {
        SpeakingQuestion speakingQuestion = speakingQuestionRepositoryPort
                .findById(speakingQuestionId)
                .orElseThrow(() -> new ApplicationException(
                        SpeakingQuestionErrorCode.SPEAKING_QUESTION_NOT_FOUND,
                        SpeakingQuestionDetailMessageKey.SPEAKING_QUESTION_NOT_FOUND
                ));

        Book book = bookRepositoryPort.findBySpeakingQuestionId(speakingQuestionId).orElse(null);
        Topic topic = topicRepositoryPort.findBySpeakingQuestionId(speakingQuestionId).orElse(null);
        Lesson lesson = lessonRepositoryPort.findBySpeakingQuestionId(speakingQuestionId).orElse(null);
        Objective objective = objectiveRepositoryPort.findBySpeakingQuestionId(speakingQuestionId).orElse(null);

        String curriculumTitle = (book != null) ? book.getTitle() : "N/A";
        String jlptLevel = (book != null && book.getJlptLevel() != null) ? book.getJlptLevel().name() : "N/A";
        String lessonOrderIndex = (objective != null && objective.getOrderIndex() != null)
                ? String.valueOf(objective.getOrderIndex()) : "N/A";
        String topicName = (topic != null) ? topic.getJapaneseName() : "N/A";
        String lessonName = (lesson != null) ? lesson.getJapaneseName() : "N/A";
        String canDoObjective = (objective != null)
                ? (objective.getJapaneseDescription() != null && !objective.getJapaneseDescription().isBlank()
                ? objective.getJapaneseDescription() : objective.getJapaneseName())
                : "N/A";

        String grammarFocus = buildGrammarFocusText(speakingQuestion);
        String vocabularyFocus = buildVocabFocusText(speakingQuestion);

        String questionTitle = speakingQuestion.getJapaneseName() != null
                ? speakingQuestion.getJapaneseName() : "N/A";
        String questionDescription = (speakingQuestion.getDescription() != null
                && !speakingQuestion.getDescription().isBlank())
                ? speakingQuestion.getDescription() : questionTitle;

        return new QuestionContextCommand(
                curriculumTitle,
                jlptLevel,
                lessonOrderIndex,
                topicName,
                lessonName,
                canDoObjective,
                grammarFocus,
                vocabularyFocus,
                questionTitle,
                questionDescription,
                transcript != null ? transcript : ""
        );
    }

    public AiFeedback parseLlmResponse(String rawJson, SpeechAssessmentResult speechAssessmentResult) {
        try {
            JsonNode root = objectMapper.readTree(rawJson);

            double grammarScore = root.path("grammarScore").asDouble(0.0);
            double vocabularyScore = root.path("vocabularyScore").asDouble(0.0);
            double naturalnessScore = root.path("naturalnessScore").asDouble(0.0);
            double contentRelevantScore = root.path("contentRelevantScore").asDouble(0.0);
            String suggestJapaneseAnswer = root.path("suggestJapaneseAnswer").asText("");
            String suggestAnswerTranslation = root.path("suggestAnswerTranslation").asText("");

            List<UsedVocabularyAndGrammar> usedList = new ArrayList<>();
            JsonNode usedNode = root.path("usedVocabulariesAndGrammars");
            if (usedNode.isArray()) {
                for (JsonNode item : usedNode) {
                    String expression = item.path("expression").asText("");
                    String categoryString = item.path("category").asText("VOCABULARY");
                    LanguageCategory category = parseLanguageCategory(categoryString);
                    usedList.add(UsedVocabularyAndGrammar.builder()
                            .expression(expression)
                            .category(category)
                            .build());
                }
            }

            List<UserAnswerError> errorList = new ArrayList<>();
            JsonNode errorsNode = root.has("userAnswerErrors")
                    ? root.path("userAnswerErrors")
                    : root.path("errors");
            if (errorsNode.isArray()) {
                for (JsonNode item : errorsNode) {
                    String incorrect = item.path("incorrect").asText("");
                    String correction = item.path("correction").asText("");
                    errorList.add(UserAnswerError.builder()
                            .incorrect(incorrect)
                            .correction(correction)
                            .build());
                }
            }

            double averageScore = (grammarScore + vocabularyScore + naturalnessScore + contentRelevantScore) / 4.0;

            return AiFeedback.builder()
                    .grammarScore(grammarScore)
                    .vocabularyScore(vocabularyScore)
                    .naturalnessScore(naturalnessScore)
                    .contentRelevantScore(contentRelevantScore)
                    .averageScore(averageScore)
                    .suggestJapaneseAnswer(suggestJapaneseAnswer)
                    .suggestAnswerTranslation(suggestAnswerTranslation)
                    .usedVocabulariesAndGrammars(usedList)
                    .userAnswerErrors(errorList)
                    .build();

        } catch (Exception e) {
            throw new ApplicationException(
                    SpeakingQuestionErrorCode.SPEAKING_QUESTION_EVALUATION_FAILED,
                    SpeakingQuestionDetailMessageKey.SPEAKING_QUESTION_EVALUATION_FAILED
            );
        }
    }




    public LanguageCategory parseLanguageCategory(String rawCategory) {
        if (rawCategory == null) return LanguageCategory.VOCABULARY;
        return switch (rawCategory.trim().toUpperCase()) {
            case "GRAMMAR" -> LanguageCategory.GRAMMAR;
            default -> LanguageCategory.VOCABULARY;
        };
    }

    public String buildGrammarFocusText(SpeakingQuestion speakingQuestion) {
        StringBuilder sb = new StringBuilder();
        if (speakingQuestion.getGrammars() != null) {
            for (Grammar grammar : speakingQuestion.getGrammars()) {
                if (!sb.isEmpty()) sb.append("\n");
                String japanese = grammar.getJapanese() != null ? grammar.getJapanese() : "";
                String vietnameseMeaning = grammar.getVietnameseMeaningText() != null ? grammar.getVietnameseMeaningText() : "";
                if (!japanese.isEmpty() && !vietnameseMeaning.isEmpty()) {
                    sb.append("- ").append(japanese).append("（").append(vietnameseMeaning).append("）");
                } else if (!japanese.isEmpty()) {
                    sb.append("- ").append(japanese);
                } else {
                    sb.append("- ").append(vietnameseMeaning);
                }
            }
        }
        return sb.isEmpty() ? "N/A (no specific grammar focus for this lesson)" : sb.toString();
    }

    public String buildVocabFocusText(SpeakingQuestion speakingQuestion) {
        StringBuilder sb = new StringBuilder();
        if (speakingQuestion.getVocabularies() != null) {
            for (Vocabulary vocab : speakingQuestion.getVocabularies()) {
                if (!sb.isEmpty()) sb.append("\n");
                String japanese = vocab.getJapanese() != null ? vocab.getJapanese() : "";
                String reading = vocab.getReading() != null ? vocab.getReading() : "";
                String vietnameseMeaning = vocab.getVietnameseMeaningText() != null ? vocab.getVietnameseMeaningText() : "";
                StringBuilder entry = new StringBuilder("- ").append(japanese);
                if (!reading.isEmpty()) entry.append("（").append(reading).append("）");
                if (!vietnameseMeaning.isEmpty()) entry.append(" = ").append(vietnameseMeaning);
                sb.append(entry);
            }
        }
        return sb.isEmpty() ? "N/A (no specific vocabulary focus for this lesson)" : sb.toString();
    }
}
