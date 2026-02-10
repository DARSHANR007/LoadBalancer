

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Round-Robin Load Balancing Strategy.
 * 
 * Algorithm:
 * 1. Maintains an atomic counter to track the current position
 * 2. On each request, returns the server at (counter % serverCount)
 * 3. Increments the counter for the next request
 * 4. Skips unhealthy servers and continues to the next one
 * 
 * Time Complexity: O(n) in worst case where n is the number of unhealthy servers
 * Space Complexity: O(1) - only stores a counter
 * 
 * Pros: Simple, fair distribution, no server state tracking needed
 * Cons: Doesn't consider server capacity or current load
 */
public class RoundRobinStrategy implements LoadBalancingStrategy {
    private final AtomicInteger currentIndex;

    public RoundRobinStrategy() {
        this.currentIndex = new AtomicInteger(0);
    }

    /**
     * Selects the next server using round-robin algorithm.
     * Automatically skips unhealthy servers.
     * 
     * @param servers List of available backend servers
     * @return The next server in round-robin order that is healthy, or null if all are unhealthy
     */
    @Override
    public BackendServer selectServer(List<BackendServer> servers) {
        if (servers == null || servers.isEmpty()) {
            return null;
        }


        int serversCount = servers.size();
        for (int i = 0; i < serversCount; i++) {
          
            int index = currentIndex.getAndIncrement() % serversCount;
            BackendServer server = servers.get(index);

            // Return the server if it's healthy
            if (server.isHealthy()) {
                return server;
            }
        }

       
        return null;
    }

    @Override
    public String getStrategyName() {
        return "Round-Robin";
    }
}
