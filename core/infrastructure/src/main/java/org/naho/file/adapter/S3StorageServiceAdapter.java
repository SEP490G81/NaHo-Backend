package org.naho.file.adapter;

import lombok.RequiredArgsConstructor;
import org.naho.file.constant.S3Properties;
import org.naho.file.exception.FileErrorCode;
import org.naho.file.port.out.FileStorageServicePort;
import org.naho.i18n.message.file.FileDetailMessageKey;
import org.naho.shared.exception.InfrastructureException;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.io.InputStream;

@Service
@RequiredArgsConstructor
public class S3StorageServiceAdapter implements FileStorageServicePort {
    private final S3Client s3Client;
    private final S3Properties s3Properties;

    @Override
    public void uploadFile(String objectKey,
                           InputStream inputStream,
                           String contentType,
                           Long size) {
        try {
            s3Client.putObject(
                    request -> request.bucket(s3Properties.getBucketName())
                            .key(objectKey)
                            .contentType(contentType),
                    RequestBody.fromInputStream(inputStream, size)
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
    public void deleteFileByObjectKey(String objectKey) {
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
}
