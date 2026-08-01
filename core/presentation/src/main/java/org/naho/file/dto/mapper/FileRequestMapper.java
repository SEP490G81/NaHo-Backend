package org.naho.file.dto.mapper;

import org.mapstruct.Mapper;
import org.naho.file.command.UploadFileCommand;
import org.naho.file.exception.FileErrorCode;
import org.naho.shared.exception.PresentationException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Mapper(componentModel = "spring")
public interface FileRequestMapper {
    default UploadFileCommand multipartFileAndFolderNameToCommand(MultipartFile file, String folderName) {
        if (file == null || file.isEmpty()) return null;
        try {
            return UploadFileCommand.builder()
//                    .folderName(folderName)
//                    .originalName(file.getOriginalFilename())
                    .inputStream(file.getInputStream())
                    .contentType(file.getContentType())
                    .size(file.getSize())
                    .build();
        } catch (IOException e) {
            throw new PresentationException(
                    FileErrorCode.FILE_UPLOAD_FAILED,
                    org.naho.i18n.message.file.FileDetailMessageKey.FILE_UPLOAD_FAILED,
                    e.getMessage()
            );
        }
    }
}
