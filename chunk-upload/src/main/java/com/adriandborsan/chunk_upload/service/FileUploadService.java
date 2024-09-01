package com.adriandborsan.chunk_upload.service;


import com.adriandborsan.chunk_upload.dto.InitUploadRequest;
import com.adriandborsan.chunk_upload.dto.InitUploadResponse;
import com.adriandborsan.chunk_upload.dto.UploadChunkRequest;
import com.adriandborsan.chunk_upload.dto.UploadChunkResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Base64;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class FileUploadService {

    private final ConcurrentHashMap<String, UploadSession> uploadSessions = new ConcurrentHashMap<>();
    @Value("${upload-dir}")
    private String uploadDir;
    @Value("${temp-dir}")
    private String tempDir;

    public InitUploadResponse initiateUpload(InitUploadRequest request) {
        String uploadId = UUID.randomUUID().toString();
        Path tempFilePath = Paths.get(tempDir + uploadId);
        uploadSessions.put(uploadId, new UploadSession(request.getFileName(), request.getTotalChunks(), tempFilePath));
        log.info("Initiated upload with ID: {}", uploadId);

        InitUploadResponse response = new InitUploadResponse();
        response.setUploadId(uploadId);
        response.setMessage("Upload initiated successfully.");
        return response;
    }

    public UploadChunkResponse uploadChunk(UploadChunkRequest request) {
        boolean isCompleted = false;
        UploadSession session = uploadSessions.get(request.getUploadId());
        if (session == null) {
            throw new IllegalArgumentException("Invalid uploadId.");
        }

        try {
            byte[] decodedBytes = Base64.getDecoder().decode(request.getChunkData());
            Path chunkFilePath = Paths.get(session.getTempFilePath() + "." + request.getChunkNumber());
            Files.write(chunkFilePath, decodedBytes);

            log.info("Received chunk {} for uploadId {}", request.getChunkNumber(), request.getUploadId());
            log.info("request=[{}]", request);

            if (Boolean.TRUE.equals(request.getIsFinalChunk())) {
                assembleFile(session);
                uploadSessions.remove(request.getUploadId());
                log.info("Upload completed for uploadId {}", request.getUploadId());
                isCompleted = true;
            }

        } catch (IOException e) {
            log.error("Error while uploading chunk: {}", e.getMessage());
            throw new UncheckedIOException("Failed to upload chunk.", e);
        }
        UploadChunkResponse response = new UploadChunkResponse();
        response.setUploadId(request.getUploadId());
        response.setChunkNumber(request.getChunkNumber());
        response.setUploadCompleted(isCompleted);
        response.setMessage("Chunk " + request.getChunkNumber() + " uploaded successfully.");
        return response;
    }

    private void assembleFile(UploadSession session) throws IOException {
        Path finalFilePath = Paths.get(uploadDir + session.getFileName());
        try (OutputStream outputStream = new BufferedOutputStream(Files.newOutputStream(finalFilePath, StandardOpenOption.CREATE))) {
            for (int i = 1; i <= session.getTotalChunks(); i++) {
                Path chunkFilePath = Paths.get(session.getTempFilePath() + "." + i);
                byte[] chunkData = Files.readAllBytes(chunkFilePath);
                outputStream.write(chunkData);
                Files.delete(chunkFilePath); // Clean up chunk file after merging
            }
        }
        log.info("File assembled successfully at {}", finalFilePath);
    }

}
