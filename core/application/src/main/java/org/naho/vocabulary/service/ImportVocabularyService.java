package org.naho.vocabulary.service;

import org.naho.vocabulary.model.Vocabulary;
import org.naho.vocabulary.port.out.ExcelParserPort;
import org.naho.vocabulary.port.out.SaveVocabularyPort;
import org.naho.vocabulary.usecase.ImportVocabularyUseCase;
import org.naho.shared.port.out.TransactionPort;
import org.naho.shared.exception.ApplicationException;
import org.naho.vocabulary.exception.VocabularyErrorCode;

import java.io.InputStream;
import java.util.List;

public class ImportVocabularyService implements ImportVocabularyUseCase {

    private final ExcelParserPort excelParserPort;
    private final SaveVocabularyPort saveVocabularyPort;
    private final TransactionPort transactionPort;

    public ImportVocabularyService(ExcelParserPort excelParserPort, SaveVocabularyPort saveVocabularyPort, TransactionPort transactionPort) {
        this.excelParserPort = excelParserPort;
        this.saveVocabularyPort = saveVocabularyPort;
        this.transactionPort = transactionPort;
    }

    @Override
    public void importVocabulary(InputStream inputStream) {
        transactionPort.execute(() -> {
            List<Vocabulary> vocabularies = excelParserPort.parseExcel(inputStream);
            if (vocabularies == null || vocabularies.isEmpty()) {
                throw new ApplicationException(VocabularyErrorCode.VOCABULARY_IMPORT_EMPTY, "vocabulary.import.empty");
            }
            saveVocabularyPort.saveAll(vocabularies);
            return null;
        });
    }
}
