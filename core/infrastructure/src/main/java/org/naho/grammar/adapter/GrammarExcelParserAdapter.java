package org.naho.grammar.adapter;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.naho.grammar.exception.GrammarErrorCode;
import org.naho.grammar.port.out.GrammarExcelParserPort;
import org.naho.i18n.message.question.GrammarDetailMessageKey;
import org.naho.question.model.Grammar;
import org.naho.shared.exception.ApplicationException;
import org.naho.shared.util.ExcelUtil;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Component
public class GrammarExcelParserAdapter implements GrammarExcelParserPort {

    @Override
    public List<Grammar> parseGrammarExcel(InputStream inputStream) {
        List<Grammar> grammars = new ArrayList<>();

        try (Workbook workbook = new XSSFWorkbook(inputStream)) {
            Sheet grammarSheet = workbook.getSheetAt(0);

            if (grammarSheet == null) {
                return grammars;
            }

            for (int rowNum = 1; rowNum <= grammarSheet.getLastRowNum(); rowNum++) {
                Row grammarRow = grammarSheet.getRow(rowNum);
                if (grammarRow == null) {
                    continue;
                }

                String japanese = ExcelUtil.getCellValueAsString(grammarRow.getCell(0));
                String reading = ExcelUtil.getCellValueAsString(grammarRow.getCell(1));
                String vietMeaning = ExcelUtil.getCellValueAsString(grammarRow.getCell(2));
                String engMeaning = ExcelUtil.getCellValueAsString(grammarRow.getCell(3));

                if (reading.isEmpty() && japanese.isEmpty() && vietMeaning.isEmpty()) {
                    continue;
                }

                Grammar grammar = Grammar.builder()
                        .japanese(japanese)
                        .reading(reading)
                        .vietnameseMeaningText(vietMeaning)
                        .englishMeaningText(engMeaning)
                        .build();

                grammars.add(grammar);
            }
        } catch (Exception e) {
            throw new ApplicationException(
                    GrammarErrorCode.GRAMMAR_IMPORT_INVALID_FILE,
                    GrammarDetailMessageKey.GRAMMAR_IMPORT_INVALID_FILE,
                    e
            );
        }
        return grammars;
    }
}
