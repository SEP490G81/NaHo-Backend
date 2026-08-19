package org.naho.speech.llm.conversation.result;

import java.util.List;

public record SuggestedTopicsResult(List<TopicItem> topics) {
    public record TopicItem(
            String nameJa,
            String nameVie,
            String description,
            String jlptLevel
    ) {
    }
}
