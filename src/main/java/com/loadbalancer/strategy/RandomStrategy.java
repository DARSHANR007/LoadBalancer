package com.loadbalancer.strategy;

import com.loadbalancer.server.BackendServer;
import java.util.List;
import java.util.Random;

public class RandomStrategy implements LoadBalancingStrategy {
    private final Random random;

    public RandomStrategy() {
        this.random = new Random();
    }

    @Override
    public BackendServer selectServer(List<BackendServer> servers) {
        if (servers == null || servers.isEmpty()) {
            return null;
        }

       
        List<BackendServer> healthyServers = servers.stream()
                .filter(BackendServer::isHealthy)
                .toList();

        if (healthyServers.isEmpty()) {
            return null;
        }

        int randomIndex = random.nextInt(healthyServers.size());
        return healthyServers.get(randomIndex);
    }

    @Override
    public String getStrategyName() {
        return "Random";
    }
}
