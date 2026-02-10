# Quick Start Guide

## Setup & Installation

### Prerequisites
- Java 11 or higher
- Maven 3.6.0 or higher

### Installation

1. **Navigate to the project directory**
   ```bash
   cd d:\JAVA\ shit\Loadbalancer
   ```

2. **Build the project**
   ```bash
   mvn clean install
   ```

3. **Run the demo**
   ```bash
   mvn clean compile exec:java -Dexec.mainClass="com.loadbalancer.LoadBalancerDemo"
   ```

4. **Run tests**
   ```bash
   mvn test
   ```

## Understanding the Code

### 1. Core Components

#### BackendServer.java
Represents a single backend server with:
- Health status tracking
- Request counter (atomic for thread-safety)
- Connection details (host, port)

```java
BackendServer server = new BackendServer("Server-1", "192.168.1.10", 8080);
server.incrementRequestCount();
server.setHealthy(false);  // Mark as unhealthy
```

#### LoadBalancer.java
Main class that:
- Maintains server pool
- Routes requests using strategies
- Performs health checks
- Provides statistics

```java
LoadBalancer lb = new LoadBalancer("MyLB", new RoundRobinStrategy());
lb.addServer(server1);
lb.addServer(server2);
Response response = lb.routeRequest(request);
```

#### Strategies
Different server selection algorithms:

| Class | Algorithm | Best For |
|-------|-----------|----------|
| RoundRobinStrategy | Cyclic selection | Equal capacity servers |
| LeastConnectionsStrategy | Minimum connections | Long-lived connections |
| RandomStrategy | Random selection | Simple use cases |
| IPHashStrategy | Hash-based persistence | Session persistence |
| WeightedRoundRobinStrategy | Weighted cyclic | Heterogeneous servers |

### 2. Key Algorithms

#### Round-Robin Algorithm
```
counter = 0

selectServer(servers):
    for each server in servers:
        index = counter++ % servers.length
        if servers[index].isHealthy():
            return servers[index]
    return null
```

**Use Case**: Web servers with stateless services
**Distribution**: Perfect - each server gets equal load

#### Least Connections Algorithm
```
selectServer(servers):
    min_server = null
    min_connections = MAX_INT
    
    for each server in servers:
        if server.isHealthy() and 
           server.requestCount < min_connections:
            min_server = server
            min_connections = server.requestCount
    
    return min_server
```

**Use Case**: Services with long connection times
**Distribution**: Adaptive - based on actual load

### 3. Request Flow

```
Client Request
    ↓
LoadBalancer.routeRequest(request)
    ↓
strategy.selectServer(servers)
    ↓
Health Check
    ├─ Healthy → Selected
    └─ Unhealthy → Try Next
    ↓
server.incrementRequestCount()
    ↓
Simulate Backend Processing
    ↓
Return Response with Metadata
```

## Common Usage Patterns

### Pattern 1: Basic Round-Robin Load Balancing

```java
import com.loadbalancer.*;
import com.loadbalancer.server.BackendServer;
import com.loadbalancer.strategy.RoundRobinStrategy;
import com.loadbalancer.request.Request;

public class BasicExample {
    public static void main(String[] args) {
        // Create load balancer
        LoadBalancer lb = new LoadBalancer("WebLB", new RoundRobinStrategy());
        
        // Add servers
        lb.addServer(new BackendServer("web1", "10.0.1.10", 8080));
        lb.addServer(new BackendServer("web2", "10.0.1.11", 8080));
        lb.addServer(new BackendServer("web3", "10.0.1.12", 8080));
        
        // Route requests
        for (int i = 1; i <= 9; i++) {
            Request req = new Request("REQ-" + i, "/api/users", "GET", null);
            Response resp = lb.routeRequest(req);
            System.out.println(resp);
        }
        
        // Print statistics
        lb.printStatistics();
    }
}
```

### Pattern 2: Least Connections for Chat Service

```java
LoadBalancer lb = new LoadBalancer("ChatLB", 
                                   new LeastConnectionsStrategy());

// Add chat servers with varying capacities
lb.addServer(new BackendServer("chat1", "10.0.2.10", 5000));
lb.addServer(new BackendServer("chat2", "10.0.2.11", 5000));

// Route messages - automatically balances based on connections
Request chatRequest = new Request("CHAT-001", "/messages", "POST", messageData);
Response response = lb.routeRequest(chatRequest);
```

