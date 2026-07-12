package org.naho.book.dto.response;

import org.naho.book.type.CefrLevel;
import org.naho.user.type.JLPTLevel;

public record BookResponse(
        Long id,
        String title,
        String description,
        JLPTLevel jlptLevel,
        CefrLevel cefrLevel,
        Double orderIndex,
        Long coverImageFileId
) {
}
