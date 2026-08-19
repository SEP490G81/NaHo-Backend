package org.naho.vocabulary.adapter;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.naho.question.model.Vocabulary;
import org.naho.vocabulary.port.out.ExcelWriterPort;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Component
public class ExcelWriterAdapter implements ExcelWriterPort {

    @Override
    public ByteArrayInputStream writeVocabulariesToExcel(List<Vocabulary> vocabularies) {
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Vocabularies");

            // Create Header Row
            Row headerRow = sheet.createRow(0);
            String[] headers = {"Reading", "Japanese", "Vietnamese Meaning", "English Meaning"};

            // Header font and style
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setColor(IndexedColors.WHITE.getIndex());

            CellStyle headerCellStyle = workbook.createCellStyle();
            headerCellStyle.setFont(headerFont);
            headerCellStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
            headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerCellStyle);
            }

            // Populate Data
            int rowIdx = 1;
            for (Vocabulary vocab : vocabularies) {
                Row row = sheet.createRow(rowIdx++);

                // Col 0: Reading
                row.createCell(0).setCellValue(vocab.getReading() != null ? vocab.getReading() : "");
                // Col 1: Japanese
                row.createCell(1).setCellValue(vocab.getJapanese() != null ? vocab.getJapanese() : "");
                // Col 2: Vietnamese
                row.createCell(2)
                        .setCellValue(vocab.getVietnameseMeaningText() != null ? vocab.getVietnameseMeaningText() : "");
                // Col 3: English
                row.createCell(3)
                        .setCellValue(vocab.getEnglishMeaningText() != null ? vocab.getEnglishMeaningText() : "");
            }

            // Auto-size columns
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());

        } catch (IOException e) {
            throw new RuntimeException("Failed to export vocabulary list to Excel", e);
        }
    }
}
