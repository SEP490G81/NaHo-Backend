package org.naho.question.usecase;

import org.naho.book.util.MarkupParserUtil;
import org.naho.i18n.message.question.SpeakingQuestionDetailMessageKey;
import org.naho.question.command.UpdateSpeakingQuestionCommand;
import org.naho.question.exception.SpeakingQuestionErrorCode;
import org.naho.question.model.SpeakingQuestion;
import org.naho.question.port.in.UpdateSpeakingQuestionInputPort;
import org.naho.question.port.out.SpeakingQuestionRepositoryPort;
import org.naho.question.result.UpdateSpeakingQuestionResult;
import org.naho.shared.exception.ApplicationException;

import java.util.Optional;

public class UpdateSpeakingQuestionUseCase implements UpdateSpeakingQuestionInputPort {

    private final SpeakingQuestionRepositoryPort speakingQuestionRepositoryPort;

    public UpdateSpeakingQuestionUseCase(SpeakingQuestionRepositoryPort speakingQuestionRepositoryPort) {
        this.speakingQuestionRepositoryPort = speakingQuestionRepositoryPort;
    }

    @Override
    public UpdateSpeakingQuestionResult updateSpeakingQuestion(UpdateSpeakingQuestionCommand command) {
        // 1. Verify Question exists
        Optional<SpeakingQuestion> questionOpt = speakingQuestionRepositoryPort.findById(command.id());
        if (questionOpt.isEmpty()) {
            throw new ApplicationException(
                    SpeakingQuestionErrorCode.SPEAKING_QUESTION_NOT_FOUND,
                    SpeakingQuestionDetailMessageKey.SPEAKING_QUESTION_NOT_FOUND,
                    command.id()
            );
        }

        SpeakingQuestion speakingQuestion = questionOpt.get();

        // 2. Check update rule: Cannot update if already answered
        boolean isAnswered = speakingQuestionRepositoryPort.hasSpeakingQuestionBeenAnswered(command.id());
        if (isAnswered) {
            throw new ApplicationException(
                    SpeakingQuestionErrorCode.SPEAKING_QUESTION_UPDATE_FORBIDDEN,
                    SpeakingQuestionDetailMessageKey.SPEAKING_QUESTION_UPDATE_FORBIDDEN,
                    command.id()
            );
        }

        // 3. Extract raw text from markup
        String rawJapaneseName = MarkupParserUtil.extractRawTextFromMarkup(command.japaneseNameMarkup());
        String rawDescription = MarkupParserUtil.extractRawTextFromMarkup(command.descriptionMarkup());
        String rawJapaneseSampleAnswer = MarkupParserUtil.extractRawTextFromMarkup(command.japaneseSampleAnswerMarkup());

        // 5. Update Domain Model
        speakingQuestion.update(
                rawJapaneseName,
                command.japaneseNameMarkup(),
                command.vietnameseName(),
                rawDescription,
                command.descriptionMarkup(),
                rawJapaneseSampleAnswer,
                command.japaneseSampleAnswerMarkup(),
                command.vietnameseSampleAnswer(),
                command.englishSampleAnswer(),
                null // Audio is null for now
        );

        // 6. Save and Return
        SpeakingQuestion updatedSpeakingQuestion = speakingQuestionRepositoryPort.save(speakingQuestion);

        return new UpdateSpeakingQuestionResult(
                updatedSpeakingQuestion.getId(),
                updatedSpeakingQuestion.getUserId(),
                updatedSpeakingQuestion.getJapaneseName(),
                updatedSpeakingQuestion.getJapaneseNameMarkup(),
                updatedSpeakingQuestion.getVietnameseName(),
                updatedSpeakingQuestion.getDescription(),
                updatedSpeakingQuestion.getDescriptionMarkup(),
                updatedSpeakingQuestion.getJapaneseSampleAnswer(),
                updatedSpeakingQuestion.getJapaneseSampleAnswerMarkup(),
                updatedSpeakingQuestion.getVietnameseSampleAnswer(),
                updatedSpeakingQuestion.getEnglishSampleAnswer(),
                updatedSpeakingQuestion.getStatus()
        );
    }
}
