package com.adriandborsan.chunk_upload.controller;

import com.adriandborsan.chunk_upload.dto.InitUploadRequest;
import com.adriandborsan.chunk_upload.dto.InitUploadResponse;
import com.adriandborsan.chunk_upload.dto.UploadChunkRequest;
import com.adriandborsan.chunk_upload.dto.UploadChunkResponse;
import com.adriandborsan.chunk_upload.service.FileUploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/upload")
@RequiredArgsConstructor
public class FileUploadController {

    private final FileUploadService fileUploadService;

    @PostMapping("/initiate")
    public InitUploadResponse initiateUpload(@RequestBody InitUploadRequest request) {
        return fileUploadService.initiateUpload(request);
    }

    @PostMapping("/chunk")
    public UploadChunkResponse uploadChunk(@RequestBody UploadChunkRequest request) {
        return fileUploadService.uploadChunk(request);
    }
}
