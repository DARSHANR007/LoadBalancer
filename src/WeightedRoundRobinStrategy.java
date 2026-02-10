

import java.util.List;


public class WeightedRoundRobinStrategy implements LoadBalancingStrategy {
    private final int[] weights;
    private int currentIndex = 0;

   
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
