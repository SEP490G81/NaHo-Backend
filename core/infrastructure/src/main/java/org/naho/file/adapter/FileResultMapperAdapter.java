package org.naho.file.adapter;

import lombok.RequiredArgsConstructor;
import org.naho.file.constant.CloudFrontProperties;
import org.naho.file.constant.StaticResourceProperties;
import org.naho.file.model.File;
import org.naho.file.port.in.CrudFileOperationInputPort;
import org.naho.file.port.out.FileResultMapperPort;
import org.naho.file.result.FileOperationResult;
import org.naho.file.result.FileResult;
import org.naho.file.type.OperationStatus;
import org.naho.file.type.OperationType;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FileResultMapperAdapter implements FileResultMapperPort {

    private final StaticResourceProperties staticResourceProperties;
    private final CloudFrontProperties cloudFrontProperties;
    private final CrudFileOperationInputPort crudFileOperationInputPort;

    @Override
    public FileResult domainToResult(File domain) {
        if (domain == null) {
            return null;
        }

        FileOperationResult uploadOperation = crudFileOperationInputPort
                .findByFileIdAndOperationType(domain.getId(), OperationType.UPLOAD);

        String accessUrl = uploadOperation.operationStatus().equals(OperationStatus.COMPLETED) ?
                cloudFrontProperties.getDomain() + domain.getObjectKey() :
                staticResourceProperties.getBackendFilesBaseUrl() + domain.getObjectKey();

        return FileResult.builder()
                .id(domain.getId())
                .accessUrl(accessUrl)
                .originalFileName(domain.getOriginalFileName())
                .contentType(domain.getContentType())
                .size(domain.getSize())
                .build();
    }
}
