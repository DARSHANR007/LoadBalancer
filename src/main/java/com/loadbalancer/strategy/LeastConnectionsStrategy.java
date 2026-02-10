package com.loadbalancer.strategy;

import com.loadbalancer.server.BackendServer;
import java.util.List;


public class LeastConnectionsStrategy implements LoadBalancingStrategy {

    
    @Override
    public BackendServer selectServer(List<BackendServer> servers) {
        if (servers == null || servers.isEmpty()) {
            return null;
        }

        BackendServer selectedServer = null;
        int minRequests = Integer.MAX_VALUE;

        for (BackendServer server : servers) {
            if (server.isHealthy() && server.getRequestCount() < minRequests) {
                selectedServer = server;
                minRequests = server.getRequestCount();
            }
        }

        return selectedServer;
    }

    @Override
    public String getStrategyName() {
        return "Least Connections";
    }
}
