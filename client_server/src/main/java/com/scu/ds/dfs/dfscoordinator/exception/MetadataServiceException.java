package com.scu.ds.dfs.dfscoordinator.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class MetadataServiceException extends RuntimeException {
    private HttpStatus status;
    public MetadataServiceException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }
}
