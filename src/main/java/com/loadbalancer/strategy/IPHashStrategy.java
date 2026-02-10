package com.loadbalancer.strategy;

import com.loadbalancer.server.BackendServer;
import java.util.List;


public class IPHashStrategy implements LoadBalancingStrategy {

    
    @Override
    public BackendServer selectServer(List<BackendServer> servers) {
        if (servers == null || servers.isEmpty()) {
            return null;
        }

        // Simulate client IP (in production, would come from request context)
        String clientIp = getClientIP();
        
        // Hash the IP and get server index
        int hash = Math.abs(clientIp.hashCode());
        int serverCount = servers.size();
        int initialIndex = hash % serverCount;

        // Find healthy server starting from hash index
        for (int i = 0; i < serverCount; i++) {
            int index = (initialIndex + i) % serverCount;
            BackendServer server = servers.get(index);
            if (server.isHealthy()) {
                return server;
            }
        }

        return null;
    }

    /**
     * Simulates getting client IP address.
     * In production, would extract from HTTP request headers.
     * 
     * @return Simulated client IP
     */
    private String getClientIP() {
        // Simulate a range of client IPs
        return "192.168." + (int)(Math.random() * 256) + "." + (int)(Math.random() * 256);
    }

    @Override
    public String getStrategyName() {
        return "IP Hash";
    }
}
