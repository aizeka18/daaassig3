package com.transportation;

import com.transportation.model.Graph;
import com.transportation.model.Edge;
import com.transportation.algorithms.PrimAlgorithm;
import com.transportation.algorithms.KruskalAlgorithm;
import com.transportation.model.MSTResult;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.List;

public class GraphDemoTest {

    @Test
    void testCustomGraphStructure() {
        // Create graph using custom structure
        Graph graph = new Graph("demo_graph");

        // Add vertices
        graph.addVertex("A");
        graph.addVertex("B");
        graph.addVertex("C");
        graph.addVertex("D");

        // Add edges
        graph.addEdge(new Edge("A", "B", 1));
        graph.addEdge(new Edge("B", "C", 2));
        graph.addEdge(new Edge("C", "D", 3));
        graph.addEdge(new Edge("D", "A", 4));
        graph.addEdge(new Edge("A", "C", 5));

        // Test graph properties
        assertEquals(4, graph.getVertexCount());
        assertEquals(5, graph.getEdgeCount());
        assertTrue(graph.isConnected());
        assertTrue(graph.containsEdge("A", "B"));
        assertTrue(graph.containsEdge("B", "A")); // Undirected graph

        // Test incident edges
        List<Edge> incidentToA = graph.getIncidentEdges("A");
        assertEquals(3, incidentToA.size());

        // Test neighbors
        List<String> neighborsOfA = graph.getNeighbors("A");
        assertEquals(3, neighborsOfA.size());
        assertTrue(neighborsOfA.contains("B"));
        assertTrue(neighborsOfA.contains("C"));
        assertTrue(neighborsOfA.contains("D"));

        // Test graph density
        double expectedDensity = 5.0 / (4 * 3 / 2); // edges / max_edges
        assertEquals(expectedDensity, graph.getDensity(), 0.001);

        System.out.println("Custom Graph Structure Test Passed!");
        System.out.println(graph.getDetailedInfo());
    }

    @Test
    void testMSTWithCustomGraph() {
        // Create a test graph
        List<String> vertices = Arrays.asList("A", "B", "C", "D");
        List<Edge> edges = Arrays.asList(
                new Edge("A", "B", 1),
                new Edge("B", "C", 2),
                new Edge("C", "D", 3),
                new Edge("D", "A", 4),
                new Edge("A", "C", 5)
        );

        Graph graph = Graph.createGraph("mst_test", vertices, edges);

        // Run both algorithms
        PrimAlgorithm prim = new PrimAlgorithm();
        KruskalAlgorithm kruskal = new KruskalAlgorithm();

        MSTResult primResult = prim.findMST(graph);
        MSTResult kruskalResult = kruskal.findMST(graph);

        // Verify results
        assertEquals(primResult.getTotalCost(), kruskalResult.getTotalCost());
        assertEquals(6, primResult.getTotalCost()); // 1 + 2 + 3
        assertEquals(3, primResult.getMstEdgeCount()); // V-1 edges

        // Verify graph properties are correctly passed
        assertEquals(4, primResult.getVertexCount());
        assertEquals(5, primResult.getEdgeCount());
        assertEquals(4, kruskalResult.getVertexCount());
        assertEquals(5, kruskalResult.getEdgeCount());

        System.out.println("MST with Custom Graph Test Passed!");
        System.out.println("Prim Result: " + primResult);
        System.out.println("Kruskal Result: " + kruskalResult);
    }

    @Test
    void testGraphConnectivity() {
        // Test connected graph
        Graph connectedGraph = Graph.createGraph("connected",
                Arrays.asList("A", "B", "C"),
                Arrays.asList(new Edge("A", "B", 1), new Edge("B", "C", 2))
        );
        assertTrue(connectedGraph.isConnected());

        // Test disconnected graph
        Graph disconnectedGraph = Graph.createGraph("disconnected",
                Arrays.asList("A", "B", "C", "D"),
                Arrays.asList(new Edge("A", "B", 1), new Edge("C", "D", 2))
        );
        assertFalse(disconnectedGraph.isConnected());

        // Test connected components
        assertEquals(2, disconnectedGraph.getConnectedComponents().size());
    }
}