package com.adriandborsan.chunk_upload.dto;

import lombok.Data;

@Data
public class InitUploadRequest {
    private String fileName;
    private String fileDescription;
    private long fileSize;
    private int totalChunks;
    private String fileType;
}

