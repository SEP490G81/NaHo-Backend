package org.naho.book.mapper;

import org.naho.book.model.Topic;
import org.naho.book.result.TopicResult;

public class TopicResultMapper {
    public TopicResult domainToResult(Topic domain) {
        return TopicResult.builder()
                .id(domain.getId())
                .userId(domain.getUserId())
                .bookId(domain.getBookId())
                .coverImageFileId(domain.getCoverImageFileId())
                .japaneseName(domain.getJapaneseName())
                .japaneseDescription(domain.getJapaneseDescription())
                .japaneseNameMarkup(domain.getJapaneseNameMarkup())
                .japaneseDescriptionMarkup(domain.getJapaneseDescriptionMarkup())
                .status(domain.getStatus())
                .orderIndex(domain.getOrderIndex())
                .build();
    }
}
