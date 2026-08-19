package org.naho.question.usecase;

import org.naho.question.command.CreateSpeakingQuestionCommand;
import org.naho.question.model.SpeakingQuestion;
import org.naho.question.port.in.CreateSpeakingQuestionInputPort;
import org.naho.question.port.out.SpeakingQuestionRepositoryPort;
import org.naho.question.result.CreateSpeakingQuestionResult;
import org.naho.question.type.QuestionStatus;

public class CreateSpeakingQuestionUseCase implements CreateSpeakingQuestionInputPort {

    private final SpeakingQuestionRepositoryPort speakingQuestionRepositoryPort;
    private final org.naho.furigana.port.out.FuriganaGenerationPort furiganaGenerationPort;

    public CreateSpeakingQuestionUseCase(SpeakingQuestionRepositoryPort speakingQuestionRepositoryPort,
                                         org.naho.furigana.port.out.FuriganaGenerationPort furiganaGenerationPort) {
        this.speakingQuestionRepositoryPort = speakingQuestionRepositoryPort;
        this.furiganaGenerationPort = furiganaGenerationPort;
    }

    @Override
    public CreateSpeakingQuestionResult createSpeakingQuestion(CreateSpeakingQuestionCommand command) {
        // 2. Generate markup from raw text
        String japaneseNameMarkup = furiganaGenerationPort.generateFuriganaMarkup(command.japaneseName());
        String descriptionMarkup = command.description() != null
                ? furiganaGenerationPort.generateFuriganaMarkup(command.description())
                : null;
        String japaneseSampleAnswerMarkup = command.japaneseSampleAnswer() != null
                ? furiganaGenerationPort.generateFuriganaMarkup(command.japaneseSampleAnswer())
                : null;

        // 4. Determine initial status based on Creator Role
        QuestionStatus initialStatus = command.isContentManager() ? QuestionStatus.DRAFT : QuestionStatus.PRIVATE;

        // 5. Build Question
        SpeakingQuestion speakingQuestion = SpeakingQuestion.builder()
                .userId(command.userId())
                .japaneseNameMarkup(japaneseNameMarkup)
                .japaneseName(command.japaneseName())
                .vietnameseName(command.vietnameseName())
                .descriptionMarkup(descriptionMarkup)
                .description(command.description())
                .japaneseSampleAnswerMarkup(japaneseSampleAnswerMarkup)
                .japaneseSampleAnswer(command.japaneseSampleAnswer())
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
