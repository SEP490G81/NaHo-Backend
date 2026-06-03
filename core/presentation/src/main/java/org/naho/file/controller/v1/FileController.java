package org.naho.file.controller.v1;

import lombok.RequiredArgsConstructor;
import org.naho.file.command.FileUploadCommand;
import org.naho.file.constant.FileApplicationMessageKey;
import org.naho.file.dto.mapper.FileRequestMapper;
import org.naho.file.dto.mapper.FileResponseMapper;
import org.naho.file.dto.response.FileResponse;
import org.naho.file.port.in.FileStorageInputPort;
import org.naho.file.result.FileResult;
import org.naho.shared.annotation.ApiResponseMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/files")
@RequiredArgsConstructor
public class FileController {
    private final FileStorageInputPort fileStorageInputPort;
    private final FileRequestMapper fileRequestMapper;
    private final FileResponseMapper fileResponseMapper;

    @ApiResponseMessage(message = FileApplicationMessageKey.FILE_UPLOAD_SUCCESSFULLY)
    @PostMapping
    public ResponseEntity<FileResponse> uploadFile(
            @RequestPart("file") MultipartFile file
    ) {
        FileUploadCommand command = fileRequestMapper.multipartFileToCommand(file);
        FileResult result = fileStorageInputPort.upload(command);
        FileResponse response = fileResponseMapper.resultToResponse(result);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
