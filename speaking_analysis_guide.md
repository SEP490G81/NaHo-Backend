# Cẩm Nang Tự Code Chi Tiết: Speaking Analysis Flow (Azure Speech + OpenAI)

Tài liệu này hướng dẫn chi tiết từng bước, cấu trúc thư mục, các file cần tạo mới/chỉnh sửa, cùng toàn bộ mã nguồn mẫu để bạn tự lập trình và hiểu sâu luồng phân tích phát âm (`POST /api/v1/analysis` và `GET /api/v1/history/{historyId}`).

---

## 1. Bản Đồ File (File Roadmap)

Bạn cần tạo mới và chỉnh sửa các file theo đúng cấu trúc thư mục dưới đây để đảm bảo Clean Architecture không bị vi phạm:

### A. Tầng Application (`:core:application`)
1. **[NEW]** `org.naho.speech.analysis.command.SpeakingAnalysisCommand` (Command chứa dữ liệu đầu vào)
2. **[NEW]** `org.naho.speech.analysis.result.SpeakingAnalysisResult` (Kết quả trả về của hàm phân tích)
3. **[NEW]** `org.naho.speech.analysis.result.SpeakingHistoryDetailResult` (Kết quả chi tiết báo cáo lịch sử)
4. **[NEW]** `org.naho.speech.analysis.port.in.SpeakingAnalysisInputPort` (Cổng vào - Interface định nghĩa API nghiệp vụ)
5. **[NEW]** `org.naho.speech.analysis.port.out.AiAnalysisPort` (Cổng ra để giao tiếp với OpenAI)
6. **[NEW]** `org.naho.speech.analysis.port.out.AnswerHistoryRepositoryPort` (Cổng ra lưu trữ dữ liệu nghiệp vụ bài làm)
7. **[NEW]** `org.naho.speech.analysis.usecase.SpeakingAnalysisUseCase` (Thực thi nghiệp vụ phân tích bài nói)
8. **[MODIFY]** `org.naho.speech.question.port.out.QuestionRepositoryPort` (Thêm hàm tìm Question theo ID)

### B. Tầng Infrastructure (`:core:infrastructure`)
9. **[NEW]** `org.naho.speech.question.entity.AnswerHistoryEntity` (JPA Entity cho bảng `answer_histories`)
10. **[NEW]** `org.naho.speech.azure.entity.SpeechAssessmentEntity` (JPA Entity lưu điểm tổng quát từ Azure)
11. **[NEW]** `org.naho.speech.azure.entity.WordAssessmentEntity` (JPA Entity lưu điểm từng từ từ Azure)
12. **[NEW]** `org.naho.speech.azure.entity.ContentAssessmentEntity` (JPA Entity lưu điểm ngữ pháp/từ vựng & AI feedback)
13. **[NEW]** `org.naho.speech.question.repository.AnswerHistoryJpaRepository` (Spring Data JPA Repo)
14. **[NEW]** `org.naho.speech.azure.repository.SpeechAssessmentJpaRepository` (Spring Data JPA Repo)
15. **[NEW]** `org.naho.speech.azure.repository.WordAssessmentJpaRepository` (Spring Data JPA Repo)
16. **[NEW]** `org.naho.speech.azure.repository.ContentAssessmentJpaRepository` (Spring Data JPA Repo)
17. **[NEW]** `org.naho.speech.analysis.adapter.AnswerHistoryRepositoryAdapter` (Thực thi cổng ra lưu trữ - Mapping Domain <-> Entity)
18. **[NEW]** `org.naho.speech.analysis.adapter.OpenAiAnalysisAdapter` (Thực thi cổng gọi OpenAI LLM)
19. **[MODIFY]** `org.naho.speech.question.adapter.QuestionRepositoryAdapter` (Thực thi hàm tìm Question)

### C. Tầng Presentation (`:core:presentation`)
20. **[NEW]** `org.naho.speech.analysis.dto.response.SpeakingAnalysisResponse` (Response trả về sau khi phân tích)
21. **[NEW]** `org.naho.speech.analysis.dto.response.SpeakingHistoryDetailResponse` (Response báo cáo chi tiết)
22. **[NEW]** `org.naho.speech.analysis.dto.mapper.SpeakingAnalysisMapper` (Chuyển đổi Result nghiệp vụ -> Response DTO)
23. **[NEW]** `org.naho.speech.analysis.controller.v1.SpeakingAnalysisController` (Định nghĩa REST Endpoints)

---

## 2. Chi Tiết Các File & Code Mẫu

### 2.1. Tầng Application (Nghiệp vụ và Cổng giao tiếp)

#### File 1: `SpeakingAnalysisCommand.java`
Đường dẫn: `core/application/src/main/java/org/naho/speech/analysis/command/SpeakingAnalysisCommand.java`
```java
package org.naho.speech.analysis.command;

public record SpeakingAnalysisCommand(
        Long userId,
        Long topicId,
        Long questionId,
        byte[] audioBytes,
        String contentType,
        String originalFileName,
        Integer durationSec
) {}
```

#### File 2: `SpeakingAnalysisResult.java`
Đường dẫn: `core/application/src/main/java/org/naho/speech/analysis/result/SpeakingAnalysisResult.java`
```java
package org.naho.speech.analysis.result;

public record SpeakingAnalysisResult(
        Long historyId,
        Double score
) {}
```

#### File 3: `SpeakingHistoryDetailResult.java`
Đường dẫn: `core/application/src/main/java/org/naho/speech/analysis/result/SpeakingHistoryDetailResult.java`
```java
package org.naho.speech.analysis.result;

import java.time.Instant;
import java.util.List;

public record SpeakingHistoryDetailResult(
        Long historyId,
        Long topicId,
        Long questionId,
        Instant practicedAt,
        Integer durationSec,
        Double score,
        String audioUrl,
        Report report
) {
    public record Report(
            Double average,
            Scores scores,
            List<UserTranscriptItem> userTranscript,
            AiSuggestion aiSuggestion,
            List<PronunciationItem> pronunciation,
            String pronunciationNote,
            List<ExpressionItem> expressions,
            List<ItVocabItem> itVocab
    ) {}

    public record Scores(
            Double pronunciation,
            Double vocabulary,
            Double grammar,
            Double naturalness
    ) {}

    public record UserTranscriptItem(
            String text,
            ErrorDetail error
    ) {}

    public record ErrorDetail(
            String type,
            String explanation,
            String suggestion
    ) {}

    public record AiSuggestion(
            String jp,
            String furigana,
            String vi
    ) {}

    public record PronunciationItem(
            String text,
            String furigana,
            String severity,
            String note
    ) {}

    public record ExpressionItem(
            String jp,
            String furigana,
            String vi,
            String note
    ) {}

    public record ItVocabItem(
            String term,
            String reading,
            String meaning
    ) {}
}
```

