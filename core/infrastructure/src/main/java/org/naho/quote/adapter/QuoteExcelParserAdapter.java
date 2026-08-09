package org.naho.quote.adapter;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.naho.i18n.message.quote.QuoteDetailMessageKey;
import org.naho.quote.exception.QuoteErrorCode;
import org.naho.quote.model.Quote;
import org.naho.quote.port.out.QuoteExcelParserPort;
import org.naho.shared.exception.ApplicationException;
import org.naho.shared.util.ExcelUtil;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Component
public class QuoteExcelParserAdapter implements QuoteExcelParserPort {

    @Override
    public List<Quote> parseQuoteExcel(InputStream inputStream) {
        List<Quote> quotes = new ArrayList<>();

        try (Workbook workbook = new XSSFWorkbook(inputStream)) {
            Sheet quoteSheet = workbook.getSheetAt(0);

            if (quoteSheet == null) {
                return quotes;
            }

            for (int rowNum = 1; rowNum <= quoteSheet.getLastRowNum(); rowNum++) {
                Row quoteRow = quoteSheet.getRow(rowNum);
                if (quoteRow == null) {
                    continue;
                }

                String kanji = ExcelUtil.getCellValueAsString(quoteRow.getCell(1));
                String hiragana = ExcelUtil.getCellValueAsString(quoteRow.getCell(2));
                String romaji = ExcelUtil.getCellValueAsString(quoteRow.getCell(3));
                String translation = ExcelUtil.getCellValueAsString(quoteRow.getCell(4));
                String kanjiDetail = ExcelUtil.getCellValueAsString(quoteRow.getCell(5));

                if (kanji.isEmpty() && hiragana.isEmpty() && translation.isEmpty()) {
                    continue;
                }

                Quote quote = Quote.builder()
                        .kanji(kanji)
                        .hiragana(hiragana)
                        .romaji(romaji)
                        .translation(translation)
                        .kanjiDetail(kanjiDetail)
                        .build();

                quotes.add(quote);
            }
        } catch (Exception e) {
            throw new ApplicationException(
                    QuoteErrorCode.QUOTE_IMPORT_FAILED,
                    QuoteDetailMessageKey.QUOTE_IMPORT_FAILED,
                    e
            );
        }

        return quotes;
    }
}
