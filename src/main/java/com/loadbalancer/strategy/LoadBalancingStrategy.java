package com.loadbalancer.strategy;

import com.loadbalancer.server.BackendServer;
import java.util.List;

/**
 * Interface for server selection strategies.
 * Different implementations can provide various load balancing algorithms.
 */
public interface LoadBalancingStrategy {
    /**
     * Selects the next server to handle a request from the available servers.
     * 
     * @param servers List of available backend servers
     * @return Selected BackendServer, or null if no healthy server is available
     */
    BackendServer selectServer(List<BackendServer> servers);
    
    /**
     * Gets the name of the strategy.
     * 
     * @return Strategy name
     */
    String getStrategyName();
}
