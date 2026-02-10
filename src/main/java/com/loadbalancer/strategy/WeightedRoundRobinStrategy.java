package com.loadbalancer.strategy;

import com.loadbalancer.server.BackendServer;
import java.util.List;

/**
 * Weighted Load Balancing Strategy.
 * 
 * Algorithm:
 * 1. Each server has a weight (e.g., 1, 2, 3)
 * 2. Server with weight 3 gets 3x more requests than weight 1 server
 * 3. Uses modulo-based weighted distribution
 * 
 * Time Complexity: O(n) worst case
 * Space Complexity: O(1)
 * 
 * Pros: Accommodates heterogeneous servers with different capacities
 * Cons: Requires weight configuration, slightly more complex
 * 
 * Example:
 * - Server A (weight=1): Gets 1/6 of requests
 * - Server B (weight=2): Gets 2/6 of requests  
 * - Server C (weight=3): Gets 3/6 of requests
 * Total weight = 6
 */
public class WeightedRoundRobinStrategy implements LoadBalancingStrategy {
    private final int[] weights;
    private int currentIndex = 0;

    /**
     * Creates weighted round-robin strategy.
     * Note: In production, weights would come from configuration.
     * 
     * @param servers List of servers - weights derived from order
     */
    public WeightedRoundRobinStrategy(List<BackendServer> servers) {
        // Default weights: 1, 2, 3, 1, 2, 3, ...
        this.weights = new int[servers.size()];
        for (int i = 0; i < weights.length; i++) {
            weights[i] = (i % 3) + 1;
        }
    }

    @Override
    public BackendServer selectServer(List<BackendServer> servers) {
        if (servers == null || servers.isEmpty()) {
            return null;
        }

        int totalWeight = getTotalWeight(servers);
        if (totalWeight <= 0) {
            return null;  // No healthy servers
        }

        // Find server using weighted distribution
        int currentWeight = currentIndex % totalWeight;
        int accumulatedWeight = 0;

        for (int i = 0; i < servers.size(); i++) {
            BackendServer server = servers.get(i);
            
            if (server.isHealthy()) {
                accumulatedWeight += weights[i];
                
                if (currentWeight < accumulatedWeight) {
                    currentIndex++;
                    return server;
                }
            }
        }

        currentIndex++;
        return null;
    }

    /**
     * Calculates total weight of all healthy servers.
     */
    private int getTotalWeight(List<BackendServer> servers) {
        int total = 0;
        for (int i = 0; i < servers.size(); i++) {
            if (servers.get(i).isHealthy()) {
                total += weights[i];
            }
        }
        return total;
    }

    @Override
    public String getStrategyName() {
        return "Weighted Round-Robin";
    }
}
