package org.naho.book.result;

import org.naho.book.type.CefrLevel;
import org.naho.book.type.TopicStatus;
import org.naho.user.type.JLPTLevel;

public record BookListItemResult(
        Long id,
        String title,
        String description,
        JLPTLevel jlptLevel,
        CefrLevel cefrLevel,
        Double orderIndex,
        Long coverImageFileId
) {
}
