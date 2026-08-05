package org.naho.file.adapter;

import lombok.RequiredArgsConstructor;
import org.naho.file.exception.FileErrorCode;
import org.naho.file.port.out.FileHelperPort;
import org.naho.i18n.message.file.FileDetailMessageKey;
import org.naho.shared.exception.InfrastructureException;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

@Component
@RequiredArgsConstructor
public class FileHelperAdapter implements FileHelperPort {
    private static final String DEFAULT_HASH_ALGORITHM = "SHA-256";

    @Override
    public String getExtension(String originalFileName) {
        if (originalFileName == null) {
            return "";
        }

        int lastDot = originalFileName.lastIndexOf('.');

        if (lastDot == -1) {
            return "";
        }

        return originalFileName.substring(lastDot);
    }

    @Override
    public String calculateChecksum(Path file) {
        try (InputStream inputStream = Files.newInputStream(file)) {

            MessageDigest digest = MessageDigest.getInstance(DEFAULT_HASH_ALGORITHM);

            byte[] buffer = new byte[8192];
            int read;

            while ((read = inputStream.read(buffer)) != -1) {
                digest.update(buffer, 0, read);
            }

            return HexFormat.of().formatHex(digest.digest());

        } catch (IOException | NoSuchAlgorithmException e) {
            throw new InfrastructureException(
                    FileErrorCode.FILE_NOT_VALID,
                    FileDetailMessageKey.FILE_NOT_VALID,
                    e.getMessage()
            );
        }
    }
}
