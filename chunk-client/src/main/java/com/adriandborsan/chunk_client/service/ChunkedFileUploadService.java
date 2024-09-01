package com.adriandborsan.chunk_client.service;

import com.adriandborsan.chunk_client.dto.UploadChunkRequest;
import com.adriandborsan.chunk_client.dto.UploadChunkResponse;
import com.adriandborsan.chunk_client.dto.UploadInitiateRequest;
import com.adriandborsan.chunk_client.dto.UploadInitiateResponse;
import com.adriandborsan.chunk_client.dto.UploadRequest;
import com.adriandborsan.chunk_client.feign.ChunkedFileUploadFeignClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.Arrays;

@Service
@Slf4j
public class ChunkedFileUploadService {

    private final ChunkedFileUploadFeignClient feignClient;

    private final int chunkSize;

    public ChunkedFileUploadService(ChunkedFileUploadFeignClient feignClient, @Value("${max-file-size}") int maxFileSize) {
        this.feignClient = feignClient;
        this.chunkSize = maxFileSize * 1024 * 1024;
    }

    public String uploadFile(UploadRequest uploadRequest) {
        try {
            // Step 1: Initiate upload and get uploadId
            MultipartFile file = uploadRequest.getFile();

            UploadInitiateRequest uploadInitiateRequest = new UploadInitiateRequest();
            uploadInitiateRequest.setFileName(uploadRequest.getFileName());
            uploadInitiateRequest.setFileDescription(uploadRequest.getFileDescription());
            long fileSize = file.getSize();
            int totalChunks = (int) Math.ceil((double) fileSize / chunkSize);
            uploadInitiateRequest.setFileSize(fileSize);
            uploadInitiateRequest.setTotalChunks(totalChunks);
            uploadInitiateRequest.setFileType(file.getContentType());

            UploadInitiateResponse uploadInitiateResponse = feignClient.initiateUpload(uploadInitiateRequest);
            log.info("uploadInitiateResponse: {}", uploadInitiateResponse);
            String uploadId = uploadInitiateResponse.getUploadId();

            // Step 2: Upload chunks

            try (InputStream inputStream = new BufferedInputStream(file.getInputStream())) {
                byte[] buffer = new byte[chunkSize];
                int chunkNumber = 1;
                int bytesRead;

                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    boolean isFinalChunk = chunkNumber == totalChunks;

                    String base64ChunkData = java.util.Base64.getEncoder().encodeToString(Arrays.copyOf(buffer, bytesRead));

                    UploadChunkRequest uploadChunkRequest = new UploadChunkRequest();
                    uploadChunkRequest.setUploadId(uploadId);
                    uploadChunkRequest.setChunkNumber(chunkNumber);
                    uploadChunkRequest.setChunkData(base64ChunkData);
                    uploadChunkRequest.setIsFinalChunk(isFinalChunk);


                    UploadChunkResponse uploadChunkResponse = feignClient.uploadChunk(uploadChunkRequest);
                    log.info("uploadChunkResponse: {}", uploadChunkResponse);

                    chunkNumber++;
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return e.getMessage();
        }

        return "File upload completed successfully.";
    }

}