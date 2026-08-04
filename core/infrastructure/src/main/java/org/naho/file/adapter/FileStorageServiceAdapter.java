package org.naho.file.adapter;

import lombok.RequiredArgsConstructor;
import org.naho.file.constant.S3Metadata;
import org.naho.file.constant.S3Properties;
import org.naho.file.constant.StaticResourceProperties;
import org.naho.file.exception.FileErrorCode;
import org.naho.file.model.File;
import org.naho.file.port.out.FileHelperPort;
import org.naho.file.port.out.FileRepositoryPort;
import org.naho.file.port.out.FileResultMapperPort;
import org.naho.file.port.out.FileStorageServicePort;
import org.naho.file.repository.FileJpaRepository;
import org.naho.file.result.DownloadedFile;
import org.naho.file.result.StoredFile;
import org.naho.i18n.message.file.FileDetailMessageKey;
import org.naho.shared.exception.InfrastructureException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

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
    private final S3Presigner s3Presigner;
    private final S3Properties s3Properties;
    private final StaticResourceProperties staticResourceProperties;
    private final FileHelperPort fileHelperPort;
    private final FileRepositoryPort fileRepositoryPort;
    private final FileResultMapperPort fileResultMapperPort;
    private final FileJpaRepository fileJpaRepository;

    @Override
    public void uploadFileToCloud(StoredFile storedFile) throws S3Exception {
        String absolutePath = staticResourceProperties.getLocalPath() + storedFile.objectKey();

        Path path = Path.of(absolutePath);
        if (!Files.exists(path)) {
            throw new InfrastructureException(
                    FileErrorCode.FILE_NOT_FOUND,
                    FileDetailMessageKey.FILE_NOT_FOUND,
                    absolutePath
            );
        }

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(storedFile.bucketName())
                .key(storedFile.objectKey())
                .contentType(storedFile.contentType())
                .metadata(Map.of(
                        S3Metadata.ORIGINAL_FILE_NAME, storedFile.originalFileName(),
                        S3Metadata.CHECKSUM, storedFile.checksum()))
                .build();

        s3Client.putObject(putObjectRequest, RequestBody.fromFile(path));
    }

//    @Override
//    public void deleteFileInCloud(String objectKey, String bucketName) {
//        try {
//            s3Client.deleteObject(request -> request
//                    .bucket(bucketName)
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
    public StoredFile saveFileToLocal(Object file, boolean isPublic) {
        if (!(file instanceof MultipartFile multipartFile)) {
            throw new InfrastructureException(
                    FileErrorCode.FILE_NOT_VALID,
                    FileDetailMessageKey.FILE_NOT_VALID);
        }

        try {
            String originalFileName = multipartFile.getOriginalFilename();

            String fileName = UUID.randomUUID() + "_" + Instant.now().toEpochMilli() +
                    fileHelperPort.getExtension(originalFileName);

            Path storageDirectory = Paths.get(
                    staticResourceProperties.getLocalPath(),
                    staticResourceProperties.getRecordings()
            );

            Files.createDirectories(storageDirectory);

            Path destination = storageDirectory.resolve(fileName);

            multipartFile.transferTo(destination);

            String checksum = fileHelperPort.calculateChecksum(destination);

            String objectKey = staticResourceProperties.getRecordings() + "/" + fileName;

            String bucketName = isPublic ?
                    s3Properties.getPublicBucketName() :
                    s3Properties.getPrivateBucketName();

            return StoredFile.builder()
                    .objectKey(objectKey)
                    .bucketName(bucketName)
                    .originalFileName(originalFileName)
                    .contentType(multipartFile.getContentType())
                    .size(multipartFile.getSize())
                    .checksum(checksum)
                    .build();

        } catch (IOException e) {
            throw new InfrastructureException(
                    FileErrorCode.FILE_UPLOAD_FAILED,
                    FileDetailMessageKey.FILE_UPLOAD_FAILED,
                    e.getMessage());
        }
    }

    @Override
    public void deleteFileInLocal(String objectKey) {
        if (objectKey == null || objectKey.isEmpty()) {
            throw new InfrastructureException(
                    FileErrorCode.FILE_NOT_VALID,
                    FileDetailMessageKey.FILE_LOCAL_STORAGE_PATH_EMPTY
            );
        }

        String absolutePath = staticResourceProperties.getLocalPath() + objectKey;

        Path path = Path.of(absolutePath);
        try {
            Files.deleteIfExists(path);
        } catch (IOException e) {
            throw new InfrastructureException(
                    FileErrorCode.FILE_DELETE_FAILED,
                    FileDetailMessageKey.FILE_DELETE_FAILED,
                    e.getMessage());
        }
    }

    @Override
    public String generatePresignedUrl(File file) {
        try {
            GetObjectPresignRequest getObjectPresignRequest = GetObjectPresignRequest.builder()
                    .signatureDuration(s3Properties.getSignatureDuration())
                    .getObjectRequest(request -> request
                            .bucket(file.getBucketName())
                            .key(file.getObjectKey()))
                    .build();

            return s3Presigner.presignGetObject(getObjectPresignRequest).url().toString();

        } catch (Exception e) {
            throw new InfrastructureException(
                    FileErrorCode.FILE_GENERATE_PRESIGNED_URL_FAILED,
                    FileDetailMessageKey.FILE_GENERATE_PRESIGNED_URL_FAILED,
                    e.getMessage()
            );
        }
    }

    @Override
    public DownloadedFile downloadFileFromCloud(File file) {
        try {
            GetObjectRequest request = GetObjectRequest.builder()
                    .bucket(file.getBucketName())
                    .key(file.getObjectKey())
                    .build();

            ResponseInputStream<GetObjectResponse> stream = s3Client.getObject(request);

            return DownloadedFile.builder()
                    .fileName(file.getOriginalFileName())
                    .contentType(stream.response().contentType())
                    .contentLength(stream.response().contentLength())
                    .inputStream(stream)
                    .build();
        } catch (Exception e) {
            throw new InfrastructureException();
        }
    }
}
