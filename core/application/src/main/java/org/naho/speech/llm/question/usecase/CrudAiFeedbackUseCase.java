package org.naho.speech.llm.question.usecase;

import org.naho.i18n.message.question.SpeakingQuestionDetailMessageKey;
import org.naho.question.exception.SpeakingQuestionErrorCode;
import org.naho.shared.exception.ApplicationException;
import org.naho.speech.llm.model.question.AiFeedback;
import org.naho.speech.llm.question.mapper.AiFeedbackResultMapper;
import org.naho.speech.llm.question.port.in.CrudAiFeedbackInputPort;
import org.naho.speech.llm.question.port.out.AiFeedbackRepositoryPort;
import org.naho.speech.llm.question.result.AiFeedbackResult;

public class CrudAiFeedbackUseCase implements CrudAiFeedbackInputPort {
    private final AiFeedbackRepositoryPort aiFeedbackRepositoryPort;
    private final AiFeedbackResultMapper aiFeedbackResultMapper;

    public CrudAiFeedbackUseCase(
            AiFeedbackRepositoryPort aiFeedbackRepositoryPort,
            AiFeedbackResultMapper aiFeedbackResultMapper
    ) {
        this.aiFeedbackRepositoryPort = aiFeedbackRepositoryPort;
        this.aiFeedbackResultMapper = aiFeedbackResultMapper;
    }

    @Override
    public AiFeedbackResult findById(Long id) {
        if (id == null) {
            throw new ApplicationException(
                    SpeakingQuestionErrorCode.SPEAKING_QUESTION_NOT_FOUND,
                    SpeakingQuestionDetailMessageKey.CONTENT_ASSESSMENT_NOT_FOUND
            );
        }

        AiFeedback aiFeedback = aiFeedbackRepositoryPort
                .findById(id)
                .orElseThrow(() -> new ApplicationException(
                        SpeakingQuestionErrorCode.SPEAKING_QUESTION_NOT_FOUND,
                        SpeakingQuestionDetailMessageKey.CONTENT_ASSESSMENT_NOT_FOUND,
                        id
                ));

        return aiFeedbackResultMapper.domainToResult(aiFeedback);
    }
}

