package org.naho.question.port.out;

import org.naho.question.model.SpeakingQuestion;
import org.naho.question.type.QuestionStatus;

import java.util.List;
import java.util.Optional;

public interface SpeakingQuestionRepositoryPort {
    void deleteSpeakingQuestionsByTopicId(Long topicId);

    void updateSpeakingQuestionsStatusByTopicId(Long topicId, QuestionStatus status);

    boolean hasAnySpeakingQuestionBeenAnsweredInTopic(Long topicId);

    boolean hasSpeakingQuestionBeenAnswered(Long questionId);

    Optional<SpeakingQuestion> findById(Long id);

    SpeakingQuestion save(SpeakingQuestion speakingQuestion);

    void deleteById(Long id);

    List<SpeakingQuestion> findByObjectiveId(Long objectiveId);
}
