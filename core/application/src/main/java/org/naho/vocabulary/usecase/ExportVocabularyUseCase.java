package org.naho.vocabulary.usecase;

import org.naho.question.model.Vocabulary;
import org.naho.question.port.out.VocabulariesQuestionPort;
import org.naho.shared.exception.ApplicationException;
import org.naho.vocabulary.exception.VocabularyErrorCode;
import org.naho.vocabulary.port.in.ExportVocabularyInputPort;
import org.naho.vocabulary.port.out.ExcelWriterPort;
import org.naho.vocabulary.port.out.VocabularyRepositoryPort;

import java.io.ByteArrayInputStream;
import java.util.List;

public class ExportVocabularyUseCase implements ExportVocabularyInputPort {

    private final VocabularyRepositoryPort vocabularyRepositoryPort;
    private final VocabulariesQuestionPort vocabulariesQuestionPort;
    private final ExcelWriterPort excelWriterPort;

    public ExportVocabularyUseCase(
            VocabularyRepositoryPort vocabularyRepositoryPort,
            VocabulariesQuestionPort vocabulariesQuestionPort,
            ExcelWriterPort excelWriterPort
    ) {
        this.vocabularyRepositoryPort = vocabularyRepositoryPort;
        this.vocabulariesQuestionPort = vocabulariesQuestionPort;
        this.excelWriterPort = excelWriterPort;
    }

    @Override
    public ByteArrayInputStream exportByQuestion(Long questionId) {
        List<Vocabulary> list = vocabularyRepositoryPort.findVocabularyList(questionId);
        if (list == null || list.isEmpty()) {
            throw new ApplicationException(VocabularyErrorCode.VOCABULARY_EXPORT_NOT_FOUND, "vocabulary.export.not_found", questionId, "-");
        }
        return excelWriterPort.writeVocabulariesToExcel(list);
    }

    @Override
    public ByteArrayInputStream exportByObjective(Long objectiveId) {
        List<Vocabulary> list = vocabulariesQuestionPort.findVocabularyListOfObjective(objectiveId);
        if (list == null || list.isEmpty()) {
            throw new ApplicationException(VocabularyErrorCode.VOCABULARY_EXPORT_NOT_FOUND, "vocabulary.export.not_found", "-", objectiveId);
        }
        return excelWriterPort.writeVocabulariesToExcel(list);
    }
}
