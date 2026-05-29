package org.naho.speech.azure.helper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TextToSpeechServiceHelper {
    public String sanitizeInput(String input) {
        if (input == null) {
            return null;
        }
        String sanitized = input.trim();
        if ((sanitized.startsWith("\"") && sanitized.endsWith("\"")) ||
                (sanitized.startsWith("'") && sanitized.endsWith("'"))) {
            sanitized = sanitized.substring(1, sanitized.length() - 1).trim();
        }
        return sanitized;
    }
}
