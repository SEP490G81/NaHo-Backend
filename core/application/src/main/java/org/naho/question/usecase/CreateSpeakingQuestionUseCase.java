package org.naho.question.usecase;

import org.naho.book.util.MarkupParserUtil;
import org.naho.question.command.CreateSpeakingQuestionCommand;
import org.naho.question.model.SpeakingQuestion;
import org.naho.question.port.in.CreateSpeakingQuestionInputPort;
import org.naho.question.port.out.SpeakingQuestionRepositoryPort;
import org.naho.question.result.CreateSpeakingQuestionResult;
import org.naho.question.type.QuestionStatus;

public class CreateSpeakingQuestionUseCase implements CreateSpeakingQuestionInputPort {

    private final SpeakingQuestionRepositoryPort speakingQuestionRepositoryPort;

    public CreateSpeakingQuestionUseCase(SpeakingQuestionRepositoryPort speakingQuestionRepositoryPort) {
        this.speakingQuestionRepositoryPort = speakingQuestionRepositoryPort;
    }

    @Override
    public CreateSpeakingQuestionResult createSpeakingQuestion(CreateSpeakingQuestionCommand command) {
        // 2. Extract raw text from markup
        String rawJapaneseName = MarkupParserUtil.extractRawTextFromMarkup(command.japaneseNameMarkup());
        String rawDescription = MarkupParserUtil.extractRawTextFromMarkup(command.descriptionMarkup());
        String rawJapaneseSampleAnswer = MarkupParserUtil.extractRawTextFromMarkup(command.japaneseSampleAnswerMarkup());

        // 4. Determine initial status based on Creator Role
        QuestionStatus initialStatus = command.isContentManager() ? QuestionStatus.DRAFT : QuestionStatus.PRIVATE;

        // 5. Build Question
        SpeakingQuestion speakingQuestion = SpeakingQuestion.builder()
                .userId(command.userId())
                .japaneseNameMarkup(command.japaneseNameMarkup())
                .japaneseName(rawJapaneseName)
                .vietnameseName(command.vietnameseName())
                .descriptionMarkup(command.descriptionMarkup())
                .description(rawDescription)
                .japaneseSampleAnswerMarkup(command.japaneseSampleAnswerMarkup())
                .japaneseSampleAnswer(rawJapaneseSampleAnswer)
                .vietnameseSampleAnswer(command.vietnameseSampleAnswer())
                .englishSampleAnswer(command.englishSampleAnswer())
                .status(initialStatus)
                .build();

        // 6. Save and Return
        SpeakingQuestion savedSpeakingQuestion = speakingQuestionRepositoryPort.save(speakingQuestion);

        return new CreateSpeakingQuestionResult(
                savedSpeakingQuestion.getId(),
                savedSpeakingQuestion.getUserId(),
                savedSpeakingQuestion.getSpeakingQuestionAudioFileId(),
                savedSpeakingQuestion.getJapaneseName(),
                savedSpeakingQuestion.getJapaneseNameMarkup(),
                savedSpeakingQuestion.getVietnameseName(),
                savedSpeakingQuestion.getDescription(),
                savedSpeakingQuestion.getDescriptionMarkup(),
                savedSpeakingQuestion.getJapaneseSampleAnswer(),
                savedSpeakingQuestion.getJapaneseSampleAnswerMarkup(),
                savedSpeakingQuestion.getVietnameseSampleAnswer(),
                savedSpeakingQuestion.getEnglishSampleAnswer(),
                savedSpeakingQuestion.getStatus());
    }
}