#### File 4: `SpeakingAnalysisInputPort.java`
Đường dẫn: `core/application/src/main/java/org/naho/speech/analysis/port/in/SpeakingAnalysisInputPort.java`
```java
package org.naho.speech.analysis.port.in;

import org.naho.speech.analysis.command.SpeakingAnalysisCommand;
import org.naho.speech.analysis.result.SpeakingAnalysisResult;
import org.naho.speech.analysis.result.SpeakingHistoryDetailResult;

public interface SpeakingAnalysisInputPort {
    SpeakingAnalysisResult analyze(SpeakingAnalysisCommand command);
    SpeakingHistoryDetailResult getHistoryDetail(Long historyId);
}
```

#### File 5: `AiAnalysisPort.java`
Đường dẫn: `core/application/src/main/java/org/naho/speech/analysis/port/out/AiAnalysisPort.java`
```java
package org.naho.speech.analysis.port.out;

public interface AiAnalysisPort {
    String analyzeSpeaking(String topic, String question, String studentTranscript, String azureWordFeedbackJson);
}
```

#### File 6: `AnswerHistoryRepositoryPort.java`
Đường dẫn: `core/application/src/main/java/org/naho/speech/analysis/port/out/AnswerHistoryRepositoryPort.java`
```java
package org.naho.speech.analysis.port.out;

import org.naho.speech.model.AnswerHistory;
import org.naho.speech.model.SpeechAssessment;
import org.naho.speech.model.ContentAssessment;
import org.naho.speech.model.WordAssessment;

import java.util.List;
import java.util.Optional;

public interface AnswerHistoryRepositoryPort {
    AnswerHistory saveAnswerHistory(AnswerHistory domain);
    SpeechAssessment saveSpeechAssessment(SpeechAssessment domain);
    ContentAssessment saveContentAssessment(ContentAssessment domain);
    List<WordAssessment> saveAllWordAssessments(List<WordAssessment> domains);

    Optional<AnswerHistory> findAnswerHistoryById(Long id);
    Optional<SpeechAssessment> findSpeechAssessmentByAnswerHistoryId(Long answerHistoryId);
    Optional<ContentAssessment> findContentAssessmentByAnswerHistoryId(Long answerHistoryId);
}
```

#### File 7: `QuestionRepositoryPort.java` (Sửa đổi)
Đường dẫn: `core/application/src/main/java/org/naho/speech/question/port/out/QuestionRepositoryPort.java`
*Thêm dòng sau vào interface:*
```java
import org.naho.topic.model.Question;
import java.util.Optional;

// Thêm phương thức này:
Optional<Question> findById(Long id);
```

