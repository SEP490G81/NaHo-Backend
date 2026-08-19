package org.naho.vocabulary.usecase;

import org.naho.i18n.message.question.VocabularyQuestionDetailMessageKey;
import org.naho.question.model.Vocabulary;
import org.naho.shared.exception.ApplicationException;
import org.naho.shared.port.out.TransactionPort;
import org.naho.vocabulary.exception.VocabularyErrorCode;
import org.naho.vocabulary.port.in.ImportVocabularyPort;
import org.naho.vocabulary.port.out.SaveVocabularyPort;
import org.naho.vocabulary.port.out.VocabularyExcelParserPort;

import java.io.InputStream;
import java.util.List;

public class ImportVocabularyUseCase implements ImportVocabularyPort {

    private final VocabularyExcelParserPort vocabularyExcelParserPort;
    private final SaveVocabularyPort saveVocabularyPort;
    private final TransactionPort transactionPort;

    public ImportVocabularyUseCase(VocabularyExcelParserPort vocabularyExcelParserPort,
                                   SaveVocabularyPort saveVocabularyPort,
                                   TransactionPort transactionPort) {
        this.vocabularyExcelParserPort = vocabularyExcelParserPort;
        this.saveVocabularyPort = saveVocabularyPort;
        this.transactionPort = transactionPort;
    }

    @Override
    public void importVocabulary(InputStream inputStream) {
        transactionPort.execute(() -> {
            List<Vocabulary> vocabularies = vocabularyExcelParserPort.parseVocabularyExcel(inputStream);

            if (vocabularies == null || vocabularies.isEmpty()) {
                throw new ApplicationException(
                        VocabularyErrorCode.VOCABULARY_IMPORT_EMPTY,
                        VocabularyQuestionDetailMessageKey.VOCABULARY_IMPORT_EMPTY
                );
            }

            saveVocabularyPort.saveAll(vocabularies);
            return null;
        });
    }
}

