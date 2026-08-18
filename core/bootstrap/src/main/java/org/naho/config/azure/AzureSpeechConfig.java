package org.naho.config.azure;

import org.naho.speech.azure.constant.AzureSpeechConfigProperties;
import org.naho.speech.azure.mapper.SpeechAssessmentResultMapper;
import org.naho.speech.azure.mapper.WordAssessmentResultMapper;
import org.naho.speech.azure.port.in.SpeechAssessmentInputPort;
import org.naho.speech.azure.port.out.AzureSpeechServicePort;
import org.naho.speech.azure.usecase.SpeechAssessmentUseCase;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(AzureSpeechConfigProperties.class)
public class AzureSpeechConfig {

    @Bean
    public WordAssessmentResultMapper wordAssessmentMapper() {
        return new WordAssessmentResultMapper();
    }

    @Bean
    public SpeechAssessmentResultMapper pronunciationAssessmentMapper(WordAssessmentResultMapper wordAssessmentResultMapper) {
        return new SpeechAssessmentResultMapper(wordAssessmentResultMapper);
    }

    @Bean
    public SpeechAssessmentInputPort speakingAssessmentInputPort(
            AzureSpeechServicePort azureSpeechServicePort,
            SpeechAssessmentResultMapper speechAssessmentResultMapper
    ) {
        return new SpeechAssessmentUseCase(azureSpeechServicePort, speechAssessmentResultMapper);
    }
}