### Pattern 3: Health Monitoring

```java
LoadBalancer lb = new LoadBalancer("MonitoredLB", new RoundRobinStrategy());

// Add servers
// ... (add servers)

// Periodic health check
new Thread(() -> {
    while (true) {
        lb.healthCheck();
        try {
            Thread.sleep(10000);  // Check every 10 seconds
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            break;
        }
    }
}).start();

// Route requests normally
// Unhealthy servers will be automatically skipped
```

### Pattern 4: Graceful Shutdown

```java
LoadBalancer lb = new LoadBalancer("GracefulLB", new RoundRobinStrategy());

// ... add servers and process requests ...

// When shutting down:
System.out.println("Shutting down load balancer...");
lb.printStatistics();  // Final statistics
lb.shutdown();
```

## Understanding Demo Output

### Round-Robin Demo
```
Routing 9 requests:
  🔀 Routing request [REQ-1] to: Server-A
  🔀 Routing request [REQ-2] to: Server-B
  🔀 Routing request [REQ-3] to: Server-C
  🔀 Routing request [REQ-4] to: Server-A
  ...
```
**Expected**: Each server gets 3 requests (9 ÷ 3)

### Least Connections Demo
```
Server-X: ✓ UP | Requests: 2
Server-Y: ✓ UP | Requests: 2
Server-Z: ✓ UP | Requests: 2
```
**Expected**: Equal distribution despite different arrival patterns

### Fault Tolerance Demo
```
Step 1: All servers healthy - routing 3 requests
Step 2: Simulating Server-2 failure...
⚠️  FT-Server-2 marked as UNHEALTHY

Step 3: Routing 3 requests with server-2 down
```
**Expected**: Server-2 receives 0 requests after becoming unhealthy

## Performance Tips

1. **For High Throughput**: Use Round-Robin (O(1) amortized)
2. **For Mixed Workloads**: Use Least Connections (considers load)
3. **For Session Persistence**: Use IP Hash (same client→same server)
4. **For Heterogeneous Servers**: Use Weighted Round-Robin

## Extending the Load Balancer

### Add New Strategy

```java
public class MyCustomStrategy implements LoadBalancingStrategy {
    @Override
    public BackendServer selectServer(List<BackendServer> servers) {
        // Your custom algorithm here
        return selectedServer;
    }

    @Override
    public String getStrategyName() {
        return "My Custom Strategy";
    }
}

// Use it
LoadBalancer lb = new LoadBalancer("LB", new MyCustomStrategy());
```

### Add Metrics Collection

```java
LoadBalancer lb = new LoadBalancer("LB", new RoundRobinStrategy());
// ... add servers ...

long startTime = System.currentTimeMillis();

for (int i = 0; i < 1000; i++) {
    Request req = new Request("REQ-" + i, "/test", "GET", null);
    lb.routeRequest(req);
}

long duration = System.currentTimeMillis() - startTime;
System.out.println("Processed 1000 requests in " + duration + "ms");
System.out.println("Throughput: " + (1000.0 / duration * 1000) + " req/sec");
```

## Troubleshooting

### Issue: All requests go to one server
**Cause**: Other servers marked as unhealthy
**Solution**: Run health check, check server logs

### Issue: Compilation errors
**Solution**: Ensure Java 11+ and Maven 3.6.0+
```bash
java -version
mvn -version
```

### Issue: Tests fail
**Solution**: Run with more verbose output
```bash
mvn test -X
```

## Next Steps

1. **Study the Algorithms**: Review `ALGORITHM_ANALYSIS.md`
2. **Examine the Code**: Read inline comments in source files
3. **Run the Demo**: See algorithms in action
4. **Write Custom Strategies**: Implement your own
5. **Add Features**: Health check intervals, metrics, logging

## Resources

- [README.md](README.md) - Project overview
- [ALGORITHM_ANALYSIS.md](ALGORITHM_ANALYSIS.md) - In-depth algorithm analysis
- Source code with inline documentation

Happy learning! 🚀
