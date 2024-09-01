package com.adriandborsan.chunk_client.controller;

import com.adriandborsan.chunk_client.dto.UploadRequest;
import com.adriandborsan.chunk_client.service.ChunkedFileUploadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin("*")
@Slf4j
@RequiredArgsConstructor
public class FileUploadController {

    private final ChunkedFileUploadService chunkedFileUploadService;

    @PostMapping("/upload")
    public String handleFileUpload(@ModelAttribute UploadRequest uploadRequest) {
        log.info("uploadRequest: {}", uploadRequest);
        return chunkedFileUploadService.uploadFile(uploadRequest);
    }
}
