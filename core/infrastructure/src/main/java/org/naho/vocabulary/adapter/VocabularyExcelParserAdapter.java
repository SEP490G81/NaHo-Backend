package org.naho.vocabulary.adapter;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.naho.i18n.message.question.VocabularyQuestionDetailMessageKey;
import org.naho.question.model.Vocabulary;
import org.naho.shared.exception.ApplicationException;
import org.naho.shared.util.ExcelUtil;
import org.naho.vocabulary.exception.VocabularyErrorCode;
import org.naho.vocabulary.port.out.VocabularyExcelParserPort;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Component
public class VocabularyExcelParserAdapter implements VocabularyExcelParserPort {

    @Override
    public List<Vocabulary> parseVocabularyExcel(InputStream inputStream) {
        List<Vocabulary> vocabularies = new ArrayList<>();

        try (Workbook workbook = new XSSFWorkbook(inputStream)) {
            Sheet vocabSheet = workbook.getSheetAt(0);

            if (vocabSheet == null) {
                return vocabularies;
            }

            for (int rowNum = 1; rowNum <= vocabSheet.getLastRowNum(); rowNum++) {
                Row vocabRow = vocabSheet.getRow(rowNum);
                if (vocabRow == null) {
                    continue;
                }

                String japanese = ExcelUtil.getCellValueAsString(vocabRow.getCell(0));
                String reading = ExcelUtil.getCellValueAsString(vocabRow.getCell(1));
                String vietMeaning = ExcelUtil.getCellValueAsString(vocabRow.getCell(2));
                String engMeaning = ExcelUtil.getCellValueAsString(vocabRow.getCell(3));

                if (reading.isEmpty() && japanese.isEmpty() && vietMeaning.isEmpty()) {
                    continue; // skip empty rows
                }

                Vocabulary vocabulary = Vocabulary.builder()
                        .japanese(japanese)
                        .reading(reading)
                        .vietnameseMeaningText(vietMeaning)
                        .englishMeaningText(engMeaning)
                        .build();

                vocabularies.add(vocabulary);
            }
        } catch (Exception e) {
            throw new ApplicationException(
                    VocabularyErrorCode.VOCABULARY_IMPORT_INVALID_FILE,
                    VocabularyQuestionDetailMessageKey.VOCABULARY_IMPORT_INVALID_FILE,
                    e
            );
        }

        return vocabularies;
    }
}
