package com.project.SecureFileSharingApiApplication.service;


import com.project.SecureFileSharingApiApplication.dto.S3ObjectDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Service
public class S3Service {

    private final S3Client s3Client;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    @Value("${aws.s3.region}")
    private String region;

    @Autowired
    public S3Service(S3Client s3Client){
        this.s3Client = s3Client;
    }

    public List<S3ObjectDto> listObjects(){

        ListObjectsV2Request request = ListObjectsV2Request.builder()
                .bucket(bucketName)
                .build();

        ListObjectsV2Response result = s3Client.listObjectsV2(request);

        List<S3ObjectDto> s3ObjectDtoList = new ArrayList<>();

        for(S3Object s3Object : result.contents()){
            s3ObjectDtoList.add(S3ObjectToS3FileDto(s3Object));
        }

        return s3ObjectDtoList;
    }

    public String uploadFile(MultipartFile file) throws IOException {

        String key = file.getOriginalFilename();

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .contentType(file.getContentType())
                .build();

        s3Client.putObject(putObjectRequest, RequestBody.fromBytes(file.getBytes()));

        return key;

    }

    public byte[] downloadFile(String key) {
        return s3Client.getObjectAsBytes(builder -> builder
                .bucket(bucketName)
                .key(key)
        ).asByteArray();
    }

    public String generatePreSignedUrl(String key){
        S3Presigner presigner = S3Presigner.builder()
                .region(Region.of(region)) // e.g., "ap-south-1"
                .build();

        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();

        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(2)) // URL expires in 10 minutes
                .getObjectRequest(getObjectRequest)
                .build();

        return presigner.presignGetObject(presignRequest).url().toString();
    }


    public S3ObjectDto S3ObjectToS3FileDto(S3Object s3Object){
        S3ObjectDto s3ObjectDto = new S3ObjectDto();
        s3ObjectDto.setChecksumAlgorithm(s3Object.checksumAlgorithmAsStrings());
        s3ObjectDto.setChecksumType(s3Object.checksumTypeAsString());
        s3ObjectDto.seteTag(s3Object.eTag());
        s3ObjectDto.setKey(s3Object.key());
        s3ObjectDto.setSize(s3Object.size());
        s3ObjectDto.setOwner(s3Object.owner());
        s3ObjectDto.setRestoreStatus(s3Object.restoreStatus());
        s3ObjectDto.setLastModified(s3Object.lastModified());
        return s3ObjectDto;
    }

}
