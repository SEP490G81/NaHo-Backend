package org.naho.furigana.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class FuriganaResponse {
    private String originalText;
    private List<FuriganaTokenResponse> tokens;
    private String fullFurigana;

    @Data
    public static class FuriganaTokenResponse {
        private String kanji;
        private String furigana;
    }
}
