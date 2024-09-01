package com.adriandborsan.chunk_client.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class UploadRequest {
    private String fileName;
    private String fileDescription;
    private MultipartFile file;
}
