package org.naho.config;

import org.naho.speech.port.out.SpeechAssessmentService;
import org.naho.speech.usecase.AssessSpeechUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpeechConfig {

    @Bean
    public AssessSpeechUseCase assessSpeechUseCase(SpeechAssessmentService speechAssessmentService) {
        return new AssessSpeechUseCase(speechAssessmentService);
    }
}