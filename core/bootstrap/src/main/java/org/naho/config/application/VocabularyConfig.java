package org.naho.config.application;

import org.naho.learning.port.in.CrudUserLearningProgressInputPort;
import org.naho.learning.port.in.UserLearningStreakInputPort;
import org.naho.learning.port.out.LearningPathNodeRepositoryPort;
import org.naho.learning.port.out.UserLearningProgressRepositoryPort;
import org.naho.learning.port.out.UserNodeProgressRepositoryPort;
import org.naho.point.port.in.CrudPointHistoryInputPort;
import org.naho.question.port.in.CompleteVocabularyQuestionInputPort;
import org.naho.question.port.in.SearchVocabulariesOfQuestionInputPort;
import org.naho.question.port.out.VocabulariesQuestionPort;
import org.naho.question.usecase.CompleteVocabularyQuestionUseCase;
import org.naho.question.usecase.SearchVocabulariesOfQuestionUsecase;
import org.naho.shared.port.out.TransactionPort;
import org.naho.vocabulary.port.in.ExportVocabularyInputPort;
import org.naho.vocabulary.port.in.GetRandomVocabularyQuizInputPort;
import org.naho.vocabulary.port.in.GetVocabulariesOfObjectiveInputPort;
import org.naho.vocabulary.port.in.ImportVocabularyPort;
import org.naho.vocabulary.port.out.ExcelWriterPort;
import org.naho.vocabulary.port.out.RandomVocabularyPort;
import org.naho.vocabulary.port.out.SaveVocabularyPort;
import org.naho.vocabulary.port.out.VocabularyExcelParserPort;
import org.naho.vocabulary.port.out.VocabularyPort;
import org.naho.vocabulary.usecase.ExportVocabularyUseCase;
import org.naho.vocabulary.usecase.GetRandomVocabularyQuizUseCase;
import org.naho.vocabulary.usecase.GetVocabulariesOfObjectiveUseCase;
import org.naho.vocabulary.usecase.ImportVocabularyUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class VocabularyConfig {

    @Bean
    public GetRandomVocabularyQuizInputPort getRandomVocabularyQuizInputPort(
            RandomVocabularyPort randomVocabularyPort
    ) {
        return new GetRandomVocabularyQuizUseCase(randomVocabularyPort);
    }

    @Bean
    public ImportVocabularyPort importVocabularyUseCase(
            VocabularyExcelParserPort vocabularyExcelParserPort,
            SaveVocabularyPort saveVocabularyPort,
            TransactionPort transactionPort
    ) {
        return new ImportVocabularyUseCase(vocabularyExcelParserPort, saveVocabularyPort, transactionPort);
    }


    @Bean
    public GetVocabulariesOfObjectiveInputPort getVocabulariesOfObjectiveInputPort(
            VocabulariesQuestionPort vocabulariesQuestionPort
    ) {
        return new GetVocabulariesOfObjectiveUseCase(vocabulariesQuestionPort);
    }

    @Bean
    public SearchVocabulariesOfQuestionInputPort searchVocabulariesOfQuestionInputPort(
            VocabularyPort vocabularyPort
    ) {
        return new SearchVocabulariesOfQuestionUsecase(vocabularyPort);
    }

    @Bean
    public ExportVocabularyInputPort exportVocabularyInputPort(
            VocabularyPort vocabularyPort,
            VocabulariesQuestionPort vocabulariesQuestionPort,
            ExcelWriterPort excelWriterPort
    ) {
        return new ExportVocabularyUseCase(vocabularyPort, vocabulariesQuestionPort, excelWriterPort);
    }

    @Bean
    public CompleteVocabularyQuestionInputPort completeVocabularyQuestionInputPort(
            UserNodeProgressRepositoryPort userNodeProgressRepositoryPort,
            UserLearningProgressRepositoryPort userLearningProgressRepositoryPort,
            CrudPointHistoryInputPort crudPointHistoryInputPort,
            TransactionPort transactionPort,
            UserLearningStreakInputPort userLearningStreakInputPort,
            CrudUserLearningProgressInputPort crudUserLearningProgressInputPort,
            LearningPathNodeRepositoryPort learningPathNodeRepositoryPort
    ) {
        return new CompleteVocabularyQuestionUseCase(
                userNodeProgressRepositoryPort,
                userLearningProgressRepositoryPort,
                crudPointHistoryInputPort,
                transactionPort,
                userLearningStreakInputPort,
                crudUserLearningProgressInputPort,
                learningPathNodeRepositoryPort
        );
    }
}

