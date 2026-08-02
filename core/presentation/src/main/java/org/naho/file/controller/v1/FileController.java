package org.naho.file.controller.v1;

import lombok.RequiredArgsConstructor;
import org.naho.file.dto.mapper.FileRequestMapper;
import org.naho.file.dto.mapper.FileResponseMapper;
import org.naho.file.dto.response.FileResponse;
import org.naho.file.port.out.FileStorageServicePort;
import org.naho.i18n.message.file.FileDetailMessageKey;
import org.naho.shared.annotation.ApiResponseMessage;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/files")
@RequiredArgsConstructor
public class FileController {
    private final FileRequestMapper fileRequestMapper;
    private final FileResponseMapper fileResponseMapper;
    private final FileStorageServicePort fileStorageServicePort;

    @ApiResponseMessage(message = FileDetailMessageKey.FILE_UPLOAD_SUCCESSFULLY)
    @PostMapping
    public ResponseEntity<FileResponse> uploadFile(
            @RequestPart("file") MultipartFile file,
            @RequestPart("folder-name") String folderName
    ) {
        return null;
    }

    @ApiResponseMessage(message = FileDetailMessageKey.FILE_DELETE_SUCCESSFULLY)
    @DeleteMapping
    public ResponseEntity<Void> deleteFileInCloud(@RequestParam("objectKey") String objectKey) {
        return ResponseEntity.ok().build();
    }
}
