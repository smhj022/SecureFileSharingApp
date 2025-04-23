package com.project.SecureFileSharingApiApplication.exception;

public class AWSServiceException extends RuntimeException{
    public AWSServiceException(String message) {
        super(message);
    }

    public AWSServiceException(String message, Throwable cause) {
        super(message, cause);
    }

}
