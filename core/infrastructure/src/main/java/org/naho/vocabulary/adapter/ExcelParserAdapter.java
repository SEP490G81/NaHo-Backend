package org.naho.vocabulary.adapter;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.naho.shared.exception.ApplicationException;
import org.naho.vocabulary.exception.VocabularyErrorCode;
import org.naho.vocabulary.model.Vocabulary;
import org.naho.vocabulary.port.out.ExcelParserPort;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Component
public class ExcelParserAdapter implements ExcelParserPort {

    /**
     * Expected Excel column order:
     * Col 0: Reading
     * Col 1: Japanese
     * Col 2: Vietnamese Meaning
     * Col 3: English Meaning
     * Col 4: objective_id  (Long - from objectives_reference.xlsx)
     * Col 5: question_id   (Long - from questions_reference.xlsx)
     */
    @Override
    public List<Vocabulary> parseExcel(InputStream inputStream) {
        List<Vocabulary> vocabularies = new ArrayList<>();
        try (Workbook workbook = new XSSFWorkbook(inputStream)) {
            Sheet sheet = workbook.getSheetAt(0);
            boolean isFirstRow = true;
            for (Row row : sheet) {
                if (isFirstRow) {
                    isFirstRow = false;
                    continue; // Skip header
                }

                String reading = getCellValueAsString(row.getCell(0));
                String japanese = getCellValueAsString(row.getCell(1));
                String vietMeaning = getCellValueAsString(row.getCell(2));
                String engMeaning = getCellValueAsString(row.getCell(3));
                // col 4 = objective_id (reference only, not stored on Vocabulary)
                Long questionId = getCellValueAsLong(row.getCell(5));

                if (reading.isEmpty() && japanese.isEmpty() && vietMeaning.isEmpty()) {
                    continue; // skip empty rows
                }

                Vocabulary vocabulary = Vocabulary.builder()
                        .reading(reading)
                        .japanese(japanese)
                        .vietnameseMeaningText(vietMeaning)
                        .englishMeaningText(engMeaning)
                        .questionId(questionId)
                        .build();

                vocabularies.add(vocabulary);
            }
        } catch (Exception e) {
            throw new ApplicationException(VocabularyErrorCode.VOCABULARY_IMPORT_INVALID_FILE, "vocabulary.import.invalid_file", e);
        }
        return vocabularies;
    }

    private String getCellValueAsString(Cell cell) {
        if (cell == null) {
            return "";
        }
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue().trim();
            case NUMERIC -> {
                double val = cell.getNumericCellValue();
                yield val == Math.floor(val) ? String.valueOf((long) val) : String.valueOf(val);
            }
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            case FORMULA -> cell.getCellFormula();
            default -> "";
        };
    }

    private Long getCellValueAsLong(Cell cell) {
        if (cell == null) return null;
        return switch (cell.getCellType()) {
            case NUMERIC -> (long) cell.getNumericCellValue();
            case STRING -> {
                try {
                    yield Long.parseLong(cell.getStringCellValue().trim());
                } catch (NumberFormatException e) {
                    yield null;
                }
            }
            default -> null;
        };
    }
}
