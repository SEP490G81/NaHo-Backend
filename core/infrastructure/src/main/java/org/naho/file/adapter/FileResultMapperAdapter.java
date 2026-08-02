package org.naho.file.adapter;

import lombok.RequiredArgsConstructor;
import org.naho.file.constant.CloudFrontProperties;
import org.naho.file.constant.StaticResourceProperties;
import org.naho.file.model.File;
import org.naho.file.port.out.FileResultMapperPort;
import org.naho.file.result.FileResult;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FileResultMapperAdapter implements FileResultMapperPort {

    private final CloudFrontProperties cloudFrontProperties;
    private final StaticResourceProperties staticResourceProperties;

    @Override
    public FileResult domainToResult(File domain) {
        if (domain == null) {
            return null;
        }
        return FileResult.builder()
                .id(domain.getId())
                .localStoragePath(staticResourceProperties.getLocalPath() + domain.getLocalStoragePath())
                .objectKey(cloudFrontProperties.getDomain() + domain.getObjectKey())
                .originalFileName(domain.getOriginalFileName())
                .contentType(domain.getContentType())
                .size(domain.getSize())
                .build();
    }
}
