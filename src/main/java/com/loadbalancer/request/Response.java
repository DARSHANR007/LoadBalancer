package com.loadbalancer.request;

import com.loadbalancer.server.BackendServer;

/**
 * Represents a response from a backend server.
 */
public class Response {
    private final String requestId;
    private final int statusCode;
    private final Object data;
    private final BackendServer sourceServer;
    private final long processingTime;

    /**
     * Creates a new response.
     * 
     * @param requestId Original request ID
     * @param statusCode HTTP status code
     * @param data Response data
     * @param sourceServer Server that processed the request
     * @param processingTime Time taken to process the request
     */
    public Response(String requestId, int statusCode, Object data, 
                   BackendServer sourceServer, long processingTime) {
        this.requestId = requestId;
        this.statusCode = statusCode;
        this.data = data;
        this.sourceServer = sourceServer;
        this.processingTime = processingTime;
    }

    public String getRequestId() {
        return requestId;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public Object getData() {
        return data;
    }

    public BackendServer getSourceServer() {
        return sourceServer;
    }

    public long getProcessingTime() {
        return processingTime;
    }

    @Override
    public String toString() {
        return String.format("Response{requestId='%s', statusCode=%d, sourceServer='%s', processingTime=%dms}",
                requestId, statusCode, sourceServer.getId(), processingTime);
    }
}
