package org.naho.book.result;

import org.naho.book.type.CefrLevel;
import org.naho.file.result.FileResult;
import org.naho.user.type.JLPTLevel;

public record BookResult(
        Long id,
        String title,
        String description,
        JLPTLevel jlptLevel,
        CefrLevel cefrLevel,
        Double orderIndex,
        FileResult coverImage
) {
}
