<<<<<<< HEAD
# Java Load Balancer

A comprehensive implementation of a load balancer in Java that demonstrates core algorithms and design patterns for distributing incoming client requests across multiple backend servers.

## Overview

This project provides a clean, educational implementation of a load balancer with support for multiple load balancing strategies, health monitoring, and fault tolerance.

## Key Features

### 1. **Multiple Load Balancing Strategies**

#### Round-Robin Strategy
- **Algorithm**: Distributes requests sequentially across servers
- **Formula**: `selectedServer = servers[counter++ % serverCount]`
- **Time Complexity**: O(n) worst case (skipping unhealthy servers)
- **Space Complexity**: O(1)
- **Use Case**: Equal weight distribution, stateless services

```
Request 1 → Server A
Request 2 → Server B
Request 3 → Server C
Request 4 → Server A
...
```

#### Least Connections Strategy
- **Algorithm**: Routes to the server with minimum active requests
- **Logic**: `selectedServer = server with MIN(requestCount)`
- **Time Complexity**: O(n)
- **Space Complexity**: O(1)
- **Use Case**: Long-lived connections, varied request durations

```
Server A: 2 connections
Server B: 5 connections
Server C: 1 connection
→ Route next request to Server C
```

### 2. **Fault Tolerance**

Automatic handling of server failures:
- Maintains health status for each server
- Skips unhealthy servers during request routing
- Returns `503 Service Unavailable` when all servers are down
- Supports dynamic server removal/addition

### 3. **Health Monitoring**

- Periodic health checks on all backend servers
- Automatic status updates (UP/DOWN)
- Configurable health check intervals
- Real-time health status reporting

### 4. **Thread-Safe Design**

- Uses `CopyOnWriteArrayList` for thread-safe server list operations
- Atomic operations for request counters
- Safe concurrent request routing

## Project Structure

```
src/
├── main/java/com/loadbalancer/
│   ├── LoadBalancer.java              # Main load balancer class
│   ├── LoadBalancerDemo.java          # Demonstration program
│   ├── server/
│   │   └── BackendServer.java         # Backend server representation
│   ├── strategy/
│   │   ├── LoadBalancingStrategy.java # Strategy interface
│   │   ├── RoundRobinStrategy.java    # Round-robin implementation
│   │   └── LeastConnectionsStrategy.java # Least connections implementation
│   └── request/
│       ├── Request.java               # Request representation
│       └── Response.java              # Response representation
└── test/java/com/loadbalancer/
    └── LoadBalancerTest.java          # Unit tests
```

## Core Algorithm

### Request Routing Algorithm

```
function routeRequest(request):
    1. Use configured strategy to select best server
    2. if no healthy server found:
        return ServiceUnavailable(503)
    
    3. Increment request counter on selected server
    4. Simulate processing on backend server
    5. Return response with processing details
```

### Round-Robin Selection Algorithm

```
class RoundRobinStrategy:
    counter = 0
    
    function selectServer(servers):
        healthyServersChecked = 0
        
        while healthyServersChecked < servers.length:
            index = counter++ % servers.length
            server = servers[index]
            
            if server.isHealthy():
                return server
            
            healthyServersChecked++
        
        return null  // No healthy server available
```

### Least Connections Selection Algorithm

```
class LeastConnectionsStrategy:
    function selectServer(servers):
        selectedServer = null
        minConnections = Integer.MAX_VALUE
        
        for each server in servers:
            if server.isHealthy() AND 
               server.requestCount < minConnections:
                selectedServer = server
                minConnections = server.requestCount
        
        return selectedServer
```

## Usage

### Running the Demo

```bash
# Compile the project
mvn clean compile

# Run the demonstration
mvn exec:java -Dexec.mainClass="com.loadbalancer.LoadBalancerDemo"

# Or build and run JAR
mvn clean package
java -cp target/java-load-balancer-1.0.0.jar com.loadbalancer.LoadBalancerDemo
```

### Running Tests

```bash
# Run all tests
mvn test

# Run specific test
mvn test -Dtest=LoadBalancerTest#testRoundRobinDistribution
```

### Code Example

```java
import com.loadbalancer.*;
import com.loadbalancer.server.BackendServer;
import com.loadbalancer.strategy.RoundRobinStrategy;
import com.loadbalancer.request.Request;

// Create load balancer with round-robin strategy
LoadBalancer lb = new LoadBalancer("MyLB", new RoundRobinStrategy());

// Add backend servers
lb.addServer(new BackendServer("Server-1", "192.168.1.10", 8080));
lb.addServer(new BackendServer("Server-2", "192.168.1.11", 8080));
lb.addServer(new BackendServer("Server-3", "192.168.1.12", 8080));

// Route requests
Request request = new Request("REQ-001", "/api/data", "GET", null);
Response response = lb.routeRequest(request);

// Monitor health
lb.healthCheck();
lb.printStatistics();
```

## Performance Characteristics

### Round-Robin Strategy
- **Best for**: Stateless services, equal server capacity
- **Worst Case**: O(n) when most servers are unhealthy
- **Average Case**: O(1) amortized
- **Cache Efficiency**: Good - sequential access pattern

### Least Connections Strategy
- **Best for**: Long-lived connections, varied processing times
- **Worst Case**: O(n) - must check all servers
- **Average Case**: O(n)
- **Load Distribution**: More balanced for mixed workloads

## Demo Output

The demonstration showcases:

1. **Round-Robin Distribution**: 9 requests distributed across 3 servers (3 each)
2. **Least Connections**: Balanced distribution based on connection count
3. **Fault Tolerance**: Automatic failover when a server is marked unhealthy
4. **Health Checks**: Periodic monitoring with up/down status updates

## Learning Outcomes

This project helps understand:

- ✅ How load balancers distribute traffic
- ✅ Different load balancing algorithms and their trade-offs
- ✅ Fault tolerance and health monitoring
- ✅ Thread-safe concurrent programming
- ✅ Design patterns (Strategy pattern)
- ✅ Atomic operations and thread safety
- ✅ Request routing logic

## Extensibility

Easy to add new strategies:

```java
public class IPHashStrategy implements LoadBalancingStrategy {
    @Override
    public BackendServer selectServer(List<BackendServer> servers) {
        // Implementation using client IP hash
    }

    @Override
    public String getStrategyName() {
        return "IP Hash";
    }
}
```

## Requirements

- Java 11 or higher
- Maven 3.6.0 or higher
- JUnit 5 for testing

## License

MIT License - Educational use
=======
This project implements a basic load balancer using Java.
It forwards incoming client requests to multiple backend servers to distribute load evenly.

The load balancer uses a round-robin strategy to choose the next available server.
It maintains a list of backend servers and routes requests sequentially.

This project helps in understanding:

How load balancers work internally

Request routing logic

Server selection strategies

Basic fault handling when a server is unavailable
>>>>>>> 4609287be232e45e02c8a48290bc91ca8aedfad7
