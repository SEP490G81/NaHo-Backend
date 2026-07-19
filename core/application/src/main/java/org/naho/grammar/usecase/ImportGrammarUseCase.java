package org.naho.grammar.usecase;

import org.naho.grammar.exception.GrammarErrorCode;
import org.naho.grammar.port.in.ImportGrammarPort;
import org.naho.grammar.port.out.GrammarExcelParserPort;
import org.naho.i18n.message.question.GrammarDetailMessageKey;
import org.naho.question.model.Grammar;
import org.naho.question.port.out.SaveGrammarPort;
import org.naho.shared.exception.ApplicationException;
import org.naho.shared.port.out.TransactionPort;

import java.io.InputStream;
import java.util.List;

public class ImportGrammarUseCase implements ImportGrammarPort {

    private final GrammarExcelParserPort grammarExcelParserPort;
    private final SaveGrammarPort saveGrammarPort;
    private final TransactionPort transactionPort;

    public ImportGrammarUseCase(GrammarExcelParserPort grammarExcelParserPort,
                                SaveGrammarPort saveGrammarPort,
                                TransactionPort transactionPort) {
        this.grammarExcelParserPort = grammarExcelParserPort;
        this.saveGrammarPort = saveGrammarPort;
        this.transactionPort = transactionPort;
    }

    @Override
    public void importGrammar(InputStream inputStream) {
        transactionPort.execute(() -> {
            List<Grammar> grammars = grammarExcelParserPort.parseGrammarExcel(inputStream);

            if (grammars == null || grammars.isEmpty()) {
                throw new ApplicationException(
                        GrammarErrorCode.GRAMMAR_IMPORT_EMPTY,
                        GrammarDetailMessageKey.GRAMMAR_IMPORT_EMPTY
                );
            }

            saveGrammarPort.saveAll(grammars);
            return null;
        });
    }
}

