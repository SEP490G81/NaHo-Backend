package org.naho.book.command;

import org.naho.book.type.CefrLevel;
import org.naho.user.type.JLPTLevel;

public record UpdateBookCommand(
        Long bookId,
        Long coverImageFileId,
        String title,
        String description,
        JLPTLevel jlptLevel,
        CefrLevel cefrLevel,
        Long adminUserId,
        boolean isAdminOrManager
) {
}
