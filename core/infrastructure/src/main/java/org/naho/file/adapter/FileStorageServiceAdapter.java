package org.naho.file.adapter;

import lombok.RequiredArgsConstructor;
import org.naho.file.constant.S3Metadata;
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
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileStorageServiceAdapter implements FileStorageServicePort {

    private final S3Client s3Client;
    private final S3Properties s3Properties;
    private final StaticResourceProperties staticResourceProperties;
    private final FileHelperPort fileHelperPort;

    @Override
    public void uploadFileToCloud(StoredFile file) throws S3Exception {
        Path path = Path.of(file.absoluteLocalStoragePath());
        if (!Files.exists(path)) {
            throw new InfrastructureException(
                    FileErrorCode.FILE_NOT_FOUND,
                    FileDetailMessageKey.FILE_NOT_FOUND,
                    file.absoluteLocalStoragePath()
            );
        }

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(s3Properties.getBucketName())
                .key(file.objectKey())
                .contentType(file.contentType())
                .metadata(Map.of(
                        S3Metadata.ORIGINAL_FILE_NAME, file.originalFileName(),
                        S3Metadata.CHECKSUM, file.checksum()
                ))
                .build();

        s3Client.putObject(putObjectRequest, RequestBody.fromFile(path));
    }

//    @Override
//    public void deleteFileInCloud(String objectKey) {
//        try {
//            s3Client.deleteObject(request -> request
//                    .bucket(s3Properties.getBucketName())
//                    .key(objectKey)
//            );
//        } catch (S3Exception e) {
//            throw new InfrastructureException(
//                    FileErrorCode.FILE_DELETE_FAILED,
//                    FileDetailMessageKey.FILE_DELETE_FAILED,
//                    e.getMessage()
//            );
//        }
//    }

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

            Path storageDirectory = Paths.get(staticResourceProperties.getLocalPath()
                    + staticResourceProperties.getRecordings());

            Files.createDirectories(storageDirectory);

            Path destination = storageDirectory.resolve(fileName);

            multipartFile.transferTo(destination);

            String checksum = fileHelperPort.calculateChecksum(destination);

            String relativePath = staticResourceProperties.getRecordings() + "/" + fileName;

            return StoredFile.builder()
                    .objectKey(relativePath)
                    .relativeLocalStoragePath(relativePath)
                    .absoluteLocalStoragePath(destination.toAbsolutePath().toString())
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

    @Override
    public void deleteFileInLocal(String localStoragePath) {
        if (localStoragePath == null || localStoragePath.isEmpty()) {
            throw new InfrastructureException(
                    FileErrorCode.FILE_NOT_VALID,
                    FileDetailMessageKey.FILE_LOCAL_STORAGE_PATH_EMPTY
            );
        }

        Path path = Path.of(localStoragePath);
        try {
            Files.deleteIfExists(path);
        } catch (IOException e) {
            throw new InfrastructureException(
                    FileErrorCode.FILE_DELETE_FAILED,
                    FileDetailMessageKey.FILE_DELETE_FAILED,
                    e.getMessage()
            );
        }
    }
}
