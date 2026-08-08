package org.naho.speech.llm.result;

public record WordPronunciationResult(
        String word,
        Double accuracyScore,
        String errorType,
        String colorCategory, // GREEN, YELLOW, RED
        String hexColor       // #22C55E, #EAB308, #EF4444
) {
    public static WordPronunciationResult from(String word, Double accuracyScore, String errorType) {
        double score = accuracyScore != null ? accuracyScore : 0.0;
        String colorCategory;
        String hexColor;

        if (score >= 80.0 && !"Mispronunciation".equalsIgnoreCase(errorType)) {
            colorCategory = "GREEN";
            hexColor = "#22C55E";
        } else if (score >= 60.0 && !"Mispronunciation".equalsIgnoreCase(errorType)) {
            colorCategory = "YELLOW";
            hexColor = "#EAB308";
        } else {
            colorCategory = "RED";
            hexColor = "#EF4444";
        }
        return new WordPronunciationResult(word, score, errorType, colorCategory, hexColor);
    }

    public static WordPronunciationResult wordPronunciationResult(String word, Double accuracyScore, String errorType) {
        return from(word, accuracyScore, errorType);
    }
}

