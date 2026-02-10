package com.loadbalancer;

import com.loadbalancer.request.Request;
import com.loadbalancer.request.Response;
import com.loadbalancer.server.BackendServer;
import com.loadbalancer.strategy.LoadBalancingStrategy;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Main Load Balancer implementation.
 * 
 * Responsibilities:
 * - Maintain a list of backend servers
 * - Route incoming requests using the specified strategy
 * - Monitor server health
 * - Handle fault tolerance
 * 
 * Thread-Safety: Uses CopyOnWriteArrayList for thread-safe server list access
 */
public class LoadBalancer {
    private final List<BackendServer> servers;
    private final LoadBalancingStrategy strategy;
    private final String name;
    private volatile boolean running;

    /**
     * Creates a new load balancer.
     * 
     * @param name Name identifier for the load balancer
     * @param strategy The load balancing strategy to use
     */
    public LoadBalancer(String name, LoadBalancingStrategy strategy) {
        this.name = name;
        this.strategy = strategy;
        this.servers = new CopyOnWriteArrayList<>();
        this.running = true;
    }

    /**
     * Adds a backend server to the pool.
     * 
     * @param server The server to add
     */
    public void addServer(BackendServer server) {
        if (server != null && !servers.contains(server)) {
            servers.add(server);
            System.out.println("[" + name + "] Added server: " + server);
        }
    }

    /**
     * Removes a backend server from the pool.
     * 
     * @param server The server to remove
     */
    public void removeServer(BackendServer server) {
        if (servers.remove(server)) {
            System.out.println("[" + name + "] Removed server: " + server);
        }
    }

    /**
     * Performs health check on all servers.
     * Simulates checking server availability.
     */
    public void healthCheck() {
        if (!running) return;

        System.out.println("\n[" + name + "] --- Health Check Started ---");
        for (BackendServer server : servers) {
            boolean isHealthy = simulateHealthCheck(server);
            boolean wasHealthy = server.isHealthy();

            server.setHealthy(isHealthy);

            // Log status changes
            if (wasHealthy && !isHealthy) {
                System.out.println("[" + name + "] ⚠️  Server DOWN: " + server.getId());
            } else if (!wasHealthy && isHealthy) {
                System.out.println("[" + name + "] ✓ Server UP: " + server.getId());
            } else if (isHealthy) {
                System.out.println("[" + name + "] ✓ Server HEALTHY: " + server.getId());
            }
        }
        System.out.println("[" + name + "] --- Health Check Completed ---\n");
    }

    /**
     * Simulates a health check to a backend server.
     * In production, this would make an actual HTTP request to the server.
     * 
     * @param server Server to check
     * @return true if server is healthy, false otherwise
     */
    private boolean simulateHealthCheck(BackendServer server) {
        // Simulate 90% success rate to introduce occasional failures
        return Math.random() > 0.1;
    }

    /**
     * Routes a request to an appropriate backend server.
     * 
     * Algorithm:
     * 1. Use the load balancing strategy to select a server
     * 2. If no healthy server available, return null response
     * 3. Forward the request to the selected server
     * 4. Increment the server's request counter
     * 5. Simulate processing and return response
     * 
     * @param request The incoming request to route
     * @return Response from the selected backend server, or null if no healthy server
     */
    public Response routeRequest(Request request) {
        if (!running) {
            System.out.println("[" + name + "] Load Balancer is not running");
            return null;
        }

        // Step 1: Select server using the configured strategy
        BackendServer selectedServer = strategy.selectServer(servers);

        // Step 2: Check if a healthy server was found
        if (selectedServer == null) {
            System.out.println("[" + name + "] ❌ No healthy servers available for request: " + request.getId());
            return new Response(request.getId(), 503, "Service Unavailable - No healthy backends", null, 0);
        }

        // Step 3: Forward request to the selected server
        System.out.println("[" + name + "] 🔀 Routing request [" + request.getId() + "] to: " + selectedServer.getId());

        // Step 4: Increment request counter
        selectedServer.incrementRequestCount();

        // Step 5: Simulate processing on the backend server
        Response response = processRequestOnServer(request, selectedServer);

        return response;
    }

    /**
     * Simulates processing a request on a backend server.
     * 
     * @param request The request to process
     * @param server The server processing the request
     * @return The response from the server
     */
    private Response processRequestOnServer(Request request, BackendServer server) {
        long startTime = System.currentTimeMillis();

        // Simulate processing time (50-200ms)
        long processingTime = 50 + (long) (Math.random() * 150);
        try {
            Thread.sleep(processingTime);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;

        // Simulate occasional failures (5% failure rate)
        int statusCode = Math.random() < 0.05 ? 500 : 200;
        String responseData = String.format("Response from %s (processed in %dms)", 
                                           server.getId(), totalTime);

        return new Response(request.getId(), statusCode, responseData, server, totalTime);
    }

    /**
     * Shuts down the load balancer.
     */
    public void shutdown() {
        running = false;
        System.out.println("[" + name + "] Load Balancer shutting down...");
    }

    /**
     * Prints statistics about the load balancer and its servers.
     */
    public void printStatistics() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("Load Balancer: " + name);
        System.out.println("Strategy: " + strategy.getStrategyName());
        System.out.println("Total Servers: " + servers.size());
        System.out.println("Healthy Servers: " + getHealthyServerCount());
        System.out.println("-".repeat(60));

        for (BackendServer server : servers) {
            String status = server.isHealthy() ? "✓ UP" : "✗ DOWN";
            System.out.printf("  %s: %s | Requests: %d%n", 
                            server.getId(), status, server.getRequestCount());
        }
        System.out.println("=".repeat(60) + "\n");
    }

    /**
     * Gets the count of healthy servers.
     * 
     * @return Number of healthy servers
     */
    public int getHealthyServerCount() {
        return (int) servers.stream().filter(BackendServer::isHealthy).count();
    }

    /**
     * Gets the total number of servers.
     * 
     * @return Number of servers
     */
    public int getServerCount() {
        return servers.size();
    }

    // Getters
    public String getName() {
        return name;
    }

    public List<BackendServer> getServers() {
        return new ArrayList<>(servers);
    }

    public LoadBalancingStrategy getStrategy() {
        return strategy;
    }
}
