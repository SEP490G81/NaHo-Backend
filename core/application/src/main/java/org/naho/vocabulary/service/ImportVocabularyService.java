package org.naho.vocabulary.service;

import org.naho.question.model.Grammar;
import org.naho.question.port.out.SaveGrammarPort;
import org.naho.shared.exception.ApplicationException;
import org.naho.shared.port.out.TransactionPort;
import org.naho.vocabulary.exception.VocabularyErrorCode;
import org.naho.vocabulary.model.Vocabulary;
import org.naho.vocabulary.model.VocabularyGrammarImportResult;
import org.naho.vocabulary.port.out.ExcelParserPort;
import org.naho.vocabulary.port.out.SaveVocabularyPort;
import org.naho.vocabulary.usecase.ImportVocabularyUseCase;

import java.io.InputStream;
import java.util.List;

public class ImportVocabularyService implements ImportVocabularyUseCase {

    private final ExcelParserPort excelParserPort;
    private final SaveVocabularyPort saveVocabularyPort;
    private final SaveGrammarPort saveGrammarPort;
    private final TransactionPort transactionPort;

    public ImportVocabularyService(ExcelParserPort excelParserPort, 
                                   SaveVocabularyPort saveVocabularyPort, 
                                   SaveGrammarPort saveGrammarPort,
                                   TransactionPort transactionPort) {
        this.excelParserPort = excelParserPort;
        this.saveVocabularyPort = saveVocabularyPort;
        this.saveGrammarPort = saveGrammarPort;
        this.transactionPort = transactionPort;
    }

    @Override
    public void importVocabulary(InputStream inputStream) {
        transactionPort.execute(() -> {
            VocabularyGrammarImportResult result = excelParserPort.parseExcel(inputStream);
            List<Vocabulary> vocabularies = result.vocabularies();
            List<Grammar> grammars = result.grammars();

            boolean isVocabEmpty = vocabularies == null || vocabularies.isEmpty();
            boolean isGrammarEmpty = grammars == null || grammars.isEmpty();

            if (isVocabEmpty && isGrammarEmpty) {
                throw new ApplicationException(VocabularyErrorCode.VOCABULARY_IMPORT_EMPTY, "vocabulary.import.empty");
            }

            if (!isVocabEmpty) {
                saveVocabularyPort.saveAll(vocabularies);
            }

            if (!isGrammarEmpty) {
                saveGrammarPort.saveAll(grammars);
            }

            return null;
        });
    }
}
