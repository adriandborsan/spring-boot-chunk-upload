package com.adriandborsan.chunk_client.dto;

import lombok.Data;

@Data
public class UploadInitiateRequest {
    private String fileName;
    private String fileDescription;
    private long fileSize;
    private int totalChunks;
    private String fileType;
}