#### File 8: `SpeakingAnalysisUseCase.java`
Đường dẫn: `core/application/src/main/java/org/naho/speech/analysis/usecase/SpeakingAnalysisUseCase.java`
*Chú ý: Không import bất kỳ Entity nào ở đây! Dùng ports để lấy thông tin.*
```java
package org.naho.speech.analysis.usecase;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.naho.file.command.FileUploadCommand;
import org.naho.file.constant.S3Properties;
import org.naho.file.port.in.FileStorageInputPort;
import org.naho.file.port.out.FileRepositoryPort;
import org.naho.file.result.FileResult;
import org.naho.furigana.port.out.FuriganaAnalysisPort;
import org.naho.shared.exception.ApplicationException;
import org.naho.speech.analysis.command.SpeakingAnalysisCommand;
import org.naho.speech.analysis.port.in.SpeakingAnalysisInputPort;
import org.naho.speech.analysis.port.out.AiAnalysisPort;
import org.naho.speech.analysis.port.out.AnswerHistoryRepositoryPort;
import org.naho.speech.analysis.result.SpeakingAnalysisResult;
import org.naho.speech.analysis.result.SpeakingHistoryDetailResult;
import org.naho.speech.azure.command.SpeechAssessmentCommand;
import org.naho.speech.azure.port.out.AzureSpeechServicePort;
import org.naho.speech.model.AnswerHistory;
import org.naho.speech.model.ContentAssessment;
import org.naho.speech.model.SpeechAssessment;
import org.naho.speech.model.WordAssessment;
import org.naho.speech.question.port.out.QuestionRepositoryPort;
import org.naho.speech.topic.port.out.TopicRepositoryPort;
import org.naho.speech.type.SpeechAssessmentErrorType;
import org.naho.topic.model.Question;
import org.naho.topic.model.Topic;
import org.naho.user.model.User;
import org.naho.user.port.out.UserRepositoryPort;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.*;

@Slf4j
public class SpeakingAnalysisUseCase implements SpeakingAnalysisInputPort {

    private final UserRepositoryPort userRepositoryPort;
    private final QuestionRepositoryPort questionRepositoryPort;
    private final TopicRepositoryPort topicRepositoryPort;
    private final FileStorageInputPort fileStorageInputPort;
    private final FileRepositoryPort fileRepositoryPort;
    private final AnswerHistoryRepositoryPort answerHistoryRepositoryPort;
    private final AzureSpeechServicePort azureSpeechServicePort;
    private final AiAnalysisPort aiAnalysisPort;
    private final FuriganaAnalysisPort furiganaAnalysisPort;
    private final S3Properties s3Properties;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public SpeakingAnalysisUseCase(
            UserRepositoryPort userRepositoryPort,
            QuestionRepositoryPort questionRepositoryPort,
            TopicRepositoryPort topicRepositoryPort,
            FileStorageInputPort fileStorageInputPort,
            FileRepositoryPort fileRepositoryPort,
            AnswerHistoryRepositoryPort answerHistoryRepositoryPort,
            AzureSpeechServicePort azureSpeechServicePort,
            AiAnalysisPort aiAnalysisPort,
            FuriganaAnalysisPort furiganaAnalysisPort,
            S3Properties s3Properties
    ) {
        this.userRepositoryPort = userRepositoryPort;
        this.questionRepositoryPort = questionRepositoryPort;
        this.topicRepositoryPort = topicRepositoryPort;
        this.fileStorageInputPort = fileStorageInputPort;
        this.fileRepositoryPort = fileRepositoryPort;
        this.answerHistoryRepositoryPort = answerHistoryRepositoryPort;
        this.azureSpeechServicePort = azureSpeechServicePort;
        this.aiAnalysisPort = aiAnalysisPort;
        this.furiganaAnalysisPort = furiganaAnalysisPort;
        this.s3Properties = s3Properties;
    }

    @Override
    public SpeakingAnalysisResult analyze(SpeakingAnalysisCommand command) {
        log.info("[SpeakingAnalysis] Starting analysis for user: {}, question: {}", command.userId(), command.questionId());

        User user = userRepositoryPort.findById(command.userId())
                .orElseThrow(() -> new ApplicationException("User not found with id: " + command.userId()));

        Question question = questionRepositoryPort.findById(command.questionId())
                .orElseThrow(() -> new ApplicationException("Question not found with id: " + command.questionId()));

        // Upload file WAV/WEBM ghi âm lên AWS S3
        FileUploadCommand uploadCommand = new FileUploadCommand(
                "recordings",
                command.originalFileName() != null ? command.originalFileName() : "recording.wav",
                new ByteArrayInputStream(command.audioBytes()),
                command.contentType() != null ? command.contentType() : "audio/wav",
                (long) command.audioBytes().length
        );
        FileResult fileResult = fileStorageInputPort.upload(uploadCommand);

        // Lưu lịch sử AnswerHistory
        AnswerHistory answerHistory = AnswerHistory.builder()
                .userId(user.getId())
                .questionId(question.getId())
                .audioFileId(fileResult.id())
                .build();
        answerHistory = answerHistoryRepositoryPort.saveAnswerHistory(answerHistory);

        // Gọi Azure Speech API
        SpeechAssessmentCommand azureCommand = new SpeechAssessmentCommand(command.audioBytes(), null);
        SpeechAssessment azureAssessment = azureSpeechServicePort.assess(azureCommand);

        // Lưu SpeechAssessment
        SpeechAssessment speechAssessment = SpeechAssessment.builder()
                .transcriptText(azureAssessment.getTranscriptText())
                .accuracyScore(azureAssessment.getAccuracyScore())
                .fluencyScore(azureAssessment.getFluencyScore())
                .completenessScore(azureAssessment.getCompletenessScore())
                .pronunciationScore(azureAssessment.getPronunciationScore())
                .answerHistoryId(answerHistory.getId())
                .build();
        speechAssessment = answerHistoryRepositoryPort.saveSpeechAssessment(speechAssessment);

        // Lưu danh sách điểm từng từ
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

            // Gom thông tin lỗi phát âm gửi sang OpenAI phân tích
            serializedWordList.add(Map.of(
                    "word", word.getWord(),
                    "score", word.getAccuracyScore(),
                    "error", word.getErrorType().name()
            ));
        }
        answerHistoryRepositoryPort.saveAllWordAssessments(wordList);

        // Gọi OpenAI phân tích ngữ pháp, từ vựng và tự nhiên hóa câu
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
            JsonNode root = objectMapper.readTree(rawLlmFeedback);
            JsonNode scores = root.path("scores");
            vocabScore = scores.path("vocabulary").asDouble(0.0);
            grammarScore = scores.path("grammar").asDouble(0.0);
            naturalnessScore = scores.path("naturalness").asDouble(0.0);

            // Tính điểm tổng (overall score): trung bình cộng của Phát âm (chia hệ 10), Từ vựng, Ngữ pháp, Độ tự nhiên
            double pronScore10 = azureAssessment.getPronunciationScore() / 10.0;
            double avgScore = (pronScore10 + vocabScore + grammarScore + naturalnessScore) / 4.0;
            avgScore = Math.round(avgScore * 10.0) / 10.0;

            if (root instanceof ObjectNode objectNode) {
                objectNode.put("durationSec", command.durationSec());
                objectNode.put("overallScore", avgScore);
                rawLlmFeedback = objectMapper.writeValueAsString(objectNode);
            }
        } catch (Exception e) {
            log.error("Failed to parse LLM feedback scores", e);
        }

        // Lưu thông tin ContentAssessment
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
        log.info("[SpeakingAnalysis] Loading history detail for id: {}", historyId);

        AnswerHistory history = answerHistoryRepositoryPort.findAnswerHistoryById(historyId)
                .orElseThrow(() -> new ApplicationException("Answer history not found with id: " + historyId));

        SpeechAssessment speech = answerHistoryRepositoryPort.findSpeechAssessmentByAnswerHistoryId(historyId)
                .orElseThrow(() -> new ApplicationException("Speech assessment not found for history id: " + historyId));

        ContentAssessment content = answerHistoryRepositoryPort.findContentAssessmentByAnswerHistoryId(historyId)
                .orElseThrow(() -> new ApplicationException("Content assessment not found for history id: " + historyId));

        JsonNode root;
        try {
            root = objectMapper.readTree(content.getAiFeedback());
        } catch (Exception e) {
            throw new ApplicationException("Corrupted AI feedback data");
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

        // Phân tích cấu trúc UserTranscript
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

        // Đề xuất AI Suggestion
        JsonNode sugNode = root.path("aiSuggestion");
        SpeakingHistoryDetailResult.AiSuggestion aiSuggestion = new SpeakingHistoryDetailResult.AiSuggestion(
                sugNode.path("jp").asText(""),
                sugNode.path("furigana").asText(""),
                sugNode.path("vi").asText("")
        );

        // Đọc feedback từng từ (wordNotes) từ LLM
        Map<String, String> wordNotes = new HashMap<>();
        JsonNode wordNotesNode = root.path("wordNotes");
        if (wordNotesNode.isObject()) {
            wordNotesNode.fields().forEachRemaining(entry -> {
                wordNotes.put(entry.getKey().toLowerCase(), entry.getValue().asText());
            });
        }

        // Tự động phân tích Furigana và độ nghiêm trọng lỗi cho từng từ từ Azure
        List<SpeakingHistoryDetailResult.PronunciationItem> pronunciation = new ArrayList<>();
        if (speech.getWords() != null) {
            for (WordAssessment wordEntity : speech.getWords()) {
                String wordText = wordEntity.getWord();
                double accScore = wordEntity.getAccuracyScore();
                SpeechAssessmentErrorType errType = wordEntity.getErrorType();

                String severity = "ok";
                if (errType == SpeechAssessmentErrorType.Mispronunciation || accScore < 50) {
                    severity = "error";
                } else if (errType == SpeechAssessmentErrorType.Omission || accScore < 80) {
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

        // Biểu mẫu câu mở rộng (Expressions)
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

        // Từ vựng chuyên ngành IT (itVocab)
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
        String audioUrl = String.format("https://%s.s3.%s.amazonaws.com/%s",
                s3Properties.getBucketName(),
                s3Properties.getRegion(),
                objectKey
        );

        return new SpeakingHistoryDetailResult(
                history.getId(),
                history.getQuestionId(), // bạn có thể bổ sung load topicId nếu cần thiết
                history.getQuestionId(),
                Instant.now(), // Thay thế bằng CreatedTime thực tế từ DB của history
                durationSec,
                overallScore,
                audioUrl,
                report
        );
    }
}
```

