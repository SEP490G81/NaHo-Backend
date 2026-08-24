package org.naho.file.controller.v1;

import lombok.RequiredArgsConstructor;
import org.naho.file.port.in.DownloadFileInputPort;
import org.naho.file.result.DownloadedFile;
import org.naho.i18n.message.file.FileDetailMessageKey;
import org.naho.shared.annotation.ApiResponseMessage;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/api/v1/files")
@RequiredArgsConstructor
public class FileController {
    private final DownloadFileInputPort downloadFileInputPort;

    // ROLE: ADMIN, CONTENT_MANAGER, LEARNER
    @PreAuthorize("hasAnyRole('ADMIN', 'CONTENT_MANAGER', 'LEARNER')")
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
