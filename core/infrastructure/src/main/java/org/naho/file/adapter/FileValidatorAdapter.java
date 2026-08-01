package org.naho.file.adapter;

import lombok.RequiredArgsConstructor;
import org.apache.tika.Tika;
import org.naho.file.constant.FileContentType;
import org.naho.file.exception.FileErrorCode;
import org.naho.file.port.out.FileValidatorPort;
import org.naho.i18n.message.file.FileDetailMessageKey;
import org.naho.shared.exception.InfrastructureException;
import org.springframework.stereotype.Component;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class FileValidatorAdapter implements FileValidatorPort {
    private final Tika tika;

    @Override
    public String validateImageFile(InputStream inputStream) {
        Set<String> allowedMimeTypes = Set.of(
                FileContentType.IMAGE_PNG,
                FileContentType.IMAGE_JPEG,
                FileContentType.IMAGE_WEBP
        );
        try {
            String detectedMimeType = tika.detect(inputStream);
            if (!allowedMimeTypes.contains(detectedMimeType)) {
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
    public void validateWavFileAndDuration(InputStream inputStream, Double maxDuration) {
        try {
            AudioFileFormat fileFormat = AudioSystem.getAudioFileFormat(inputStream);

            if (fileFormat.getType() != AudioFileFormat.Type.WAVE) {
                throw new InfrastructureException(
                        FileErrorCode.FILE_NOT_VALID,
                        FileDetailMessageKey.FILE_NOT_VALID,
                        fileFormat.getType()
                );
            }

            AudioFormat format = fileFormat.getFormat();

            long frameLength = fileFormat.getFrameLength();

            if (frameLength == AudioSystem.NOT_SPECIFIED) {
                throw new InfrastructureException(
                        FileErrorCode.FILE_NOT_VALID,
                        FileDetailMessageKey.FILE_AUDIO_DURATION_UNSPECIFIED
                );
            }

            double durationSeconds = frameLength / format.getFrameRate();
            if (durationSeconds > maxDuration) {
                throw new InfrastructureException(
                        FileErrorCode.FILE_NOT_VALID,
                        FileDetailMessageKey.FILE_AUDIO_DURATION_EXCEEDED,
                        durationSeconds,
                        maxDuration
                );
            }

        } catch (UnsupportedAudioFileException | IOException e) {
            throw new InfrastructureException(
                    FileErrorCode.FILE_NOT_VALID,
                    FileDetailMessageKey.FILE_NOT_VALID,
                    e.getMessage()
            );
        }
    }
}
