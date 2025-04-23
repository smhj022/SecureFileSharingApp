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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class S3Service {

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;
    private static final Logger logger = LoggerFactory.getLogger(S3Service.class);


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
            logger.info("Received request to list objects of bucket");
            ListObjectsV2Request request = ListObjectsV2Request.builder()
                    .bucket(bucketName)
                    .build();

            ListObjectsV2Response result = s3Client.listObjectsV2(request);

            List<S3ObjectDto> s3ObjectDtoList = new ArrayList<>();

            for(S3Object s3Object : result.contents()){
                s3ObjectDtoList.add(convertToDto(s3Object));
            }

            logger.info("Object list is successfully fetched");
            return s3ObjectDtoList;
        } catch (S3Exception e){
            logger.error("Unable to list the object from the bucket");
            throw new AWSServiceException("Unable to list the object from the bucket", e);
        }
    }

    public String uploadFile(MultipartFile file) {
        try {
            if (file == null || file.isEmpty()) {
                logger.warn("File is empty or null");
                throw new FileStorageException("File is empty or null");
            }

            String key = file.getOriginalFilename();
            logger.info("Uploading file: {}", key);

            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .contentType(file.getContentType())
                    .build();

            s3Client.putObject(putObjectRequest, RequestBody.fromBytes(file.getBytes()));

            logger.info("Successfully uploaded file: {}", key);
            return key;
        } catch (Exception e) {
            logger.error("Failed to upload file: {}", file != null ? file.getOriginalFilename() : "null", e);
            throw new FileStorageException("Failed to upload file to S3", e);
        }
    }

    public byte[] downloadFile(String key) {
        try {
            logger.info("Received request to download file");
            return s3Client.getObjectAsBytes(builder -> builder
                    .bucket(bucketName)
                    .key(key)
            ).asByteArray();
        } catch (NoSuchKeyException e) {
            logger.error("File not found in S3 with key: " + key);
            throw new FileStorageException("File not found in S3 with key: " + key, e);
        } catch (S3Exception e) {
            logger.error("S3 error while downloading file");
            throw new AWSServiceException("S3 error while downloading file", e);
        } catch (Exception e) {
            logger.error("Error occurred during file download");
            throw new FileStorageException("Unexpected error during file download", e);
        }

    }

    public String generatePreSignedUrl(String key) {
        try {
            logger.info("Received request to generate pre-signed url for : " + key);
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build();

            GetObjectPresignRequest preSignRequest = GetObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofMinutes(2))
                    .getObjectRequest(getObjectRequest)
                    .build();
            logger.info("Pre-signed url generated successfully : " + key);
            return s3Presigner.presignGetObject(preSignRequest).url().toString();
        } catch (S3Exception e){
            logger.error("Failed to generate pre-signed url for :" + key );
            throw new AWSServiceException("Failed to generate pre-signed url for " + key, e);
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

