package org.naho.speech.llm.conversation.internal;

import org.naho.learning.model.LearningPathNode;
import org.naho.learning.model.UserLearningProgress;
import org.naho.question.model.SpeakingQuestion;
import org.naho.user.model.User;

public record AnalysisContext(
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