---

### 2.2. Tầng Infrastructure (Database Entities, JpaRepositories & Adapters)

#### File 9: `AnswerHistoryEntity.java`
Đường dẫn: `core/infrastructure/src/main/java/org/naho/speech/question/entity/AnswerHistoryEntity.java`
```java
package org.naho.speech.question.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import org.naho.file.model.FileEntity;
import org.naho.shared.persistence.BaseEntity;
import org.naho.user.entity.UserEntity;

@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "answer_histories")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AnswerHistoryEntity extends BaseEntity {
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    UserEntity user;

    @ManyToOne
    @JoinColumn(name = "question_id", nullable = false)
    QuestionEntity question;

    @OneToOne
    @JoinColumn(name = "audio_file_id", nullable = false)
    FileEntity audioFile;
}
```

#### File 10: `SpeechAssessmentEntity.java`
Đường dẫn: `core/infrastructure/src/main/java/org/naho/speech/azure/entity/SpeechAssessmentEntity.java`
```java
package org.naho.speech.azure.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import org.naho.shared.persistence.BaseEntity;
import org.naho.speech.question.entity.AnswerHistoryEntity;

import java.util.List;

@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "speech_assessments")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SpeechAssessmentEntity extends BaseEntity {
    @Column(name = "transcript_text", nullable = false, columnDefinition = "TEXT")
    String transcriptText;

    @Column(name = "accuracy_score", nullable = false)
    Double accuracyScore;

    @Column(name = "fluency_score", nullable = false)
    Double fluencyScore;

    @Column(name = "completeness_score", nullable = false)
    Double completenessScore;

    @Column(name = "pronunciation_score", nullable = false)
    Double pronunciationScore;

    @OneToMany(mappedBy = "speechAssessment", cascade = CascadeType.ALL)
    List<WordAssessmentEntity> words;

    @OneToOne
    @JoinColumn(name = "answer_history_id", nullable = false)
    AnswerHistoryEntity answerHistory;
}
```

#### File 11: `WordAssessmentEntity.java`
Đường dẫn: `core/infrastructure/src/main/java/org/naho/speech/azure/entity/WordAssessmentEntity.java`
```java
package org.naho.speech.azure.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import org.naho.shared.persistence.BaseEntity;
import org.naho.speech.type.SpeechAssessmentErrorType;

@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "word_assessments")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class WordAssessmentEntity extends BaseEntity {
    @Column(nullable = false)
    String word;

    @Column(name = "accuracy_score", nullable = false)
    Double accuracyScore;

    @Column(name = "error_type", nullable = false)
    @Enumerated(EnumType.STRING)
    SpeechAssessmentErrorType errorType;

    @ManyToOne
    @JoinColumn(name = "speech_assessment_id", nullable = false)
    SpeechAssessmentEntity speechAssessment;
}
```

#### File 12: `ContentAssessmentEntity.java`
Đường dẫn: `core/infrastructure/src/main/java/org/naho/speech/azure/entity/ContentAssessmentEntity.java`
```java
package org.naho.speech.azure.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import org.naho.shared.persistence.BaseEntity;
import org.naho.speech.question.entity.AnswerHistoryEntity;

@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "content_assessments")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ContentAssessmentEntity extends BaseEntity {
    @Column(name = "vocabulary_score", nullable = false)
    Double vocabularyScore;

    @Column(name = "grammar_score", nullable = false)
    Double grammarScore;

    @Column(name = "ai_feedback", columnDefinition = "TEXT", nullable = false)
    String aiFeedback;

    @Column(name = "translation_text", columnDefinition = "TEXT")
    String translationText;

    @OneToOne
    @JoinColumn(name = "answer_history_id", nullable = false)
    AnswerHistoryEntity answerHistory;
}
```

#### File 13: `AnswerHistoryJpaRepository.java`
Đường dẫn: `core/infrastructure/src/main/java/org/naho/speech/question/repository/AnswerHistoryJpaRepository.java`
```java
package org.naho.speech.question.repository;

import org.naho.speech.question.entity.AnswerHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AnswerHistoryJpaRepository extends JpaRepository<AnswerHistoryEntity, Long> {
}
```

#### File 14: `SpeechAssessmentJpaRepository.java`
Đường dẫn: `core/infrastructure/src/main/java/org/naho/speech/azure/repository/SpeechAssessmentJpaRepository.java`
```java
package org.naho.speech.azure.repository;

import org.naho.speech.azure.entity.SpeechAssessmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SpeechAssessmentJpaRepository extends JpaRepository<SpeechAssessmentEntity, Long> {
    Optional<SpeechAssessmentEntity> findByAnswerHistoryId(Long answerHistoryId);
}
```

#### File 15: `WordAssessmentJpaRepository.java`
Đường dẫn: `core/infrastructure/src/main/java/org/naho/speech/azure/repository/WordAssessmentJpaRepository.java`
```java
package org.naho.speech.azure.repository;

import org.naho.speech.azure.entity.WordAssessmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WordAssessmentJpaRepository extends JpaRepository<WordAssessmentEntity, Long> {
}
```

#### File 16: `ContentAssessmentJpaRepository.java`
Đường dẫn: `core/infrastructure/src/main/java/org/naho/speech/azure/repository/ContentAssessmentJpaRepository.java`
```java
package org.naho.speech.azure.repository;

import org.naho.speech.azure.entity.ContentAssessmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ContentAssessmentJpaRepository extends JpaRepository<ContentAssessmentEntity, Long> {
    Optional<ContentAssessmentEntity> findByAnswerHistoryId(Long answerHistoryId);
}
```

