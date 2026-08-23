package org.naho.file.adapter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.bramp.ffmpeg.FFprobe;
import net.bramp.ffmpeg.probe.FFmpegProbeResult;
import org.apache.tika.Tika;
import org.naho.file.constant.FileContentType;
import org.naho.file.constant.FileFolderConstant;
import org.naho.file.constant.StaticResourceProperties;
import org.naho.file.exception.FileErrorCode;
import org.naho.file.port.out.FileValidatorPort;
import org.naho.i18n.message.file.FileDetailMessageKey;
import org.naho.shared.exception.InfrastructureException;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;


@Slf4j
@Component
@RequiredArgsConstructor
public class FileValidatorAdapter implements FileValidatorPort {
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

    private static final Set<String> ALLOWED_AUDIO_MIME_TYPES = Set.of(
            FileContentType.AUDIO_MPEG,
            FileContentType.AUDIO_WAV,
            FileContentType.AUDIO_X_WAV,
            FileContentType.AUDIO_VND_WAVE,
            FileContentType.AUDIO_MP4,
            FileContentType.AUDIO_X_M4A,
            FileContentType.AUDIO_OGG,
            FileContentType.AUDIO_WEBM,
            FileContentType.AUDIO_FLAC
    );

    private final Tika tika;
    private final FFprobe ffprobe;
    private final StaticResourceProperties staticResourceProperties;

    @Override
    public void validateImageFile(byte[] fileBytes) {
        String detectedMimeType = tika.detect(fileBytes);

        if (!ALLOWED_IMAGE_MIME_TYPES.contains(detectedMimeType)) {
            throw new InfrastructureException(
                    FileErrorCode.FILE_NOT_VALID,
                    FileDetailMessageKey.FILE_NOT_VALID,
                    detectedMimeType);
        }
    }

    @Override
    public double validateWavFileAndDuration(byte[] audioBytes, Double maxDuration) {
        if (audioBytes == null || audioBytes.length == 0) {
            throw new InfrastructureException(
                    FileErrorCode.FILE_NOT_VALID,
                    FileDetailMessageKey.FILE_EMPTY);
        }

        Path tempFile = null;

        try {
            String detectedMimeType = tika.detect(audioBytes);

            if (!ALLOWED_WAV_MIME_TYPES.contains(detectedMimeType)) {
                throw new InfrastructureException(
                        FileErrorCode.FILE_NOT_VALID,
                        FileDetailMessageKey.FILE_NOT_VALID,
                        detectedMimeType);
            }

            Path tempDirectory = Path.of(
                    staticResourceProperties.getLocalPath(),
                    FileFolderConstant.TEMP);

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
                        maxDuration);
            }

            return duration;

        } catch (IOException e) {
            throw new InfrastructureException(
                    FileErrorCode.FILE_NOT_VALID,
                    FileDetailMessageKey.FILE_NOT_VALID,
                    e.getMessage());
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

    @Override
    public double validateAudioFileAndDuration(byte[] audioBytes, Double maxDuration) {
        if (audioBytes == null || audioBytes.length == 0) {
            throw new InfrastructureException(
                    FileErrorCode.FILE_NOT_VALID,
                    FileDetailMessageKey.FILE_EMPTY);
        }

        Path tempFile = null;

        try {
            String detectedMimeType = tika.detect(audioBytes);

            if (!ALLOWED_AUDIO_MIME_TYPES.contains(detectedMimeType)
                    && !FileContentType.APPLICATION_X_MATROSKA.equals(detectedMimeType)) {
                throw new InfrastructureException(
                        FileErrorCode.FILE_NOT_VALID,
                        FileDetailMessageKey.FILE_NOT_VALID,
                        detectedMimeType);
            }

            Path tempDirectory = Path.of(
                    staticResourceProperties.getLocalPath(),
                    FileFolderConstant.TEMP
            );

            Files.createDirectories(tempDirectory);

            tempFile = Files.createTempFile(tempDirectory, "audio-", getFileExtension(detectedMimeType));
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

            return duration;

        } catch (IOException e) {
            throw new InfrastructureException(
                    FileErrorCode.FILE_NOT_VALID,
                    FileDetailMessageKey.FILE_NOT_VALID,
                    e.getMessage());
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

    private String getFileExtension(String mimeType) {
        return switch (mimeType) {
            case FileContentType.AUDIO_MPEG -> ".mp3";
            case FileContentType.AUDIO_WAV, FileContentType.AUDIO_X_WAV, FileContentType.AUDIO_VND_WAVE -> ".wav";
            case FileContentType.AUDIO_MP4 -> ".mp4";
            case FileContentType.AUDIO_X_M4A -> ".m4a";
            case FileContentType.AUDIO_OGG -> ".ogg";
            case FileContentType.AUDIO_WEBM -> ".webm";
            case FileContentType.AUDIO_FLAC -> ".flac";
            default -> ".tmp";
        };
    }
}


