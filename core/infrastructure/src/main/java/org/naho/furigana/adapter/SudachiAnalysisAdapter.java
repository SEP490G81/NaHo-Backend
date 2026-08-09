package org.naho.furigana.adapter;

import com.worksap.nlp.sudachi.Dictionary;
import com.worksap.nlp.sudachi.DictionaryFactory;
import com.worksap.nlp.sudachi.Morpheme;
import com.worksap.nlp.sudachi.Tokenizer;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.naho.furigana.port.out.FuriganaGenerationPort;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

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
    public String generateFuriganaMarkup(String text) {
        if (text == null || text.trim().isEmpty()) {
            return text;
        }

        StringBuilder markupString = new StringBuilder("<p>");

        Tokenizer tokenizer = dictionary.create(); // sửa lỗi thread-safe

        // SplitMode C ưu tiên cắt thành các từ dài nhất có thể, từ đó cho kết quả chính xác nhất về mặt nghĩa
        for (Morpheme m : tokenizer.tokenize(Tokenizer.SplitMode.C, text)) {
            String surface = m.surface();
            String readingKatakana = m.readingForm();
            String readingHiragana = convertKatakanaToHiragana(readingKatakana);

            // Nếu mà từ gốc (surface) giống với các đọc (readingForm) thì ko set furigana
            String furigana = null;
            if (!surface.equals(readingHiragana) && !surface.equals(readingKatakana)) {
                // Kiểm tra xem kí tự có phải là kanji hay ko, đơn giản là so sánh > \u4E00
                if (surface.chars().anyMatch(c -> c >= 0x4E00 && c <= 0x9FAF)) {
                    furigana = readingHiragana;
                }
            }

            if (furigana != null && !furigana.isEmpty()) {
                markupString.append("<ruby>").append(surface).append("<rt>").append(furigana).append("</rt></ruby>");
            } else {
                markupString.append(surface);
            }
        }

        markupString.append("</p>");
        return markupString.toString();
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
