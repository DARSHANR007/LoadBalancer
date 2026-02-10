package com.loadbalancer.request;

/**
 * Represents a client request that needs to be routed to a backend server.
 */
public class Request {
    private final String id;
    private final String path;
    private final String method;
    private final long timestamp;
    private final Object payload;

    /**
     * Creates a new request.
     * 
     * @param id Unique request identifier
     * @param path Request path/endpoint
     * @param method HTTP method (GET, POST, etc.)
     * @param payload Request payload
     */
    public Request(String id, String path, String method, Object payload) {
        this.id = id;
        this.path = path;
        this.method = method;
        this.payload = payload;
        this.timestamp = System.currentTimeMillis();
    }

    public String getId() {
        return id;
    }

    public String getPath() {
        return path;
    }

    public String getMethod() {
        return method;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public Object getPayload() {
        return payload;
    }

    @Override
    public String toString() {
        return String.format("Request{id='%s', method='%s', path='%s', timestamp=%d}",
                id, method, path, timestamp);
    }
}
