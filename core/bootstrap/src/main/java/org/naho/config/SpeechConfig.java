package org.naho.config;

import org.naho.speech.mapper.AudioSpeechMapper;
import org.naho.speech.mapper.PronunciationAssessmentMapper;
import org.naho.speech.port.out.SpeechAssessmentService;
import org.naho.speech.port.out.TextToSpeechService;
import org.naho.speech.usecase.AssessSpeechUseCase;
import org.naho.speech.usecase.TextToSpeechUseCase;
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

    @Bean
    public AudioSpeechMapper audioSpeechMapper() {return new AudioSpeechMapper();}

    @Bean
    public TextToSpeechUseCase textToSpeechUseCase(
            TextToSpeechService textToSpeechService,
            AudioSpeechMapper audioSpeechMapper
    ) {
        return new TextToSpeechUseCase(textToSpeechService, audioSpeechMapper);
    }


}