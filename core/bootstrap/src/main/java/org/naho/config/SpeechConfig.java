package org.naho.config;

import org.naho.speech.azure.mapper.PronunciationAssessmentMapper;
import org.naho.speech.azure.mapper.WordAssessmentMapper;
import org.naho.speech.azure.port.out.AzureSpeechService;
import org.naho.speech.azure.usecase.AssessSpeechUseCase;
import org.naho.speech.mapper.AudioSpeechMapper;
import org.naho.speech.port.out.TextToSpeechService;
import org.naho.speech.usecase.TextToSpeechUseCase;
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
    public AudioSpeechMapper audioSpeechMapper() {return new AudioSpeechMapper();}

    @Bean
    public TextToSpeechUseCase textToSpeechUseCase(
            TextToSpeechService textToSpeechService,
            AudioSpeechMapper audioSpeechMapper
    ) {
        return new TextToSpeechUseCase(textToSpeechService, audioSpeechMapper);
    }


}