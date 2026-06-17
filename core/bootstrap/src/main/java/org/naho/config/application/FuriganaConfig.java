package org.naho.config.application;

import org.naho.furigana.mapper.FuriganaResultMapper;
import org.naho.furigana.port.in.GenerateFuriganaInputPort;
import org.naho.furigana.port.out.FuriganaGenerationPort;
import org.naho.furigana.usecase.GenerateFuriganaUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FuriganaConfig {

    @Bean
    public FuriganaResultMapper furiganaResultMapper() {
        return new FuriganaResultMapper();
    }

    @Bean
    public GenerateFuriganaInputPort generateFuriganaInputPort(
            FuriganaGenerationPort furiganaGenerationPort,
            FuriganaResultMapper furiganaResultMapper) {
        return new GenerateFuriganaUseCase(furiganaGenerationPort, furiganaResultMapper);
    }
}
