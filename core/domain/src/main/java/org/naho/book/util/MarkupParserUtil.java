package org.naho.book.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MarkupParserUtil {

    // Matches [Kanji](furigana) or any [text](reading)
    private static final Pattern MARKUP_PATTERN = Pattern.compile("\\[(.*?)\\]\\((.*?)\\)");

    /**
     * Extracts raw text from markup string by removing reading blocks.
     * Example: "Chao [K](m)san" -> "Chao Ksan"
     */
    public static String extractRawTextFromMarkup(String markupStr) {
        if (markupStr == null) {
            return null;
        }
        Matcher matcher = MARKUP_PATTERN.matcher(markupStr);
        // Replace [text](reading) with text
        return matcher.replaceAll("$1");
    }
}
