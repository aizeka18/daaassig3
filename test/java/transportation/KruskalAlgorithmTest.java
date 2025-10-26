package com.transportation;

import com.transportation.algorithms.KruskalAlgorithm;
import com.transportation.algorithms.PrimAlgorithm;
import com.transportation.model.Edge;
import com.transportation.model.Graph;
import com.transportation.model.MSTResult;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.List;

class KruskalAlgorithmTest {

    @Test
    void testKruskalWithSmallGraph() {
        // Same graph as Prim test for consistency
        List<String> vertices = Arrays.asList("A", "B", "C", "D");
        List<Edge> edges = Arrays.asList(
                new Edge("A", "B", 1),
                new Edge("B", "C", 2),
                new Edge("C", "D", 3),
                new Edge("D", "A", 4),
                new Edge("A", "C", 5)
        );

        Graph graph = Graph.createGraph("test_small", vertices, edges);
        KruskalAlgorithm kruskal = new KruskalAlgorithm();
        MSTResult result = kruskal.findMST(graph);

        // Verify MST properties
        assertEquals(3, result.getMstEdgeCount());
        assertEquals(6, result.getTotalCost());
        assertTrue(result.getExecutionTimeMs() >= 0);
        assertTrue(result.getOperationsCount() > 0);
        assertEquals("Kruskal", result.getAlgorithm());
    }

    @Test
    void testKruskalConsistencyWithPrim() {
        List<String> vertices = Arrays.asList("1", "2", "3", "4");
        List<Edge> edges = Arrays.asList(
                new Edge("1", "2", 10),
                new Edge("2", "3", 15),
                new Edge("3", "4", 20),
                new Edge("4", "1", 25),
                new Edge("1", "3", 30)
        );

        Graph graph = Graph.createGraph("test_consistency", vertices, edges);

        PrimAlgorithm prim = new PrimAlgorithm();
        KruskalAlgorithm kruskal = new KruskalAlgorithm();

        MSTResult primResult = prim.findMST(graph);
        MSTResult kruskalResult = kruskal.findMST(graph);

        // Both algorithms should find MST with same cost
        assertEquals(primResult.getTotalCost(), kruskalResult.getTotalCost());
        assertEquals(primResult.getMstEdgeCount(), kruskalResult.getMstEdgeCount());
    }

    @Test
    void testKruskalWithDisconnectedGraph() {
        List<String> vertices = Arrays.asList("A", "B", "C", "D");
        List<Edge> edges = Arrays.asList(
                new Edge("A", "B", 1),
                new Edge("C", "D", 2)
        ); // Disconnected graph

        Graph graph = Graph.createGraph("test_disconnected", vertices, edges);
        KruskalAlgorithm kruskal = new KruskalAlgorithm();
        MSTResult result = kruskal.findMST(graph);

        // Should handle disconnected graph gracefully
        assertFalse(graph.isConnected());
        assertTrue(result.getTotalCost() == Integer.MAX_VALUE ||
                result.getMstEdgeCount() < graph.getVertexCount() - 1);
    }

    @Test
    void testKruskalCorrectness() {
        // Known MST example
        List<String> vertices = Arrays.asList("1", "2", "3", "4");
        List<Edge> edges = Arrays.asList(
                new Edge("1", "2", 10),
                new Edge("1", "3", 6),
                new Edge("1", "4", 5),
                new Edge("2", "4", 15),
                new Edge("3", "4", 4)
        );

        Graph graph = Graph.createGraph("test_known", vertices, edges);
        KruskalAlgorithm kruskal = new KruskalAlgorithm();
        MSTResult result = kruskal.findMST(graph);

        // Known MST cost: 5 + 4 + 6 = 15
        assertEquals(15, result.getTotalCost());
        assertEquals(3, result.getMstEdgeCount());
    }
}