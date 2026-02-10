

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Represents a backend server that will handle incoming requests.
 * Maintains server metadata, health status, and request count for monitoring.
 */
public class BackendServer {
    private final String id;
    private final String host;
    private final int port;
    private volatile boolean healthy;
    private final AtomicInteger requestCount;
    private volatile long lastHealthCheckTime;

    /**
     * Creates a new backend server instance.
     * 
     * @param id Unique identifier for the server
     * @param host Hostname or IP address
     * @param port Port number
     */
    public BackendServer(String id, String host, int port) {
        this.id = id;
        this.host = host;
        this.port = port;
        this.healthy = true;
        this.requestCount = new AtomicInteger(0);
        this.lastHealthCheckTime = System.currentTimeMillis();
    }

    /**
     * Increments the request counter atomically.
     */
    public void incrementRequestCount() {
        requestCount.incrementAndGet();
    }

    /**
     * Marks the server as healthy or unhealthy.
     * 
     * @param healthy true if server is healthy, false otherwise
     */
    public void setHealthy(boolean healthy) {
        this.healthy = healthy;
        this.lastHealthCheckTime = System.currentTimeMillis();
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public boolean isHealthy() {
        return healthy;
    }

    public int getRequestCount() {
        return requestCount.get();
    }

    public long getLastHealthCheckTime() {
        return lastHealthCheckTime;
    }

    public String getAddress() {
        return String.format("%s:%d", host, port);
    }

    @Override
    public String toString() {
        return String.format("BackendServer{id='%s', address='%s:%d', healthy=%b, requests=%d}",
                id, host, port, healthy, requestCount.get());
    }
}
