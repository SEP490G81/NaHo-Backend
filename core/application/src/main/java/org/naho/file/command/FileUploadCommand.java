package org.naho.file.command;

import java.io.InputStream;

public class FileUploadCommand {
    private String originalName;
    private InputStream inputStream;
    private String contentType;
    private Long size;

    public FileUploadCommand(String originalName, InputStream inputStream, String contentType, Long size) {
        this.originalName = originalName;
        this.inputStream = inputStream;
        this.contentType = contentType;
        this.size = size;
    }

    public String getOriginalName() {
        return originalName;
    }

    public void setOriginalName(String originalName) {
        this.originalName = originalName;
    }

    public InputStream getInputStream() {
        return inputStream;
    }


    public String getContentType() {
        return contentType;
    }

    public Long getSize() {
        return size;
    }
}
