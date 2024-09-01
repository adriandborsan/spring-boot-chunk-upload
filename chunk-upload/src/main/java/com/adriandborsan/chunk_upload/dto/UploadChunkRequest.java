package com.adriandborsan.chunk_upload.dto;

import lombok.Data;
import lombok.ToString;

@Data
public class UploadChunkRequest {
    private String uploadId;
    private int chunkNumber;
    @ToString.Exclude
    private String chunkData; // Base64 encoded string
    private Boolean isFinalChunk;
}
