package org.naho.file.adapter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFmpegExecutor;
import net.bramp.ffmpeg.builder.FFmpegBuilder;
import org.naho.file.constant.FileFolderConstant;
import org.naho.file.constant.StaticResourceProperties;
import org.naho.file.exception.FileErrorCode;
import org.naho.file.port.out.FileAudioConvertPort;
import org.naho.i18n.message.file.FileDetailMessageKey;
import org.naho.shared.exception.InfrastructureException;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Slf4j
@Component
@RequiredArgsConstructor
public class FileAudioConvertAdapter implements FileAudioConvertPort {
    private static final int SAMPLE_RATE = 16_000;
    private static final int CHANNELS = 1;
    private static final int BITS_PER_SAMPLE = 16;

    private final FFmpeg ffmpeg;
    private final StaticResourceProperties staticResourceProperties;

    /**
     * Convert audio file to wav format
     *
     * @param audioBytes audio file bytes
     * @return wav file bytes
     */
    @Override
    public byte[] convertToWav(byte[] audioBytes) {
        if (audioBytes == null || audioBytes.length == 0) {
            throw new InfrastructureException(
                    FileErrorCode.FILE_NOT_VALID,
                    FileDetailMessageKey.FILE_EMPTY
            );
        }

        Path inputFile = null;
        Path outputFile = null;

        try {
            Path tempDirectory = Path.of(
                    staticResourceProperties.getLocalPath(),
                    FileFolderConstant.TEMP
            );

            Files.createDirectories(tempDirectory);
            inputFile = Files.createTempFile(
                    tempDirectory,
                    "audio-input-",
                    ".wav"
            );

            outputFile = Files.createTempFile(
                    tempDirectory,
                    "audio-output-",
                    ".wav"
            );

            Files.write(inputFile, audioBytes);

            FFmpegBuilder builder = new FFmpegBuilder()
                    .setInput(inputFile.toString())
                    .done().overrideOutputFiles(true)
                    .addOutput(outputFile.toString())
                    .setFormat("wav")
                    .setAudioCodec("pcm_s16le")
                    .setAudioChannels(CHANNELS)
                    .setAudioSampleRate(SAMPLE_RATE)
                    .done();

            new FFmpegExecutor(ffmpeg).createJob(builder).run();

            return Files.readAllBytes(outputFile);

        } catch (IOException e) {
            throw new InfrastructureException(
                    FileErrorCode.FILE_NOT_VALID,
                    FileDetailMessageKey.FILE_NOT_VALID,
                    e.getMessage()
            );
        } finally {
            deleteTempFile(inputFile);
            deleteTempFile(outputFile);
        }
    }

    private void deleteTempFile(Path path) {
        if (path == null) {
            return;
        }

        try {
            Files.deleteIfExists(path);
        } catch (IOException e) {
            log.warn("Failed to delete temp file: {}", path, e);
        }
    }
}
