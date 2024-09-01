package com.adriandborsan.chunk_upload.service;

import lombok.Data;

import java.nio.file.Path;

@Data
class UploadSession {
    private final String fileName;
    private final int totalChunks;
    private final Path tempFilePath;
}
