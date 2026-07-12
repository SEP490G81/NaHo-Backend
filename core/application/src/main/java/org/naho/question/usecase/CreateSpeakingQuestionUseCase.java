package org.naho.question.usecase;

import org.naho.book.util.MarkupParserUtil;
import org.naho.i18n.message.question.SpeakingQuestionDetailMessageKey;
import org.naho.question.command.CreateSpeakingQuestionCommand;
import org.naho.question.exception.SpeakingQuestionErrorCode;
import org.naho.question.model.SpeakingQuestion;
import org.naho.question.port.in.CreateSpeakingQuestionInputPort;
import org.naho.question.port.out.SpeakingQuestionRepositoryPort;
import org.naho.question.result.CreateSpeakingQuestionResult;
import org.naho.question.type.QuestionStatus;
import org.naho.shared.exception.ApplicationException;

public class CreateSpeakingQuestionUseCase implements CreateSpeakingQuestionInputPort {

    private final SpeakingQuestionRepositoryPort speakingQuestionRepositoryPort;

    public CreateSpeakingQuestionUseCase(SpeakingQuestionRepositoryPort speakingQuestionRepositoryPort) {
        this.speakingQuestionRepositoryPort = speakingQuestionRepositoryPort;
    }

    @Override
    public CreateSpeakingQuestionResult createSpeakingQuestion(CreateSpeakingQuestionCommand command) {
        // 2. Extract raw text from markup
        String rawTitle = MarkupParserUtil.extractRawTextFromMarkup(command.titleMarkup());
        String rawDescription = MarkupParserUtil.extractRawTextFromMarkup(command.descriptionMarkup());

        // 4. Determine initial status based on Creator Role
        QuestionStatus initialStatus = command.isContentManager() ? QuestionStatus.DRAFT : QuestionStatus.PRIVATE;

        // 5. Build Question
        SpeakingQuestion speakingQuestion = SpeakingQuestion.builder()
                .userId(command.userId())
                .titleMarkup(command.titleMarkup())
                .descriptionMarkup(command.descriptionMarkup())
                .title(rawTitle)
                .description(rawDescription)
                .status(initialStatus)
                .build();

        // 6. Save and Return
        SpeakingQuestion savedSpeakingQuestion = speakingQuestionRepositoryPort.save(speakingQuestion);

        return new CreateSpeakingQuestionResult(
                savedSpeakingQuestion.getId(),
                savedSpeakingQuestion.getUserId(),
                savedSpeakingQuestion.getQuestionAudioFileId(),
                savedSpeakingQuestion.getTitle(),
                savedSpeakingQuestion.getTitleMarkup(),
                savedSpeakingQuestion.getDescription(),
                savedSpeakingQuestion.getDescriptionMarkup(),
                savedSpeakingQuestion.getStatus());
    }
}
