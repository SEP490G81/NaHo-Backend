package org.naho.furigana.adapter;

import com.worksap.nlp.sudachi.Dictionary;
import com.worksap.nlp.sudachi.DictionaryFactory;
import com.worksap.nlp.sudachi.Morpheme;
import com.worksap.nlp.sudachi.Tokenizer;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.naho.furigana.model.FuriganaText;
import org.naho.furigana.model.FuriganaToken;
import org.naho.furigana.port.out.FuriganaGenerationPort;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@Component
public class SudachiAnalysisAdapter implements FuriganaGenerationPort {

    private Dictionary dictionary;

    @PostConstruct
    public void init() throws IOException {
        // Extract dictionary to a temp file to support Windows (bypass URL.getPath() bug)
        // and JAR execution (MMap requires a real file system path)
        ClassPathResource dicResource = new ClassPathResource("sudachi/system_core.dic");
        if (!dicResource.exists()) {
            throw new RuntimeException("Dictionary file not found at sudachi/system_core.dic");
        }

        Path tempDicPath = Files.createTempFile("system_core", ".dic");
        tempDicPath.toFile().deleteOnExit();

        try (java.io.InputStream is = dicResource.getInputStream()) {
            Files.copy(is, tempDicPath, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
        }

        String absoluteDicPath = tempDicPath.toAbsolutePath().toString().replace("\\", "\\\\");
        String settingsContent = "{\"systemDict\":\"" + absoluteDicPath + "\"}";

        try {
            this.dictionary = new DictionaryFactory().create("", settingsContent, true);
        } catch (IOException e) {
            throw new RuntimeException("Failed to initialize Sudachi dictionary", e);
        }
    }

    @PreDestroy
    public void destroy() {
        if (dictionary != null) {
            try {
                dictionary.close();
            } catch (Exception e) {
                // Ignore
            }
        }
    }

    @Override
    public FuriganaText generate(String text) {
        if (text == null || text.trim().isEmpty()) {
            return FuriganaText.builder()
                    .originalText(text)
                    .tokens(new ArrayList<>())
                    .markupString("")
                    .build();
        }

        List<FuriganaToken> tokens = new ArrayList<>();
        StringBuilder markupString = new StringBuilder();

        Tokenizer tokenizer = dictionary.create(); // sửa lỗi thread-safe

        // Use SplitMode.C to get longest possible words, or A for shortest. C is usually best for meaning.
        for (Morpheme m : tokenizer.tokenize(Tokenizer.SplitMode.C, text)) {
            String surface = m.surface();
            String readingKatakana = m.readingForm();
            String readingHiragana = convertKatakanaToHiragana(readingKatakana);

            // If the surface is same as reading (e.g. punctuation, already hiragana), don't set furigana
            String furigana = null;
            if (!surface.equals(readingHiragana) && !surface.equals(readingKatakana)) {
                // Check if it contains Kanji. Simple check: if surface has characters > \u4E00
                if (surface.chars().anyMatch(c -> c >= 0x4E00 && c <= 0x9FAF)) {
                    furigana = readingHiragana;
                }
            }

            tokens.add(FuriganaToken.builder()
                    .kanji(surface)
                    .furigana(furigana != null ? furigana : "")
                    .build());

            if (furigana != null && !furigana.isEmpty()) {
                markupString.append("[").append(surface).append("](").append(furigana).append(")");
            } else {
                markupString.append(surface);
            }
        }

        return FuriganaText.builder()
                .originalText(text)
                .tokens(tokens)
                .markupString(markupString.toString())
                .build();
    }

    private String convertKatakanaToHiragana(String katakana) {
        if (katakana == null) return null;
        StringBuilder sb = new StringBuilder(katakana.length());
        for (int i = 0; i < katakana.length(); i++) {
            char c = katakana.charAt(i);
            if (c >= '\u30A1' && c <= '\u30F6') {
                sb.append((char) (c - 0x0060));
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }
}
