package org.naho.file.adapter;

import lombok.RequiredArgsConstructor;
import org.naho.file.command.FileUploadCommand;
import org.naho.file.constant.FileApplicationMessageKey;
import org.naho.file.constant.S3Properties;
import org.naho.file.exception.FileApplicationErrorCode;
import org.naho.file.port.out.FileStorageServicePort;
import org.naho.shared.exception.InfrastructureException;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.S3Exception;

@Service
@RequiredArgsConstructor
public class S3StorageServiceAdapter implements FileStorageServicePort {
    private final S3Client s3Client;
    private final S3Properties s3Properties;

    @Override
    public String upload(FileUploadCommand command) {
        try {
            s3Client.putObject(
                    request -> request.bucket(s3Properties.getBucketName())
                            .key(command.getOriginalName())
                            .contentType(command.getContentType()),
                    RequestBody.fromInputStream(command.getInputStream(), command.getSize())
            );
            return "";
        } catch (S3Exception e) {
            throw new InfrastructureException(
                    FileApplicationErrorCode.FILE_UPLOAD_FAILED,
                    FileApplicationMessageKey.FILE_UPLOAD_FAILED,
                    e.getMessage()
            );
        }
    }
}
