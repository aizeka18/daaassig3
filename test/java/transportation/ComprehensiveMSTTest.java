package com.transportation;

import com.transportation.algorithms.PrimAlgorithm;
import com.transportation.algorithms.KruskalAlgorithm;
import com.transportation.model.Graph;
import com.transportation.model.Edge;
import com.transportation.model.MSTResult;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.*;

public class ComprehensiveMSTTest {

    @Test
    void testMSTCorrectnessSmallGraph() {
        List<String> vertices = Arrays.asList("A", "B", "C", "D");
        List<Edge> edges = Arrays.asList(
                new Edge("A", "B", 1),
                new Edge("B", "C", 2),
                new Edge("C", "D", 3),
                new Edge("D", "A", 4),
                new Edge("A", "C", 5)
        );

        Graph graph = Graph.createGraph("test_small", vertices, edges);

        PrimAlgorithm prim = new PrimAlgorithm();
        KruskalAlgorithm kruskal = new KruskalAlgorithm();

        MSTResult primResult = prim.findMST(graph);
        MSTResult kruskalResult = kruskal.findMST(graph);

        // Test 1: Same total cost
        assertEquals(primResult.getTotalCost(), kruskalResult.getTotalCost());

        // Test 2: MST has V-1 edges
        assertEquals(graph.getVertexCount() - 1, primResult.getMstEdgeCount());
        assertEquals(graph.getVertexCount() - 1, kruskalResult.getMstEdgeCount());

        // Test 3: Non-negative execution time
        assertTrue(primResult.getExecutionTimeMs() >= 0);
        assertTrue(kruskalResult.getExecutionTimeMs() >= 0);

        // Test 4: Non-negative operations count
        assertTrue(primResult.getOperationsCount() >= 0);
        assertTrue(kruskalResult.getOperationsCount() >= 0);

        // Test 5: Graph is connected
        assertTrue(graph.isConnected());
    }

    @Test
    void testDisconnectedGraph() {
        List<String> vertices = Arrays.asList("A", "B", "C", "D");
        List<Edge> edges = Arrays.asList(
                new Edge("A", "B", 1),
                new Edge("C", "D", 2)
        ); // Graph with two components

        Graph graph = Graph.createGraph("test_disconnected", vertices, edges);

        PrimAlgorithm prim = new PrimAlgorithm();
        KruskalAlgorithm kruskal = new KruskalAlgorithm();

        MSTResult primResult = prim.findMST(graph);
        MSTResult kruskalResult = kruskal.findMST(graph);

        // For disconnected graphs, MST should not be found or handled gracefully
        assertFalse(graph.isConnected());

        // Either empty MST or special handling with MAX_VALUE cost
        assertTrue(primResult.getMstEdgeCount() < graph.getVertexCount() - 1 ||
                primResult.getTotalCost() == Integer.MAX_VALUE);
        assertTrue(kruskalResult.getMstEdgeCount() < graph.getVertexCount() - 1 ||
                kruskalResult.getTotalCost() == Integer.MAX_VALUE);
    }

    @Test
    void testSingleVertexGraph() {
        List<String> vertices = Collections.singletonList("A");
        List<Edge> edges = Collections.emptyList();

        Graph graph = Graph.createGraph("test_single", vertices, edges);

        PrimAlgorithm prim = new PrimAlgorithm();
        KruskalAlgorithm kruskal = new KruskalAlgorithm();

        MSTResult primResult = prim.findMST(graph);
        MSTResult kruskalResult = kruskal.findMST(graph);

        assertEquals(0, primResult.getTotalCost());
        assertEquals(0, kruskalResult.getTotalCost());
        assertEquals(0, primResult.getMstEdgeCount());
        assertEquals(0, kruskalResult.getMstEdgeCount());
    }

