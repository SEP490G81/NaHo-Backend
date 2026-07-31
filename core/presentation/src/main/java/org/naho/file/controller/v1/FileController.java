package org.naho.file.controller.v1;

import lombok.RequiredArgsConstructor;
import org.naho.file.command.UploadFileCommand;
import org.naho.file.dto.mapper.FileRequestMapper;
import org.naho.file.dto.mapper.FileResponseMapper;
import org.naho.file.dto.response.FileResponse;
import org.naho.file.port.in.FileStorageInputPort;
import org.naho.file.result.FileResult;
import org.naho.i18n.message.file.FileDetailMessageKey;
import org.naho.shared.annotation.ApiResponseMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/files")
@RequiredArgsConstructor
public class FileController {
    private final FileStorageInputPort fileStorageInputPort;
    private final FileRequestMapper fileRequestMapper;
    private final FileResponseMapper fileResponseMapper;

    @ApiResponseMessage(message = FileDetailMessageKey.FILE_UPLOAD_SUCCESSFULLY)
    @PostMapping
    public ResponseEntity<FileResponse> uploadFile(
            @RequestPart("file") MultipartFile file,
            @RequestPart("folder-name") String folderName
    ) {
        UploadFileCommand command =
                fileRequestMapper.multipartFileAndFolderNameToCommand(file, folderName);

        FileResult result = fileStorageInputPort.uploadFile(command);
        FileResponse response = fileResponseMapper.resultToResponse(result);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @ApiResponseMessage(message = FileDetailMessageKey.FILE_DELETE_SUCCESSFULLY)
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteFileById(@PathVariable("id") Long fileId) {
        return ResponseEntity.ok(fileStorageInputPort.deleteFileById(fileId));
    }
}