#### File 17: `AnswerHistoryRepositoryAdapter.java`
Đường dẫn: `core/infrastructure/src/main/java/org/naho/speech/analysis/adapter/AnswerHistoryRepositoryAdapter.java`
*Nhiệm vụ: Adapter này thực hiện biến đổi qua lại giữa Domain Model và JPA Entity trước khi ghi/đọc.*
```java
package org.naho.speech.analysis.adapter;

import lombok.RequiredArgsConstructor;
import org.naho.file.model.FileEntity;
import org.naho.file.repository.FileJpaRepository;
import org.naho.speech.analysis.port.out.AnswerHistoryRepositoryPort;
import org.naho.speech.azure.entity.ContentAssessmentEntity;
import org.naho.speech.azure.entity.SpeechAssessmentEntity;
import org.naho.speech.azure.entity.WordAssessmentEntity;
import org.naho.speech.azure.repository.ContentAssessmentJpaRepository;
import org.naho.speech.azure.repository.SpeechAssessmentJpaRepository;
import org.naho.speech.azure.repository.WordAssessmentJpaRepository;
import org.naho.speech.model.AnswerHistory;
import org.naho.speech.model.ContentAssessment;
import org.naho.speech.model.SpeechAssessment;
import org.naho.speech.model.WordAssessment;
import org.naho.speech.question.entity.AnswerHistoryEntity;
import org.naho.speech.question.entity.QuestionEntity;
import org.naho.speech.question.repository.AnswerHistoryJpaRepository;
import org.naho.speech.question.repository.QuestionJpaRepository;
import org.naho.user.entity.UserEntity;
import org.naho.user.repository.UserJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class AnswerHistoryRepositoryAdapter implements AnswerHistoryRepositoryPort {

    private final AnswerHistoryJpaRepository answerHistoryJpaRepository;
    private final SpeechAssessmentJpaRepository speechAssessmentJpaRepository;
    private final ContentAssessmentJpaRepository contentAssessmentJpaRepository;
    private final WordAssessmentJpaRepository wordAssessmentJpaRepository;
    
    private final UserJpaRepository userJpaRepository;
    private final QuestionJpaRepository questionJpaRepository;
    private final FileJpaRepository fileJpaRepository;

    @Override
    public AnswerHistory saveAnswerHistory(AnswerHistory domain) {
        UserEntity user = userJpaRepository.getReferenceById(domain.getUserId());
        QuestionEntity question = questionJpaRepository.getReferenceById(domain.getQuestionId());
        FileEntity file = fileJpaRepository.getReferenceById(domain.getAudioFileId());

        AnswerHistoryEntity entity = AnswerHistoryEntity.builder()
                .id(domain.getId())
                .user(user)
                .question(question)
                .audioFile(file)
                .build();

        AnswerHistoryEntity saved = answerHistoryJpaRepository.save(entity);
        return AnswerHistory.builder()
                .id(saved.getId())
                .userId(saved.getUser().getId())
                .questionId(saved.getQuestion().getId())
                .audioFileId(saved.getAudioFile().getId())
                .build();
    }

    @Override
    public SpeechAssessment saveSpeechAssessment(SpeechAssessment domain) {
        AnswerHistoryEntity answerHistory = answerHistoryJpaRepository.getReferenceById(domain.getAnswerHistoryId());

        SpeechAssessmentEntity entity = SpeechAssessmentEntity.builder()
                .id(domain.getId())
                .transcriptText(domain.getTranscriptText())
                .accuracyScore(domain.getAccuracyScore())
                .fluencyScore(domain.getFluencyScore())
                .completenessScore(domain.getCompletenessScore())
                .pronunciationScore(domain.getPronunciationScore())
                .answerHistory(answerHistory)
                .build();

        SpeechAssessmentEntity saved = speechAssessmentJpaRepository.save(entity);
        return SpeechAssessment.builder()
                .id(saved.getId())
                .transcriptText(saved.getTranscriptText())
                .accuracyScore(saved.getAccuracyScore())
                .fluencyScore(saved.getFluencyScore())
                .completenessScore(saved.getCompletenessScore())
                .pronunciationScore(saved.getPronunciationScore())
                .answerHistoryId(saved.getAnswerHistory().getId())
                .build();
    }

    @Override
    public ContentAssessment saveContentAssessment(ContentAssessment domain) {
        AnswerHistoryEntity answerHistory = answerHistoryJpaRepository.getReferenceById(domain.getAnswerHistoryId());

        ContentAssessmentEntity entity = ContentAssessmentEntity.builder()
                .id(domain.getId())
                .vocabularyScore(domain.getVocabularyScore())
                .grammarScore(domain.getGrammarScore())
                .aiFeedback(domain.getAiFeedback())
                .translationText(domain.getTranslationText())
                .answerHistory(answerHistory)
                .build();

        ContentAssessmentEntity saved = contentAssessmentJpaRepository.save(entity);
        return ContentAssessment.builder()
                .id(saved.getId())
                .vocabularyScore(saved.getVocabularyScore())
                .grammarScore(saved.getGrammarScore())
                .aiFeedback(saved.getAiFeedback())
                .translationText(saved.getTranslationText())
                .answerHistoryId(saved.getAnswerHistory().getId())
                .build();
    }

    @Override
    public List<WordAssessment> saveAllWordAssessments(List<WordAssessment> domains) {
        List<WordAssessmentEntity> entities = domains.stream().map(domain -> {
            SpeechAssessmentEntity sa = speechAssessmentJpaRepository.getReferenceById(domain.getSpeechAssessmentId());
            return WordAssessmentEntity.builder()
                    .word(domain.getWord())
                    .accuracyScore(domain.getAccuracyScore())
                    .errorType(domain.getErrorType())
                    .speechAssessment(sa)
                    .build();
        }).toList();

        List<WordAssessmentEntity> saved = wordAssessmentJpaRepository.saveAll(entities);
        return saved.stream().map(e -> WordAssessment.builder()
                .id(e.getId())
                .word(e.getWord())
                .accuracyScore(e.getAccuracyScore())
                .errorType(e.getErrorType())
                .speechAssessmentId(e.getSpeechAssessment().getId())
                .build()).toList();
    }

    @Override
    public Optional<AnswerHistory> findAnswerHistoryById(Long id) {
        return answerHistoryJpaRepository.findById(id).map(entity -> AnswerHistory.builder()
                .id(entity.getId())
                .userId(entity.getUser().getId())
                .questionId(entity.getQuestion().getId())
                .audioFileId(entity.getAudioFile().getId())
                .build());
    }

    @Override
    public Optional<SpeechAssessment> findSpeechAssessmentByAnswerHistoryId(Long answerHistoryId) {
        return speechAssessmentJpaRepository.findByAnswerHistoryId(answerHistoryId).map(entity -> {
            List<WordAssessment> words = entity.getWords() != null ? entity.getWords().stream().map(w -> WordAssessment.builder()
                    .id(w.getId())
                    .word(w.getWord())
                    .accuracyScore(w.getAccuracyScore())
                    .errorType(w.getErrorType())
                    .speechAssessmentId(w.getSpeechAssessment().getId())
                    .build()).toList() : List.of();

            return SpeechAssessment.builder()
                    .id(entity.getId())
                    .transcriptText(entity.getTranscriptText())
                    .accuracyScore(entity.getAccuracyScore())
                    .fluencyScore(entity.getFluencyScore())
                    .completenessScore(entity.getCompletenessScore())
                    .pronunciationScore(entity.getPronunciationScore())
                    .answerHistoryId(entity.getAnswerHistory().getId())
                    .words(words)
                    .build();
        });
    }

    @Override
    public Optional<ContentAssessment> findContentAssessmentByAnswerHistoryId(Long answerHistoryId) {
        return contentAssessmentJpaRepository.findByAnswerHistoryId(answerHistoryId).map(entity -> ContentAssessment.builder()
                .id(entity.getId())
                .vocabularyScore(entity.getVocabularyScore())
                .grammarScore(entity.getGrammarScore())
                .aiFeedback(entity.getAiFeedback())
                .translationText(entity.getTranslationText())
                .answerHistoryId(entity.getAnswerHistory().getId())
                .build());
    }
}
```

