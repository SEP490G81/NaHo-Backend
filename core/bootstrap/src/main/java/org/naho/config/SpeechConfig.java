package org.naho.config;

import org.naho.speech.mapper.PronunciationAssessmentMapper;
import org.naho.speech.port.out.SpeechAssessmentService;
import org.naho.speech.usecase.AssessSpeechUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpeechConfig {
    @Bean
    public PronunciationAssessmentMapper pronunciationAssessmentMapper() {
        return new PronunciationAssessmentMapper();
    }

    @Bean
    public AssessSpeechUseCase assessSpeechUseCase(SpeechAssessmentService speechAssessmentService, PronunciationAssessmentMapper pronunciationAssessmentMapper) {
        return new AssessSpeechUseCase(speechAssessmentService, pronunciationAssessmentMapper);
    }


}