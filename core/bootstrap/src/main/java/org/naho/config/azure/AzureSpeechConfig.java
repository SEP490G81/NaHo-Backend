package org.naho.config.azure;

import org.naho.speech.azure.constant.AzureSpeechConfigProperties;
import org.naho.speech.azure.mapper.PronunciationAssessmentMapper;
import org.naho.speech.azure.mapper.WordAssessmentMapper;
import org.naho.speech.azure.port.in.SpeakingAssessmentInputPort;
import org.naho.speech.azure.port.out.AzureSpeechServicePort;
import org.naho.speech.azure.usecase.SpeakingAssessmentUseCase;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(AzureSpeechConfigProperties.class)
public class AzureSpeechConfig {

    @Bean
    public WordAssessmentMapper wordAssessmentMapper() {
        return new WordAssessmentMapper();
    }

    @Bean
    public PronunciationAssessmentMapper pronunciationAssessmentMapper(WordAssessmentMapper wordAssessmentMapper) {
        return new PronunciationAssessmentMapper(wordAssessmentMapper);
    }

    @Bean
    public SpeakingAssessmentInputPort speakingAssessmentInputPort(
            AzureSpeechServicePort azureSpeechServicePort,
            PronunciationAssessmentMapper pronunciationAssessmentMapper
    ) {
        return new SpeakingAssessmentUseCase(azureSpeechServicePort, pronunciationAssessmentMapper);
    }
}
