package org.naho.config.ffmpeg;

import net.bramp.ffmpeg.FFprobe;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
public class FFmpegConfig {
    private static final String FFPROBE_PATH = "ffprobe";

    @Bean
    public FFprobe ffprobe() throws IOException {
        return new FFprobe(FFPROBE_PATH);
    }
}
