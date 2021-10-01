package com.ctrip.framework.apollo.openapi.client.service;

public class GetClusterException extends RuntimeException{
    public GetClusterException() {}

    public GetClusterException(String str, Throwable cause) {
        super(str, cause);
    }
}
