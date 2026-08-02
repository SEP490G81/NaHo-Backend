package org.naho.file.adapter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.bramp.ffmpeg.FFprobe;
import net.bramp.ffmpeg.probe.FFmpegProbeResult;
import org.apache.tika.Tika;
import org.naho.file.constant.FileContentType;
import org.naho.file.constant.StaticResourceProperties;
import org.naho.file.exception.FileErrorCode;
import org.naho.file.port.out.FileValidatorPort;
import org.naho.i18n.message.file.FileDetailMessageKey;
import org.naho.shared.exception.InfrastructureException;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class FileValidatorAdapter implements FileValidatorPort {
    private final Tika tika;
    private final FFprobe ffprobe;
    private final StaticResourceProperties staticResourceProperties;

    private static final Set<String> ALLOWED_IMAGE_MIME_TYPES = Set.of(
            FileContentType.IMAGE_PNG,
            FileContentType.IMAGE_JPEG,
            FileContentType.IMAGE_WEBP
    );

    private static final Set<String> ALLOWED_WAV_MIME_TYPES = Set.of(
            FileContentType.AUDIO_WAV,
            FileContentType.AUDIO_X_WAV,
            FileContentType.AUDIO_VND_WAVE
    );

    @Override
    public String validateImageFile(InputStream inputStream) {
        try {
            String detectedMimeType = tika.detect(inputStream);
            if (!ALLOWED_IMAGE_MIME_TYPES.contains(detectedMimeType)) {
                throw new InfrastructureException(
                        FileErrorCode.FILE_NOT_VALID,
                        FileDetailMessageKey.FILE_NOT_VALID,
                        detectedMimeType
                );
            }

            return detectedMimeType;
        } catch (IOException e) {
            throw new InfrastructureException(
                    FileErrorCode.FILE_NOT_VALID,
                    FileDetailMessageKey.FILE_NOT_VALID,
                    e.getMessage()
            );
        }
    }

    @Override
    public void validateWavFileAndDuration(byte[] audioBytes, Double maxDuration) {
        if (audioBytes == null || audioBytes.length == 0) {
            throw new InfrastructureException(
                    FileErrorCode.FILE_NOT_VALID,
                    FileDetailMessageKey.FILE_EMPTY
            );
        }

        Path tempFile = null;

        try {
            String detectedMimeType = tika.detect(audioBytes);

            if (!ALLOWED_WAV_MIME_TYPES.contains(detectedMimeType)) {
                throw new InfrastructureException(
                        FileErrorCode.FILE_NOT_VALID,
                        FileDetailMessageKey.FILE_NOT_VALID,
                        detectedMimeType
                );
            }

            Path tempDirectory = Path.of(
                    staticResourceProperties.getLocalPath(),
                    staticResourceProperties.getTemp()
            );

            Files.createDirectories(tempDirectory);

            tempFile = Files.createTempFile(tempDirectory, "audio-", ".wav");
            Files.write(tempFile, audioBytes);

            FFmpegProbeResult probeResult = ffprobe.probe(tempFile.toString());

            double duration = probeResult.getFormat().duration;

            if (maxDuration != null && duration > maxDuration) {
                throw new InfrastructureException(
                        FileErrorCode.FILE_NOT_VALID,
                        FileDetailMessageKey.FILE_AUDIO_DURATION_EXCEEDED,
                        duration,
                        maxDuration
                );
            }

        } catch (IOException e) {
            throw new InfrastructureException(
                    FileErrorCode.FILE_NOT_VALID,
                    FileDetailMessageKey.FILE_NOT_VALID,
                    e.getMessage()
            );
        } finally {
            if (tempFile != null) {
                try {
                    Files.deleteIfExists(tempFile);
                } catch (IOException e) {
                    // sẽ xóa qua cron job sau vì mục đích của method này
                    // chỉ là validate file
                    log.warn(e.getMessage());
                }
            }
        }
    }
}
