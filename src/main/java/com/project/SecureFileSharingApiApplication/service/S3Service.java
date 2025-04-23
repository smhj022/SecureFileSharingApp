package com.project.SecureFileSharingApiApplication.service;


import com.project.SecureFileSharingApiApplication.dto.S3ObjectDto;
import com.project.SecureFileSharingApiApplication.exception.AWSServiceException;
import com.project.SecureFileSharingApiApplication.exception.FileStorageException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Service
public class S3Service {

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    @Value("${aws.s3.region}")
    private String region;

    @Autowired
    public S3Service(S3Client s3Client, S3Presigner s3Presigner){
        this.s3Client = s3Client;
        this.s3Presigner = s3Presigner;
    }

    public List<S3ObjectDto> listObjects(){
        try{
            ListObjectsV2Request request = ListObjectsV2Request.builder()
                    .bucket(bucketName)
                    .build();

            ListObjectsV2Response result = s3Client.listObjectsV2(request);

            List<S3ObjectDto> s3ObjectDtoList = new ArrayList<>();

            for(S3Object s3Object : result.contents()){
                s3ObjectDtoList.add(convertToDto(s3Object));
            }

            return s3ObjectDtoList;
        } catch (S3Exception e){
            throw new AWSServiceException("Unable to list the object from the bucket", e);
        }
    }

    public String uploadFile(MultipartFile file)  {
        try {
            if (file == null || file.isEmpty()) {
                throw new FileStorageException("File is empty or null");
            }
            String key = file.getOriginalFilename();

            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .contentType(file.getContentType())
                    .build();

            s3Client.putObject(putObjectRequest, RequestBody.fromBytes(file.getBytes()));

            return key;
        } catch (Exception e){
            throw new FileStorageException("Failed to Upload File to S3");
        }
    }

    public byte[] downloadFile(String key) {
        try {
            return s3Client.getObjectAsBytes(builder -> builder
                    .bucket(bucketName)
                    .key(key)
            ).asByteArray();
        } catch (NoSuchKeyException e) {
            throw new FileStorageException("File not found in S3 with key: " + key, e);
        } catch (S3Exception e) {
            throw new AWSServiceException("S3 error while downloading file", e);
        } catch (Exception e) {
            throw new FileStorageException("Unexpected error during file download", e);
        }

    }

    public String generatePreSignedUrl(String key) {
        try {
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build();

            GetObjectPresignRequest preSignRequest = GetObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofMinutes(2))
                    .getObjectRequest(getObjectRequest)
                    .build();

            return s3Presigner.presignGetObject(preSignRequest).url().toString();
        } catch (S3Exception e){
            throw new AWSServiceException("Failed to generate pre-signed url", e);
        }
    }

    public S3ObjectDto convertToDto(S3Object s3Object){
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
