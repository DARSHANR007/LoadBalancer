

import java.util.List;


public interface LoadBalancingStrategy {
    
    BackendServer selectServer(List<BackendServer> servers);
    
    
    String getStrategyName();
}
