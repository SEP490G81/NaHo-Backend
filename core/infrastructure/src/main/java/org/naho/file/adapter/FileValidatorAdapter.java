package org.naho.file.adapter;

import lombok.RequiredArgsConstructor;
import org.apache.tika.Tika;
import org.naho.file.constant.FileContentType;
import org.naho.file.exception.FileErrorCode;
import org.naho.file.port.out.FileValidatorPort;
import org.naho.i18n.message.file.FileDetailMessageKey;
import org.naho.shared.exception.InfrastructureException;
import org.springframework.stereotype.Component;

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
}
