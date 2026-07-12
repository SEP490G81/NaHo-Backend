package org.naho.book.dto.response;

import org.naho.book.type.CefrLevel;
import org.naho.file.dto.response.FileResponse;
import org.naho.user.type.JLPTLevel;

public record BookResponse(
        Long id,
        String title,
        String description,
        JLPTLevel jlptLevel,
        CefrLevel cefrLevel,
        Double orderIndex,
        FileResponse coverImage
) {
}
