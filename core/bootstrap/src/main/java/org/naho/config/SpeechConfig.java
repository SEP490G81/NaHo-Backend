package org.naho.config;

import org.naho.speech.azure.mapper.PronunciationAssessmentMapper;
import org.naho.speech.azure.mapper.WordAssessmentMapper;
import org.naho.speech.azure.port.out.AzureSpeechService;
import org.naho.speech.azure.usecase.AssessSpeechUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpeechConfig {
    @Bean
    public WordAssessmentMapper wordAssessmentMapper() {
        return new WordAssessmentMapper();
    }

    @Bean
    public PronunciationAssessmentMapper pronunciationAssessmentMapper(WordAssessmentMapper wordAssessmentMapper) {
        return new PronunciationAssessmentMapper(wordAssessmentMapper);
    }

    @Bean
    public AssessSpeechUseCase assessSpeechUseCase(AzureSpeechService azureSpeechService, PronunciationAssessmentMapper pronunciationAssessmentMapper) {
        return new AssessSpeechUseCase(azureSpeechService, pronunciationAssessmentMapper);
    }
}