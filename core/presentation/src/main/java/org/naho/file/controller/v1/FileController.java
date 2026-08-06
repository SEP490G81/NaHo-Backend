package org.naho.file.controller.v1;

import lombok.RequiredArgsConstructor;
import org.naho.file.dto.mapper.FileRequestMapper;
import org.naho.file.dto.mapper.FileResponseMapper;
import org.naho.file.dto.response.FileResponse;
import org.naho.file.port.in.DownloadFileInputPort;
import org.naho.file.port.out.FileStorageServicePort;
import org.naho.file.result.DownloadedFile;
import org.naho.i18n.message.file.FileDetailMessageKey;
import org.naho.shared.annotation.ApiResponseMessage;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/api/v1/files")
@RequiredArgsConstructor
public class FileController {
    private final FileRequestMapper fileRequestMapper;
    private final FileResponseMapper fileResponseMapper;
    private final FileStorageServicePort fileStorageServicePort;
    private final DownloadFileInputPort downloadFileInputPort;

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

    @ApiResponseMessage(message = FileDetailMessageKey.FILE_DOWNLOAD_SUCCESSFULLY)
    @GetMapping("/download")
    public ResponseEntity<InputStreamResource> downloadFileFromCloud(@RequestParam("object_key") String objectKey) {
        DownloadedFile downloadedFile = downloadFileInputPort.downloadFileFromCloud(objectKey);
        return ResponseEntity.ok()
                .contentLength(downloadedFile.contentLength())
                .contentType(MediaType.parseMediaType(downloadedFile.contentType()))
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition
                                .attachment()
                                .filename(downloadedFile.fileName(), StandardCharsets.UTF_8)
                                .build()
                                .toString()
                )
                .body(new InputStreamResource(downloadedFile.inputStream()));
    }
}
