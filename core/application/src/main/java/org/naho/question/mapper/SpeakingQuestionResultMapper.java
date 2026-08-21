package org.naho.question.mapper;

import org.naho.grammar.mapper.GrammarResultMapper;
import org.naho.grammar.result.GrammarResult;
import org.naho.question.model.SpeakingQuestion;
import org.naho.question.result.SpeakingQuestionListItemResult;
import org.naho.question.result.SpeakingQuestionResult;
import org.naho.vocabulary.mapper.VocabularyResultMapper;
import org.naho.vocabulary.result.VocabularyResult;

import java.util.ArrayList;
import java.util.List;

public class SpeakingQuestionResultMapper {
    private final GrammarResultMapper grammarResultMapper;
    private final VocabularyResultMapper vocabularyResultMapper;

    public SpeakingQuestionResultMapper(
            GrammarResultMapper grammarResultMapper,
            VocabularyResultMapper vocabularyResultMapper
    ) {
        this.grammarResultMapper = grammarResultMapper;
        this.vocabularyResultMapper = vocabularyResultMapper;
    }

    public SpeakingQuestionResult domainToResult(SpeakingQuestion domain, boolean showSampleAnswer) {
        if (domain == null) {
            return null;
        }

        List<GrammarResult> grammarResults = new ArrayList<>();

        if (domain.getGrammars() != null) {
            grammarResults = domain.getGrammars()
                    .stream()
                    .map(grammarResultMapper::domainToResult)
                    .toList();
        }

        List<VocabularyResult> vocabularyResults = new ArrayList<>();

        if (domain.getVocabularies() != null) {
            vocabularyResults = domain.getVocabularies()
                    .stream()
                    .map(vocabularyResultMapper::domainToResult)
                    .toList();
        }

        if (showSampleAnswer) {
            return SpeakingQuestionResult.builder()
                    .id(domain.getId())
                    .userId(domain.getUserId())
                    .speakingQuestionAudioFileId(domain.getSpeakingQuestionAudioFileId())
                    .japaneseName(domain.getJapaneseName())
                    .japaneseNameMarkup(domain.getJapaneseNameMarkup())
                    .vietnameseName(domain.getVietnameseName())
                    .description(domain.getDescription())
                    .descriptionMarkup(domain.getDescriptionMarkup())
                    .japaneseSampleAnswer(domain.getJapaneseSampleAnswer())
                    .japaneseSampleAnswerMarkup(domain.getJapaneseSampleAnswerMarkup())
                    .vietnameseSampleAnswer(domain.getVietnameseSampleAnswer())
                    .englishSampleAnswer(domain.getEnglishSampleAnswer())
                    .grammars(grammarResults)
                    .vocabularies(vocabularyResults)
                    .build();
        }
        return SpeakingQuestionResult.builder()
                .id(domain.getId())
                .userId(domain.getUserId())
                .speakingQuestionAudioFileId(domain.getSpeakingQuestionAudioFileId())
                .japaneseName(domain.getJapaneseName())
                .japaneseNameMarkup(domain.getJapaneseNameMarkup())
                .vietnameseName(domain.getVietnameseName())
                .description(domain.getDescription())
                .descriptionMarkup(domain.getDescriptionMarkup())
                .grammars(grammarResults)
                .vocabularies(vocabularyResults)
                .build();
    }

    public SpeakingQuestionListItemResult domainToListItemResult(SpeakingQuestion domain, boolean showSampleAnswer) {
        if (domain == null) {
            return null;
        }

        if (showSampleAnswer) {
            return SpeakingQuestionListItemResult.builder()
                    .id(domain.getId())
                    .userId(domain.getUserId())
                    .speakingQuestionAudioFileId(domain.getSpeakingQuestionAudioFileId())
                    .japaneseName(domain.getJapaneseName())
                    .japaneseNameMarkup(domain.getJapaneseNameMarkup())
                    .vietnameseName(domain.getVietnameseName())
                    .description(domain.getDescription())
                    .descriptionMarkup(domain.getDescriptionMarkup())
                    .japaneseSampleAnswer(domain.getJapaneseSampleAnswer())
                    .japaneseSampleAnswerMarkup(domain.getJapaneseSampleAnswerMarkup())
                    .vietnameseSampleAnswer(domain.getVietnameseSampleAnswer())
                    .englishSampleAnswer(domain.getEnglishSampleAnswer())
                    .build();
        }
        return SpeakingQuestionListItemResult.builder()
                .id(domain.getId())
                .userId(domain.getUserId())
                .speakingQuestionAudioFileId(domain.getSpeakingQuestionAudioFileId())
                .japaneseName(domain.getJapaneseName())
                .japaneseNameMarkup(domain.getJapaneseNameMarkup())
                .vietnameseName(domain.getVietnameseName())
                .description(domain.getDescription())
                .descriptionMarkup(domain.getDescriptionMarkup())
                .build();
    }
}
