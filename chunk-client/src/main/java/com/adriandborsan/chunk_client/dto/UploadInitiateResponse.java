package com.adriandborsan.chunk_client.dto;

import lombok.Data;

@Data
public class UploadInitiateResponse {
    private String uploadId;
    private String message;
}
