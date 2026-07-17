package org.naho.vocabulary.usecase;

import org.naho.question.port.out.VocabulariesQuestionPort;
import org.naho.shared.exception.ApplicationException;
import org.naho.vocabulary.exception.VocabularyErrorCode;
import org.naho.vocabulary.model.Vocabulary;
import org.naho.vocabulary.port.in.ExportVocabularyInputPort;
import org.naho.vocabulary.port.out.ExcelWriterPort;
import org.naho.vocabulary.port.out.VocabularyPort;

import org.naho.shared.exception.ApplicationException;
import org.naho.vocabulary.exception.VocabularyErrorCode;

import java.io.ByteArrayInputStream;
import java.util.List;

public class ExportVocabularyUseCase implements ExportVocabularyInputPort {

    private final VocabularyPort vocabularyPort;
    private final VocabulariesQuestionPort vocabulariesQuestionPort;
    private final ExcelWriterPort excelWriterPort;

    public ExportVocabularyUseCase(VocabularyPort vocabularyPort,
                                   VocabulariesQuestionPort vocabulariesQuestionPort,
                                   ExcelWriterPort excelWriterPort) {
        this.vocabularyPort = vocabularyPort;
        this.vocabulariesQuestionPort = vocabulariesQuestionPort;
        this.excelWriterPort = excelWriterPort;
    }

    @Override
    public ByteArrayInputStream exportByQuestion(int questionId) {
        List<Vocabulary> list = vocabularyPort.findVocabularyList(questionId);
        if (list == null || list.isEmpty()) {
            throw new ApplicationException(VocabularyErrorCode.VOCABULARY_EXPORT_NOT_FOUND, "vocabulary.export.not_found", questionId, "-");
        }
        return excelWriterPort.writeVocabulariesToExcel(list);
    }

    @Override
    public ByteArrayInputStream exportByObjective(int objectiveId) {
        List<Vocabulary> list = vocabulariesQuestionPort.findVocabularyListOfObjective(objectiveId);
        if (list == null || list.isEmpty()) {
            throw new ApplicationException(VocabularyErrorCode.VOCABULARY_EXPORT_NOT_FOUND, "vocabulary.export.not_found", "-", objectiveId);
        }
        return excelWriterPort.writeVocabulariesToExcel(list);
    }
}