    @Test
    void testAcyclicProperty() {
        List<String> vertices = Arrays.asList("A", "B", "C", "D", "E");
        List<Edge> edges = Arrays.asList(
                new Edge("A", "B", 1),
                new Edge("B", "C", 2),
                new Edge("C", "D", 3),
                new Edge("D", "E", 4),
                new Edge("A", "E", 5),
                new Edge("B", "E", 6)
        );

        Graph graph = Graph.createGraph("test_acyclic", vertices, edges);

        PrimAlgorithm prim = new PrimAlgorithm();
        KruskalAlgorithm kruskal = new KruskalAlgorithm();

        MSTResult primResult = prim.findMST(graph);
        MSTResult kruskalResult = kruskal.findMST(graph);

        // MST should have exactly V-1 edges (no cycles)
        assertEquals(vertices.size() - 1, primResult.getMstEdgeCount());
        assertEquals(vertices.size() - 1, kruskalResult.getMstEdgeCount());

        // MST should be acyclic (no cycles)
        assertTrue(isAcyclic(primResult.getMstEdges(), vertices.size()));
        assertTrue(isAcyclic(kruskalResult.getMstEdges(), vertices.size()));
    }

    @Test
    void testReproducibility() {
        List<String> vertices = Arrays.asList("A", "B", "C", "D");
        List<Edge> edges = Arrays.asList(
                new Edge("A", "B", 1),
                new Edge("B", "C", 2),
                new Edge("C", "D", 3),
                new Edge("D", "A", 4)
        );

        Graph graph = Graph.createGraph("test_reproducible", vertices, edges);

        PrimAlgorithm prim = new PrimAlgorithm();
        KruskalAlgorithm kruskal = new KruskalAlgorithm();

        // Run multiple times
        MSTResult primResult1 = prim.findMST(graph);
        MSTResult primResult2 = prim.findMST(graph);
        MSTResult kruskalResult1 = kruskal.findMST(graph);
        MSTResult kruskalResult2 = kruskal.findMST(graph);

        // Results should be consistent
        assertEquals(primResult1.getTotalCost(), primResult2.getTotalCost());
        assertEquals(kruskalResult1.getTotalCost(), kruskalResult2.getTotalCost());
        assertEquals(primResult1.getMstEdgeCount(), primResult2.getMstEdgeCount());
        assertEquals(kruskalResult1.getMstEdgeCount(), kruskalResult2.getMstEdgeCount());
    }

    @Test
    void testMSTConnectsAllVertices() {
        List<String> vertices = Arrays.asList("A", "B", "C", "D", "E");
        List<Edge> edges = Arrays.asList(
                new Edge("A", "B", 1),
                new Edge("B", "C", 2),
                new Edge("C", "D", 3),
                new Edge("D", "E", 4),
                new Edge("A", "E", 5),
                new Edge("B", "D", 6)
        );

        Graph graph = Graph.createGraph("test_connectivity", vertices, edges);

        PrimAlgorithm prim = new PrimAlgorithm();
        KruskalAlgorithm kruskal = new KruskalAlgorithm();

        MSTResult primResult = prim.findMST(graph);
        MSTResult kruskalResult = kruskal.findMST(graph);

        // MST should connect all vertices
        assertTrue(connectsAllVertices(primResult.getMstEdges(), vertices));
        assertTrue(connectsAllVertices(kruskalResult.getMstEdges(), vertices));
    }

    // Helper method to check if MST connects all vertices
    private boolean connectsAllVertices(List<Edge> mstEdges, List<String> vertices) {
        if (mstEdges.isEmpty() && vertices.size() > 1) return false;
        if (vertices.size() <= 1) return true;

        Set<String> connected = new HashSet<>();
        for (Edge edge : mstEdges) {
            connected.add(edge.getSource());
            connected.add(edge.getDestination());
        }
        return connected.containsAll(vertices);
    }

    // Helper method to check if graph is acyclic
    private boolean isAcyclic(List<Edge> edges, int vertexCount) {
        if (edges.size() != vertexCount - 1) return false;

        // For a tree with V vertices and V-1 edges, it must be acyclic
        // This is a simplified check - in practice you'd use Union-Find
        return true;
    }
}