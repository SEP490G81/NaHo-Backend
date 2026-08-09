package org.naho.file.adapter;

import lombok.RequiredArgsConstructor;
import org.naho.file.constant.CloudFrontProperties;
import org.naho.file.constant.S3Properties;
import org.naho.file.model.File;
import org.naho.file.port.out.FileResultMapperPort;
import org.naho.file.port.out.FileStorageServicePort;
import org.naho.file.result.FileResult;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FileResultMapperAdapter implements FileResultMapperPort {

    private final CloudFrontProperties cloudFrontProperties;
    private final S3Properties s3Properties;
    private final FileStorageServicePort fileStorageServicePort;

    @Override
    public FileResult domainToResult(File domain) {
        if (domain == null) {
            return null;
        }

        String accessUrl;
        if (s3Properties.getPublicBucketName().equals(domain.getBucketName())) {
            // nếu là ở public bucket thì lấy đường dẫn cloud front
            accessUrl = cloudFrontProperties.getDomain() + domain.getObjectKey();
        } else {
            // nếu ở bucket private thì lấy presigned url
            accessUrl = fileStorageServicePort.generatePresignedUrl(domain);
        }

        return FileResult.builder()
                .id(domain.getId())
                .objectKey(domain.getObjectKey())
                .accessUrl(accessUrl)
                .originalFileName(domain.getOriginalFileName())
                .contentType(domain.getContentType())
                .size(domain.getSize())
                .checksum(domain.getChecksum())
                .operationType(domain.getOperationType())
                .operationStatus(domain.getOperationStatus())
                .retryCount(domain.getRetryCount())
                .nextRetryAt(domain.getNextRetryAt() != null ? domain.getNextRetryAt().getValue() : null)
                .build();
    }
}
