package org.naho.file.dto.mapper;

import org.mapstruct.Mapper;
import org.naho.file.command.FileUploadCommand;
import org.naho.file.constant.FileApplicationMessageKey;
import org.naho.file.dto.request.FileUploadRequest;
import org.naho.file.exception.FileApplicationErrorCode;
import org.naho.shared.exception.PresentationException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Mapper(componentModel = "spring")
public interface FileRequestMapper {
    FileUploadCommand requestToCommand(FileUploadRequest request);

    default FileUploadCommand multipartFileToCommand(MultipartFile file) {
        if (file == null || file.isEmpty()) return null;
        try {
            return new FileUploadCommand(
                    file.getOriginalFilename(),
                    file.getInputStream(),
                    file.getContentType(),
                    file.getSize()
            );
        } catch (IOException e) {
            throw new PresentationException(
                    FileApplicationErrorCode.FILE_UPLOAD_FAILED,
                    FileApplicationMessageKey.FILE_UPLOAD_FAILED,
                    e.getMessage()
            );
        }
    }
}
