package com.project.SecureFileSharingApiApplication.controller;

import com.project.SecureFileSharingApiApplication.dto.S3ObjectDto;
import com.project.SecureFileSharingApiApplication.service.S3Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/file")
public class FileController {

    private S3Service s3Service;

    @Autowired
    public FileController(S3Service s3Service) {
        this.s3Service = s3Service;
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) throws IOException {
        return ResponseEntity.ok(s3Service.uploadFile(file));
    }

    @GetMapping("/list")
    public ResponseEntity<List<S3ObjectDto>> listS3Object(){
        return new ResponseEntity<>(s3Service.listObjects(), HttpStatus.OK);
    }

    @GetMapping("/download")
    public ResponseEntity<byte[]> downloadFile(@RequestParam String key) {
        byte[] fileData = s3Service.downloadFile(key);
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=" + key)
                .body(fileData);
    }

    @GetMapping("/presigned-url")
    public ResponseEntity<String> getPreSignedUrl(@RequestParam String key) {
        String url = s3Service.generatePreSignedUrl(key);
        return ResponseEntity.ok(url);
    }
}
