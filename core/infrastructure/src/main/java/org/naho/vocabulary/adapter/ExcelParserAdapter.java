package org.naho.vocabulary.adapter;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.naho.question.model.Grammar;
import org.naho.shared.exception.ApplicationException;
import org.naho.vocabulary.exception.VocabularyErrorCode;
import org.naho.vocabulary.model.Vocabulary;
import org.naho.vocabulary.model.VocabularyGrammarImportResult;
import org.naho.vocabulary.port.out.ExcelParserPort;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Component
public class ExcelParserAdapter implements ExcelParserPort {

    @Override
    public VocabularyGrammarImportResult parseExcel(InputStream inputStream) {
        List<Vocabulary> vocabularies = new ArrayList<>();
        List<Grammar> grammars = new ArrayList<>();

        try (Workbook workbook = new XSSFWorkbook(inputStream)) {
            // Parse Vocabulary Sheet
            Sheet vocabSheet = workbook.getSheet("Vocabulary Import");
            if (vocabSheet == null) {
                vocabSheet = workbook.getSheetAt(0);
            }
            if (vocabSheet != null) {
                boolean isFirstRow = true;
                for (Row row : vocabSheet) {
                    if (isFirstRow) {
                        isFirstRow = false;
                        continue; // Skip header
                    }

                    String reading = getCellValueAsString(row.getCell(0));
                    String japanese = getCellValueAsString(row.getCell(1));
                    String vietMeaning = getCellValueAsString(row.getCell(2));
                    String engMeaning = getCellValueAsString(row.getCell(3));

                    if (reading.isEmpty() && japanese.isEmpty() && vietMeaning.isEmpty()) {
                        continue; // skip empty rows
                    }

                    Vocabulary vocabulary = Vocabulary.builder()
                            .reading(reading)
                            .japanese(japanese)
                            .vietnameseMeaningText(vietMeaning)
                            .englishMeaningText(engMeaning)
                            .build();

                    vocabularies.add(vocabulary);
                }
            }

            // Parse Grammar Sheet
            Sheet grammarSheet = workbook.getSheet("Grammar Import");
            if (grammarSheet == null && workbook.getNumberOfSheets() > 1) {
                grammarSheet = workbook.getSheetAt(1);
            }
            if (grammarSheet != null) {
                boolean isFirstRow = true;
                for (Row row : grammarSheet) {
                    if (isFirstRow) {
                        isFirstRow = false;
                        continue; // Skip header
                    }

                    String reading = getCellValueAsString(row.getCell(0));
                    String japanese = getCellValueAsString(row.getCell(1));
                    String vietMeaning = getCellValueAsString(row.getCell(2));
                    String engMeaning = getCellValueAsString(row.getCell(3));

                    if (reading.isEmpty() && japanese.isEmpty() && vietMeaning.isEmpty()) {
                        continue; // skip empty rows
                    }

                    Grammar grammar = Grammar.builder()
                            .reading(reading)
                            .japanese(japanese)
                            .vietnameseMeaningText(vietMeaning)
                            .englishMeaningText(engMeaning)
                            .build();

                    grammars.add(grammar);
                }
            }
        } catch (Exception e) {
            throw new ApplicationException(VocabularyErrorCode.VOCABULARY_IMPORT_INVALID_FILE, "vocabulary.import.invalid_file", e);
        }

        return new VocabularyGrammarImportResult(vocabularies, grammars);
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
