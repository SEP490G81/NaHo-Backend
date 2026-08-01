package org.naho.file.adapter;

import lombok.RequiredArgsConstructor;
import org.naho.file.command.UploadFileCommand;
import org.naho.file.constant.S3Properties;
import org.naho.file.constant.StaticResourceProperties;
import org.naho.file.exception.FileErrorCode;
import org.naho.file.model.StoredFile;
import org.naho.file.port.out.FileHelperPort;
import org.naho.file.port.out.FileStorageServicePort;
import org.naho.i18n.message.file.FileDetailMessageKey;
import org.naho.shared.exception.InfrastructureException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileStorageServiceAdapter implements FileStorageServicePort {

    private final S3Client s3Client;
    private final S3Properties s3Properties;
    private final StaticResourceProperties staticResourceProperties;

    private final FileHelperPort fileHelperPort;

    @Override
    public void uploadFileToCloud(UploadFileCommand command) {
        try {
            s3Client.putObject(request ->
                            request.bucket(s3Properties.getBucketName())
                                    .key(command.getObjectKey())
                                    .contentType(command.getContentType()),
                    RequestBody.fromInputStream(command.getInputStream(), command.getSize())
            );
        } catch (S3Exception e) {
            throw new InfrastructureException(
                    FileErrorCode.FILE_UPLOAD_FAILED,
                    FileDetailMessageKey.FILE_UPLOAD_FAILED,
                    e.getMessage()
            );
        }
    }

    @Override
    public void deleteFileInCloud(String objectKey) {
        try {
            s3Client.deleteObject(request -> request
                    .bucket(s3Properties.getBucketName())
                    .key(objectKey)
            );
        } catch (S3Exception e) {
            throw new InfrastructureException(
                    FileErrorCode.FILE_DELETE_FAILED,
                    FileDetailMessageKey.FILE_DELETE_FAILED,
                    e.getMessage()
            );
        }
    }

    @Override
    public StoredFile saveFileToLocal(Object file) {
        if (!(file instanceof MultipartFile multipartFile)) {
            throw new InfrastructureException(
                    FileErrorCode.FILE_NOT_VALID,
                    FileDetailMessageKey.FILE_NOT_VALID
            );
        }

        try {
            String originalFileName = multipartFile.getOriginalFilename();

            String fileName = UUID.randomUUID() + "_" + Instant.now().toEpochMilli() +
                    fileHelperPort.getExtension(originalFileName);

            Path storageDirectory = Paths.get(staticResourceProperties.getBaseLocation()
                    + staticResourceProperties.getRecordings());

            Files.createDirectories(storageDirectory);

            Path destination = storageDirectory.resolve(fileName);

            multipartFile.transferTo(destination);

            String checksum = fileHelperPort.calculateChecksum(destination);

            String objectKey = staticResourceProperties.getRecordings() + "/" + fileName;

            return StoredFile.builder()
                    .objectKey(objectKey)
                    .localStoragePath(destination.toString())
                    .originalFileName(originalFileName)
                    .contentType(multipartFile.getContentType())
                    .size(multipartFile.getSize())
                    .checksum(checksum)
                    .build();

        } catch (IOException e) {
            throw new InfrastructureException(
                    FileErrorCode.FILE_UPLOAD_FAILED,
                    FileDetailMessageKey.FILE_UPLOAD_FAILED,
                    e.getMessage()
            );
        }
    }
}
