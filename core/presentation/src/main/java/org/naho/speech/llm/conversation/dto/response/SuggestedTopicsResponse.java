package org.naho.speech.llm.conversation.dto.response;

import java.util.List;

/**
 * HTTP Response DTO: Danh sách chủ đề gợi ý cho speaking practice.
 */
public record SuggestedTopicsResponse(List<TopicItem> topics) {
    public record TopicItem(
            String nameJa,
            String nameVie,
            String description,
            String jlptLevel
    ) {
    }
}
