package com.loadbalancer;

import com.loadbalancer.request.Request;
import com.loadbalancer.request.Response;
import com.loadbalancer.server.BackendServer;
import com.loadbalancer.strategy.LeastConnectionsStrategy;
import com.loadbalancer.strategy.RoundRobinStrategy;


public class LoadBalancerDemo {

    public static void main(String[] args) {
        System.out.println("╔════════════════════════════════════════════════════════════╗");
        System.out.println("║        Java Load Balancer - Algorithm & Logic Demo         ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝\n");

        // Demo 1: Round-Robin Strategy
        demoRoundRobin();

        // Demo 2: Least Connections Strategy
        demoLeastConnections();

        // Demo 3: Fault Tolerance
        demoFaultTolerance();

        // Demo 4: Health Check
        demoHealthCheck();
    }

    
    private static void demoRoundRobin() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("DEMO 1: Round-Robin Load Balancing");
        System.out.println("=".repeat(60));
        System.out.println("\nAlgorithm: Distributes requests sequentially across servers");
        System.out.println("Formula: selectedServer = servers[counter++ % serverCount]\n");

        LoadBalancer lb = new LoadBalancer("LB-RoundRobin", new RoundRobinStrategy());

        // Add backend servers
        lb.addServer(new BackendServer("Server-A", "192.168.1.10", 8080));
        lb.addServer(new BackendServer("Server-B", "192.168.1.11", 8080));
        lb.addServer(new BackendServer("Server-C", "192.168.1.12", 8080));

        // Route requests
        System.out.println("Routing 9 requests:");
        for (int i = 1; i <= 9; i++) {
            Request request = new Request("REQ-" + i, "/api/data", "GET", null);
            Response response = lb.routeRequest(request);
            if (response != null) {
                System.out.println("  ↳ " + response);
            }
        }

        lb.printStatistics();
    }

    
    private static void demoLeastConnections() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("DEMO 2: Least Connections Load Balancing");
        System.out.println("=".repeat(60));
        System.out.println("\nAlgorithm: Routes to server with minimum active connections");
        System.out.println("Logic: Finds server with requestCount == MIN(all servers)\n");

        LoadBalancer lb = new LoadBalancer("LB-LeastConnections", new LeastConnectionsStrategy());

        // Add backend servers
        lb.addServer(new BackendServer("Server-X", "192.168.2.10", 8080));
        lb.addServer(new BackendServer("Server-Y", "192.168.2.11", 8080));
        lb.addServer(new BackendServer("Server-Z", "192.168.2.12", 8080));

        // Route requests
        System.out.println("Routing 9 requests:");
        for (int i = 1; i <= 9; i++) {
            Request request = new Request("REQ-LC-" + i, "/api/data", "POST", null);
            Response response = lb.routeRequest(request);
            if (response != null) {
                System.out.println("  ↳ " + response);
            }
        }

        lb.printStatistics();
    }

    private static void demoFaultTolerance() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("DEMO 3: Fault Tolerance & Server Failure Handling");
        System.out.println("=".repeat(60));
        System.out.println("\nScenario: Server failure with automatic failover\n");

        LoadBalancer lb = new LoadBalancer("LB-FaultTolerance", new RoundRobinStrategy());

        BackendServer server1 = new BackendServer("FT-Server-1", "192.168.3.10", 8080);
        BackendServer server2 = new BackendServer("FT-Server-2", "192.168.3.11", 8080);
        BackendServer server3 = new BackendServer("FT-Server-3", "192.168.3.12", 8080);

        lb.addServer(server1);
        lb.addServer(server2);
        lb.addServer(server3);

        
        System.out.println("Step 1: All servers healthy - routing 3 requests");
        for (int i = 1; i <= 3; i++) {
            Request request = new Request("FT-REQ-" + i, "/api/health", "GET", null);
            lb.routeRequest(request);
        }

 
        System.out.println("\nStep 2: Simulating Server-2 failure...");
        server2.setHealthy(false);
        System.out.println(" FT-Server-2 marked as UNHEALTHY\n");

        System.out.println("Step 3: Routing 3 requests with server-2 down");
        for (int i = 4; i <= 6; i++) {
            Request request = new Request("FT-REQ-" + i, "/api/health", "GET", null);
            lb.routeRequest(request);
        }

        lb.printStatistics();
    }

   
    private static void demoHealthCheck() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("DEMO 4: Health Check Mechanism");
        System.out.println("=".repeat(60));
        System.out.println("\nFeature: Periodic health monitoring of backend servers\n");

        LoadBalancer lb = new LoadBalancer("LB-HealthCheck", new RoundRobinStrategy());

        lb.addServer(new BackendServer("HC-Server-1", "192.168.4.10", 8080));
        lb.addServer(new BackendServer("HC-Server-2", "192.168.4.11", 8080));
        lb.addServer(new BackendServer("HC-Server-3", "192.168.4.12", 8080));

        for (int i = 1; i <= 2; i++) {
            lb.healthCheck();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        lb.printStatistics();
    }
}
