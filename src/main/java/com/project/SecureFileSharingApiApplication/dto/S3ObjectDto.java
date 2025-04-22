package com.project.SecureFileSharingApiApplication.dto;

import software.amazon.awssdk.services.s3.model.Owner;
import software.amazon.awssdk.services.s3.model.RestoreStatus;

import java.time.Instant;
import java.util.List;

public class S3ObjectDto {

    private String key;
    private Instant lastModified;
    private String eTag;
    private List<String> checksumAlgorithm;
    private String checksumType;
    private Long size;
    private String storageClass;
    private Owner owner;
    private RestoreStatus restoreStatus;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Instant getLastModified() {
        return lastModified;
    }

    public void setLastModified(Instant lastModified) {
        this.lastModified = lastModified;
    }

    public String geteTag() {
        return eTag;
    }

    public void seteTag(String eTag) {
        this.eTag = eTag;
    }

    public List<String> getChecksumAlgorithm() {
        return checksumAlgorithm;
    }

    public void setChecksumAlgorithm(List<String> checksumAlgorithm) {
        this.checksumAlgorithm = checksumAlgorithm;
    }

    public String getChecksumType() {
        return checksumType;
    }

    public void setChecksumType(String checksumType) {
        this.checksumType = checksumType;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public String getStorageClass() {
        return storageClass;
    }

    public void setStorageClass(String storageClass) {
        this.storageClass = storageClass;
    }

    public Owner getOwner() {
        return owner;
    }

    public void setOwner(Owner owner) {
        this.owner = owner;
    }

    public RestoreStatus getRestoreStatus() {
        return restoreStatus;
    }

    public void setRestoreStatus(RestoreStatus restoreStatus) {
        this.restoreStatus = restoreStatus;
    }
}
