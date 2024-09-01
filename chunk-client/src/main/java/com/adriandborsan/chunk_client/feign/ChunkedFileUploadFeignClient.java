package com.adriandborsan.chunk_client.feign;


import com.adriandborsan.chunk_client.dto.UploadChunkRequest;
import com.adriandborsan.chunk_client.dto.UploadChunkResponse;
import com.adriandborsan.chunk_client.dto.UploadInitiateRequest;
import com.adriandborsan.chunk_client.dto.UploadInitiateResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "chunkedFileUploadClient", url = "${chunked.upload.base.url}")
public interface ChunkedFileUploadFeignClient {

    @PostMapping("/api/upload/initiate")
    UploadInitiateResponse initiateUpload(@RequestBody UploadInitiateRequest uploadInitiateRequest);

    @PostMapping("/api/upload/chunk")
    UploadChunkResponse uploadChunk(@RequestBody UploadChunkRequest uploadChunkRequest);
}
