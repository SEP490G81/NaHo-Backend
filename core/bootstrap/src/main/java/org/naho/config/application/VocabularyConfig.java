package org.naho.config.application;

import org.naho.question.port.out.VocabulariesQuestionPort;
import org.naho.vocabulary.port.in.GetVocabulariesOfObjectiveInputPort;
import org.naho.vocabulary.port.out.ExcelParserPort;
import org.naho.vocabulary.port.out.SaveVocabularyPort;
import org.naho.vocabulary.service.ImportVocabularyService;
import org.naho.vocabulary.usecase.GetVocabulariesOfObjectiveUseCase;
import org.naho.vocabulary.usecase.ImportVocabularyUseCase;
import org.naho.shared.port.out.TransactionPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class VocabularyConfig {

    @Bean
    public ImportVocabularyUseCase importVocabularyUseCase(
            ExcelParserPort excelParserPort,
            SaveVocabularyPort saveVocabularyPort,
            TransactionPort transactionPort
    ) {
        return new ImportVocabularyService(excelParserPort, saveVocabularyPort, transactionPort);
    }

    @Bean
    public GetVocabulariesOfObjectiveInputPort getVocabulariesOfObjectiveInputPort(
            VocabulariesQuestionPort vocabulariesQuestionPort
    ) {
        return new GetVocabulariesOfObjectiveUseCase(vocabulariesQuestionPort);
    }
}
