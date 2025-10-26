package com.transportation;

import com.transportation.algorithms.PrimAlgorithm;
import com.transportation.model.Edge;
import com.transportation.model.Graph;
import com.transportation.model.MSTResult;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.List;
import java.util.Collections;

class PrimAlgorithmTest {

    @Test
    void testPrimWithSmallGraph() {
        // Create a simple graph
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
        MSTResult result = prim.findMST(graph);

        // Verify MST properties
        assertEquals(3, result.getMstEdgeCount()); // V-1 edges
        assertEquals(6, result.getTotalCost()); // 1+2+3
        assertTrue(result.getExecutionTimeMs() >= 0);
        assertTrue(result.getOperationsCount() > 0);
        assertEquals("Prim", result.getAlgorithm());
    }

    @Test
    void testPrimWithSingleVertex() {
        List<String> vertices = List.of("A");
        List<Edge> edges = List.of();

        Graph graph = Graph.createGraph("test_single", vertices, edges);
        PrimAlgorithm prim = new PrimAlgorithm();
        MSTResult result = prim.findMST(graph);

        assertEquals(0, result.getMstEdgeCount());
        assertEquals(0, result.getTotalCost());
        assertEquals(0, result.getExecutionTimeMs());
    }

    @Test
    void testPrimWithDisconnectedGraph() {
        List<String> vertices = Arrays.asList("A", "B", "C", "D");
        List<Edge> edges = Arrays.asList(
                new Edge("A", "B", 1),
                new Edge("C", "D", 2)
        ); // Disconnected graph

        Graph graph = Graph.createGraph("test_disconnected", vertices, edges);
        PrimAlgorithm prim = new PrimAlgorithm();
        MSTResult result = prim.findMST(graph);

        // Should handle disconnected graph gracefully
        assertFalse(graph.isConnected());
        assertTrue(result.getTotalCost() == Integer.MAX_VALUE ||
                result.getMstEdgeCount() < graph.getVertexCount() - 1);
    }

    @Test
    void testPrimCorrectness() {
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
        PrimAlgorithm prim = new PrimAlgorithm();
        MSTResult result = prim.findMST(graph);

        // Known MST cost: 5 + 4 + 6 = 15
        assertEquals(15, result.getTotalCost());
        assertEquals(3, result.getMstEdgeCount());
    }
}