package org.naho.config.application;

import org.naho.file.mapper.FileResultMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FileConfig {
    @Bean
    public FileResultMapper fileResultMapper() {
        return new FileResultMapper();
    }
}