#### File 18: `OpenAiAnalysisAdapter.java`
Đường dẫn: `core/infrastructure/src/main/java/org/naho/speech/analysis/adapter/OpenAiAnalysisAdapter.java`
*Chú ý: Đảm bảo prompt yêu cầu trả JSON cấu trúc nghiêm ngặt và giải nén chuỗi thô.*
```java
package org.naho.speech.analysis.adapter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.naho.shared.exception.InfrastructureException;
import org.naho.speech.llm.constant.OpenAiConfigProperties;
import org.naho.speech.llm.exception.LlmApplicationError;
import org.naho.speech.analysis.port.out.AiAnalysisPort;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class OpenAiAnalysisAdapter implements AiAnalysisPort {

    private static final String LLM_URL = "https://api.openai.com/v1/chat/completions";
    private final OpenAiConfigProperties properties;
    private final HttpClient httpClient;

    public OpenAiAnalysisAdapter(OpenAiConfigProperties properties) {
        this.properties = properties;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(30))
                .build();
    }

    @Override
    public String analyzeSpeaking(String topic, String question, String studentTranscript, String azureWordFeedbackJson) {
        String systemPrompt = """
            You are an expert Japanese language assessor.
            Evaluate the student's spoken Japanese answer based on the context.
            Focus on grammatical accuracy, vocabulary usage, and naturalness.
            
            Based on the student's transcript and the word-level pronunciation scores/errors from Azure Speech, perform a detailed evaluation.
            
            Return the results ONLY as a valid JSON object matching the following schema.
            Do NOT include any markdown formatting (like ```json or ```), no leading/trailing comments, and no extra text. It must be a raw parseable JSON string.
            
            JSON Schema:
            {
              "scores": {
                "vocabulary": <double 0.0-10.0>,
                "grammar": <double 0.0-10.0>,
                "naturalness": <double 0.0-10.0>
              },
              "userTranscript": [
                {
                  "text": "<segment of user's answer>",
                  "error": null
                },
                {
                  "text": "<segment containing error>",
                  "error": {
                    "type": "Ngữ pháp / Sự tự nhiên / Từ vựng",
                    "explanation": "<Vietnamese explanation of the error>",
                    "suggestion": "<corrected Japanese version>"
                  }
                }
              ],
              "aiSuggestion": {
                "jp": "<natural Japanese recommended response>",
                "furigana": "<the recommended response with furigana/hiragana for all kanji>",
                "vi": "<Vietnamese translation of the recommended response>"
              },
              "pronunciationNote": "<Overall pronunciation advice in Vietnamese based on the azure word feedback. Focus on what areas the student needs to improve, e.g., long vowels, double consonants, or typical errors.>",
              "wordNotes": {
                "<japanese_word>": "<Vietnamese feedback note for this specific word, e.g., 'Phát âm tốt', 'Chú ý kéo dài hơi', etc. Keep it very short and helpful.>"
              },
              "expressions": [
                {
                  "jp": "<useful Japanese phrase related to this topic>",
                  "furigana": "<furigana for the phrase>",
                  "vi": "<Vietnamese translation>",
                  "note": "<Vietnamese note on how/when to use it>"
                }
              ],
              "itVocab": [
                {
                  "term": "<IT Japanese vocabulary, e.g., 進捗>",
                  "reading": "<reading in hiragana>",
                  "meaning": "<Vietnamese meaning>"
                }
              ]
            }
            
            NOTE for itVocab: ONLY populate itVocab with 1-3 useful IT Japanese terms if the topic is IT/tech related. Otherwise, leave it as an empty list [].
            """;

        String userContent = String.format(
                "Topic: %s\nQuestion: %s\nStudent Transcript: %s\nAzure Pronunciation Data: %s",
                topic, question, studentTranscript, azureWordFeedbackJson
        );

        String requestBody = buildRequestBody(systemPrompt, userContent);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(LLM_URL))
                .timeout(Duration.ofSeconds(60))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + properties.getApiKey())
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                throw new InfrastructureException(
                        LlmApplicationError.LLM_API_ERROR,
                        "OpenAI Analysis API error. Status: " + response.statusCode() + " | " + response.body());
            }
            return extractContent(response.body());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new InfrastructureException(
                    LlmApplicationError.LLM_CONNECTION_TIMEOUT,
                    "Analysis request interrupted", e
            );
        } catch (Exception e) {
            throw new InfrastructureException(
                    LlmApplicationError.LLM_API_ERROR,
                    "Cannot call OpenAI Analysis API: " + e.getMessage(), e
            );
        }
    }

    private String buildRequestBody(String systemPrompt, String userContent) {
        String model = properties.getScoringModel();
        if (model == null || model.isBlank()) {
            model = "gpt-4o";
        }
        return String.format(
                "{\"model\":\"%s\",\"messages\":[{\"role\":\"system\",\"content\":%s},{\"role\":\"user\",\"content\":%s}],\"temperature\":0.2}",
                model,
                escapeJson(systemPrompt),
                escapeJson(userContent)
        );
    }

    private String extractContent(String responseJson) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(responseJson);
            String rawContent = root.path("choices").path(0).path("message").path("content").asText("");
            String trimmed = rawContent.trim();
            if (trimmed.startsWith("```")) {
                int firstLineBreak = trimmed.indexOf('\n');
                int lastFence = trimmed.lastIndexOf("```");
                if (firstLineBreak != -1 && lastFence != -1 && lastFence > firstLineBreak) {
                    trimmed = trimmed.substring(firstLineBreak + 1, lastFence).trim();
                }
            }
            return trimmed;
        } catch (Exception e) {
            throw new InfrastructureException(
                    LlmApplicationError.LLM_PARSE_ERROR,
                    "Cannot parse OpenAI response content: " + responseJson, e
            );
        }
    }

    private String escapeJson(String input) {
        if (input == null) return "\"\"";
        return new ObjectMapper().valueToTree(input).toString();
    }
}
```

#### File 19: `QuestionRepositoryAdapter.java` (Sửa đổi)
Đường dẫn: `core/infrastructure/src/main/java/org/naho/speech/question/adapter/QuestionRepositoryAdapter.java`
*Nhiệm vụ: Cung cấp cài đặt ánh xạ thực thể `QuestionEntity` sang Domain `Question` cho cổng ra.*
```java
// Thêm import các Model domain:
import org.naho.topic.model.Question;
import org.naho.topic.model.Grammar;
import org.naho.topic.model.Vocabulary;
import java.util.Optional;

