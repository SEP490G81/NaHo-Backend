package org.naho.config.ffmpeg;

import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFprobe;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
public class FFmpegConfig {
    private static final String FFPROBE_PATH = "ffprobe";
    private static final String FFMPEG_PATH = "ffmpeg";

    @Bean
    public FFprobe ffprobe() throws IOException {
        return new FFprobe(FFPROBE_PATH);
    }

    @Bean
    public FFmpeg ffmpeg() throws IOException {
        return new FFmpeg(FFMPEG_PATH);
    }
}
