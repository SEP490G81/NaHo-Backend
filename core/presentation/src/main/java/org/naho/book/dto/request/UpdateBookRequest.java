package org.naho.book.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.naho.book.type.CefrLevel;
import org.naho.user.type.JLPTLevel;

public record UpdateBookRequest(
        Long coverImageFileId,
        @NotBlank(message = "Title cannot be blank") String title,
        String description,
        @NotNull(message = "JLPT level cannot be null") JLPTLevel jlptLevel,
        @NotNull(message = "CEFR level cannot be null") CefrLevel cefrLevel
) {
}
