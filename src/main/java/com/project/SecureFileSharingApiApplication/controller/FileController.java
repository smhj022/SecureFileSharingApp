package com.project.SecureFileSharingApiApplication.controller;

import com.project.SecureFileSharingApiApplication.dto.S3ObjectDto;
import com.project.SecureFileSharingApiApplication.service.S3Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.model.S3Object;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/file")
public class FileController {

    private S3Service s3Service;

    FileController(S3Service s3Service){
        this.s3Service = s3Service;
    }

    @PostMapping("/upload")
    public String uploadFile(@RequestParam("file") MultipartFile file) throws IOException {
        try {
            return s3Service.uploadFile(file);
        } catch (IOException e){
            return "Unable to upload file";
        }
    }

    @GetMapping("/list")
    public List<S3ObjectDto> listS3Object(){
        return s3Service.listObjects();
    }

    @GetMapping("/download")
    public ResponseEntity<byte[]> downloadFile(@RequestParam String key) {
        try {
            byte[] fileData = s3Service.downloadFile(key);

            return ResponseEntity.ok()
                    .header("Content-Disposition", "attachment; filename=" + key)
                    .body(fileData);

        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(("Download failed: " + e.getMessage()).getBytes());
        }
    }

    public ResponseEntity<String> getPreSignedUrl(@RequestParam String key) {
        String url = s3Service.generatePreSignedUrl(key);
        return ResponseEntity.ok(url);
    }
}
