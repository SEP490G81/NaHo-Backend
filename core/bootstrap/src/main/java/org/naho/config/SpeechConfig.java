package org.naho.config;

import org.naho.speech.azure.mapper.PronunciationAssessmentMapper;
import org.naho.speech.azure.mapper.WordAssessmentMapper;
import org.naho.speech.azure.port.out.AzureSpeechServicePort;
import org.naho.speech.azure.port.out.TextToSpeechServicePort;
import org.naho.speech.azure.usecase.AssessSpeechUseCase;
import org.naho.speech.azure.usecase.TextToSpeechUseCase;
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
    public AssessSpeechUseCase assessSpeechUseCase(AzureSpeechServicePort azureSpeechServicePort, PronunciationAssessmentMapper pronunciationAssessmentMapper) {
        return new AssessSpeechUseCase(azureSpeechServicePort, pronunciationAssessmentMapper);
    }

    @Bean
    public TextToSpeechUseCase textToSpeechUseCase(
            TextToSpeechServicePort textToSpeechServicePort
    ) {
        return new TextToSpeechUseCase(textToSpeechServicePort);
        }
}