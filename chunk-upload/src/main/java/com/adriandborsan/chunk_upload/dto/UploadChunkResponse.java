package com.adriandborsan.chunk_upload.dto;

import lombok.Data;

@Data
public class UploadChunkResponse {
    private String uploadId;
    private int chunkNumber;
    private String message;
    private boolean uploadCompleted;
}
