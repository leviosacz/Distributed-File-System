package com.scu.ds.dfs.dfscoordinator.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class DocumentServiceException extends RuntimeException {
    private HttpStatus status;
    public DocumentServiceException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }
}