// Thêm override này vào class QuestionRepositoryAdapter:
@Override
public Optional<Question> findById(Long id) {
    return questionJpaRepository.findById(id).map(entity -> Question.builder()
            .id(entity.getId())
            .questionAudioFileId(entity.getQuestionAudioFile() != null ? entity.getQuestionAudioFile().getId() : null)
            .topicId(entity.getTopic() != null ? entity.getTopic().getId() : null)
            .userId(entity.getUser() != null ? entity.getUser().getId() : null)
            .title(entity.getTitle())
            .titleMarkup(entity.getTitleMarkup())
            .description(entity.getDescription())
            .descriptionMarkup(entity.getDescriptionMarkup())
            .orderIndex(entity.getOrderIndex())
            .status(entity.getStatus())
            .grammars(entity.getGrammars() != null ? entity.getGrammars().stream().map(g -> Grammar.builder()
                    .id(g.getId())
                    .vietnameseMeaningText(g.getVietnameseMeaningText())
                    .englishMeaningText(g.getEnglishMeaningText())
                    .explanation(g.getExplanation())
                    .build()).toList() : List.of())
            .vocabularies(entity.getVocabularies() != null ? entity.getVocabularies().stream().map(v -> Vocabulary.builder()
                    .id(v.getId())
                    .vietnameseMeaningText(v.getVietnameseMeaningText())
                    .englishMeaningText(v.getEnglishMeaningText())
                    .build()).toList() : List.of())
            .build());
}
```

---

### 2.3. Tầng Presentation (Giao tiếp API và Chuyển đổi DTO)

#### File 20: `SpeakingAnalysisResponse.java`
Đường dẫn: `core/presentation/src/main/java/org/naho/speech/analysis/dto/response/SpeakingAnalysisResponse.java`
```java
package org.naho.speech.analysis.dto.response;

public record SpeakingAnalysisResponse(
        String historyId,
        Double score
) {}
```

#### File 21: `SpeakingHistoryDetailResponse.java`
Đường dẫn: `core/presentation/src/main/java/org/naho/speech/analysis/dto/response/SpeakingHistoryDetailResponse.java`
```java
package org.naho.speech.analysis.dto.response;

import java.util.List;

public record SpeakingHistoryDetailResponse(
        String historyId,
        String topicId,
        String questionId,
        String practicedAt,
        Integer durationSec,
        Double score,
        String audioUrl,
        Report report
) {
    public record Report(
            Double average,
            Scores scores,
            List<UserTranscriptItem> userTranscript,
            AiSuggestion aiSuggestion,
            List<PronunciationItem> pronunciation,
            String pronunciationNote,
            List<ExpressionItem> expressions,
            List<ItVocabItem> itVocab
    ) {}

    public record Scores(
            Double pronunciation,
            Double vocabulary,
            Double grammar,
            Double naturalness
    ) {}

    public record UserTranscriptItem(
            String text,
            ErrorDetail error
    ) {}

    public record ErrorDetail(
            String type,
            String explanation,
            String suggestion
    ) {}

    public record AiSuggestion(
            String jp,
            String furigana,
            String vi
    ) {}

    public record PronunciationItem(
            String text,
            String furigana,
            String severity,
            String note
    ) {}

    public record ExpressionItem(
            String jp,
            String furigana,
            String vi,
            String note
    ) {}

    public record ItVocabItem(
            String term,
            String reading,
            String meaning
    ) {}
}
```

#### File 22: `SpeakingAnalysisMapper.java`
Đường dẫn: `core/presentation/src/main/java/org/naho/speech/analysis/dto/mapper/SpeakingAnalysisMapper.java`
```java
package org.naho.speech.analysis.dto.mapper;

import org.naho.speech.analysis.dto.response.SpeakingAnalysisResponse;
import org.naho.speech.analysis.dto.response.SpeakingHistoryDetailResponse;
import org.naho.speech.analysis.result.SpeakingAnalysisResult;
import org.naho.speech.analysis.result.SpeakingHistoryDetailResult;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;

@Component
public class SpeakingAnalysisMapper {

    private static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ISO_INSTANT;

    public SpeakingAnalysisResponse toResponse(SpeakingAnalysisResult result) {
        if (result == null) return null;
        return new SpeakingAnalysisResponse("h-" + result.historyId(), result.score());
    }

