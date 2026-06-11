package org.naho.config.application;

import org.naho.furigana.mapper.FuriganaResultMapper;
import org.naho.furigana.port.in.AnalyzeFuriganaInputPort;
import org.naho.furigana.port.out.FuriganaAnalysisPort;
import org.naho.furigana.usecase.AnalyzeFuriganaUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FuriganaConfig {

    @Bean
    public FuriganaResultMapper furiganaResultMapper() {
        return new FuriganaResultMapper();
    }

    @Bean
    public AnalyzeFuriganaInputPort analyzeFuriganaInputPort(
            FuriganaAnalysisPort furiganaAnalysisPort,
            FuriganaResultMapper furiganaResultMapper) {
        return new AnalyzeFuriganaUseCase(furiganaAnalysisPort, furiganaResultMapper);
    }
}
