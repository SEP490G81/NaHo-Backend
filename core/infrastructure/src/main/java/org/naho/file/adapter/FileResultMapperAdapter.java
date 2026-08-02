package org.naho.file.adapter;

import lombok.RequiredArgsConstructor;
import org.naho.file.constant.CloudFrontProperties;
import org.naho.file.constant.S3Properties;
import org.naho.file.model.File;
import org.naho.file.port.in.CrudFileOperationInputPort;
import org.naho.file.port.out.FileResultMapperPort;
import org.naho.file.result.FileOperationResult;
import org.naho.file.result.FileResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FileResultMapperAdapter implements FileResultMapperPort {

    private final CrudFileOperationInputPort crudFileOperationInputPort;
    private final CloudFrontProperties cloudFrontProperties;
    private final S3Properties s3Properties;

    @Value("${app.nextjs-server-files-api}")
    private String nextjsServerApi;

    @Override
    public FileResult domainToResult(File domain) {
        if (domain == null) {
            return null;
        }

        FileOperationResult fileOperation =
                crudFileOperationInputPort.findByFileId(domain.getId());

        String accessUrl;
        if (s3Properties.getPublicBucketName().equals(domain.getBucketName())) {
            accessUrl = cloudFrontProperties.getDomain() + domain.getObjectKey();
        } else {
            accessUrl = nextjsServerApi + domain.getId();
        }

        return FileResult.builder()
                .id(domain.getId())
                .accessUrl(accessUrl)
                .originalFileName(domain.getOriginalFileName())
                .contentType(domain.getContentType())
                .size(domain.getSize())
                .fileOperation(fileOperation)
                .build();
    }
}