    public SpeakingHistoryDetailResponse toDetailResponse(SpeakingHistoryDetailResult result) {
        if (result == null) return null;

        var report = result.report();
        var scores = report.scores();

        var scoresResponse = new SpeakingHistoryDetailResponse.Scores(
                scores.pronunciation(),
                scores.vocabulary(),
                scores.grammar(),
                scores.naturalness()
        );

        var userTranscriptResponse = report.userTranscript().stream()
                .map(item -> new SpeakingHistoryDetailResponse.UserTranscriptItem(
                        item.text(),
                        item.error() != null ? new SpeakingHistoryDetailResponse.ErrorDetail(
                                item.error().type(),
                                item.error().explanation(),
                                item.error().suggestion()
                        ) : null
                )).toList();

        var aiSuggestionResponse = new SpeakingHistoryDetailResponse.AiSuggestion(
                report.aiSuggestion().jp(),
                report.aiSuggestion().furigana(),
                report.aiSuggestion().vi()
        );

        var pronunciationResponse = report.pronunciation().stream()
                .map(item -> new SpeakingHistoryDetailResponse.PronunciationItem(
                        item.text(),
                        item.furigana(),
                        item.severity(),
                        item.note()
                )).toList();

        var expressionsResponse = report.expressions().stream()
                .map(item -> new SpeakingHistoryDetailResponse.ExpressionItem(
                        item.jp(),
                        item.furigana(),
                        item.vi(),
                        item.note()
                )).toList();

        var itVocabResponse = report.itVocab().stream()
                .map(item -> new SpeakingHistoryDetailResponse.ItVocabItem(
                        item.term(),
                        item.reading(),
                        item.meaning()
                )).toList();

        var reportResponse = new SpeakingHistoryDetailResponse.Report(
                report.average(),
                scoresResponse,
                userTranscriptResponse,
                aiSuggestionResponse,
                pronunciationResponse,
                report.pronunciationNote(),
                expressionsResponse,
                itVocabResponse
        );

        return new SpeakingHistoryDetailResponse(
                "h-" + result.historyId(),
                result.topicId() != null ? "t-" + result.topicId() : null,
                result.questionId() != null ? "q-" + result.questionId() : null,
                result.practicedAt() != null ? ISO_FORMATTER.format(result.practicedAt()) : null,
                result.durationSec(),
                result.score(),
                result.audioUrl(),
                reportResponse
        );
    }
}
```

#### File 23: `SpeakingAnalysisController.java`
Đường dẫn: `core/presentation/src/main/java/org/naho/speech/analysis/controller/v1/SpeakingAnalysisController.java`
```java
package org.naho.speech.analysis.controller.v1;

import lombok.RequiredArgsConstructor;
import org.naho.shared.annotation.ApiResponseMessage;
import org.naho.speech.analysis.command.SpeakingAnalysisCommand;
import org.naho.speech.analysis.dto.mapper.SpeakingAnalysisMapper;
import org.naho.speech.analysis.dto.response.SpeakingAnalysisResponse;
import org.naho.speech.analysis.dto.response.SpeakingHistoryDetailResponse;
import org.naho.speech.analysis.port.in.SpeakingAnalysisInputPort;
import org.naho.user.result.AccessTokenPayload;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class SpeakingAnalysisController {

    private final SpeakingAnalysisInputPort speakingAnalysisInputPort;
    private final SpeakingAnalysisMapper speakingAnalysisMapper;

    @PostMapping(
            value = "/analysis",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ApiResponseMessage(message = "Phân tích phát âm thành công!")
    public ResponseEntity<SpeakingAnalysisResponse> analyze(
            @RequestPart("file") MultipartFile file,
            @RequestParam("topicId") Long topicId,
            @RequestParam("questionId") Long questionId,
            @RequestParam("durationSec") Integer durationSec,
            @AuthenticationPrincipal AccessTokenPayload payload
    ) throws IOException {
        SpeakingAnalysisCommand command = new SpeakingAnalysisCommand(
                payload.userId(),
                topicId,
                questionId,
                file.getBytes(),
                file.getContentType(),
                file.getOriginalFilename(),
                durationSec
        );

        var result = speakingAnalysisInputPort.analyze(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(speakingAnalysisMapper.toResponse(result));
    }

    @GetMapping(value = "/history/{historyId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponseMessage(message = "Lấy chi tiết lịch sử thành công!")
    public ResponseEntity<SpeakingHistoryDetailResponse> getHistoryDetail(
            @PathVariable("historyId") String historyIdStr
    ) {
        Long historyId;
        if (historyIdStr.startsWith("h-")) {
            historyId = Long.parseLong(historyIdStr.substring(2));
        } else {
            historyId = Long.parseLong(historyIdStr);
        }

        var result = speakingAnalysisInputPort.getHistoryDetail(historyId);
        return ResponseEntity.ok(speakingAnalysisMapper.toDetailResponse(result));
    }
}
```

---

## 3. Liên Kết Đăng Ký Beans (Configuration Setup)

Sau khi tạo xong tất cả các class trên, bạn hãy mở file **[ChatConfig.java](file:///f:/FULearning/SEP_490_SU2026/merge/NaHo-Backend-Ver4/core/bootstrap/src/main/java/org/naho/config/application/ChatConfig.java)** để liên kết và đăng ký các Component vào Spring Context.

Thêm các Bean sau vào cuối class `ChatConfig`:

```java
    @Bean
    public AiAnalysisPort aiAnalysisPort(OpenAiConfigProperties openAiConfigProperties) {
        return new OpenAiAnalysisAdapter(openAiConfigProperties);
    }

    @Bean
    public SpeakingAnalysisInputPort speakingAnalysisInputPort(
            UserRepositoryPort userRepositoryPort,
            QuestionRepositoryPort questionRepositoryPort,
            TopicRepositoryPort topicRepositoryPort,
            FileStorageInputPort fileStorageInputPort,
            FileRepositoryPort fileRepositoryPort,
            AnswerHistoryRepositoryPort answerHistoryRepositoryPort,
            AzureSpeechServicePort azureSpeechServicePort,
            AiAnalysisPort aiAnalysisPort,
            FuriganaAnalysisPort furiganaAnalysisPort,
            S3Properties s3Properties
    ) {
        return new SpeakingAnalysisUseCase(
                userRepositoryPort,
                questionRepositoryPort,
                topicRepositoryPort,
                fileStorageInputPort,
                fileRepositoryPort,
                answerHistoryRepositoryPort,
                azureSpeechServicePort,
                aiAnalysisPort,
                furiganaAnalysisPort,
                s3Properties
        );
    }
```
*Lưu ý: Bạn cũng cần đảm bảo các adapter khác (như `QuestionRepositoryAdapter`, `AnswerHistoryRepositoryAdapter`, `OpenAiAnalysisAdapter`) đã được đánh dấu Spring Annotation như `@Component` để cơ chế tự động tìm kiếm Bean (Autowiring) hoạt động trơn tru.*

---

## 4. Biên Dịch & Xác Nhận Hệ Thống

Để đảm bảo các phần code bạn tự gõ không mắc lỗi cú pháp và chạy hoàn hảo:

1. Chạy lệnh Gradle để biên dịch ứng dụng từ thư mục gốc của dự án:
   ```bash
   .\gradlew.bat compileJava
   ```
2. Chạy ứng dụng Spring Boot local bằng lệnh:
   ```bash
   .\gradlew.bat :core:bootstrap:bootRun
   ```
3. Sau khi dự án khởi động thành công, bạn có thể kiểm thử API bằng Postman với endpoint `POST /api/v1/analysis` (gửi form-data file ghi âm + tham số) và `GET /api/v1/history/h-{id}` để xem kết quả đánh giá thông minh từ AI.
