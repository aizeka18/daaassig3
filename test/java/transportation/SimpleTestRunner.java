package com.transportation;

import com.transportation.algorithms.PrimAlgorithm;
import com.transportation.algorithms.KruskalAlgorithm;
import com.transportation.model.Graph;
import com.transportation.model.Edge;
import com.transportation.model.MSTResult;
import java.util.*;

public class SimpleTestRunner {

    private static int passedTests = 0;
    private static int failedTests = 0;

    public static void main(String[] args) {
        System.out.println("=== COMPREHENSIVE MST ALGORITHM TEST SUITE ===");
        System.out.println("Running all tests...\n");

        // Run all test suites
        runPrimAlgorithmTests();
        runKruskalAlgorithmTests();
        runGraphStructureTests();
        runComprehensiveMSTTests();
        runIntegrationTests();

        // Print final summary
        printFinalSummary();

        System.exit(failedTests > 0 ? 1 : 0);
    }

    private static void runPrimAlgorithmTests() {
        System.out.println("=== PRIM ALGORITHM TESTS ===");

        // Test 1: Small connected graph
        test("Prim - Small Connected Graph", () -> {
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

            assertEqual(3, result.getMstEdgeCount(), "MST edge count");
            assertEqual(6, result.getTotalCost(), "Total cost");
            assertTrue(result.getExecutionTimeMs() >= 0, "Non-negative execution time");
            assertTrue(result.getOperationsCount() > 0, "Positive operations count");
            assertTrue(graph.isConnected(), "Graph connectivity");
        });

        // Test 2: Single vertex
        test("Prim - Single Vertex", () -> {
            List<String> vertices = List.of("A");
            List<Edge> edges = List.of();

            Graph graph = Graph.createGraph("test_single", vertices, edges);
            PrimAlgorithm prim = new PrimAlgorithm();
            MSTResult result = prim.findMST(graph);

            assertEqual(0, result.getTotalCost(), "Zero cost for single vertex");
            assertEqual(0, result.getMstEdgeCount(), "Zero edges for single vertex");
        });

        // Test 3: Disconnected graph
        test("Prim - Disconnected Graph", () -> {
            List<String> vertices = Arrays.asList("A", "B", "C", "D");
            List<Edge> edges = Arrays.asList(
                    new Edge("A", "B", 1),
                    new Edge("C", "D", 2)
            );

            Graph graph = Graph.createGraph("test_disconnected", vertices, edges);
            PrimAlgorithm prim = new PrimAlgorithm();
            MSTResult result = prim.findMST(graph);

            assertFalse(graph.isConnected(), "Graph should be disconnected");
            assertTrue(result.getTotalCost() == Integer.MAX_VALUE ||
                            result.getMstEdgeCount() < graph.getVertexCount() - 1,
                    "Should handle disconnected graph gracefully");
        });

        // Test 4: Known MST cost
        test("Prim - Known MST Cost", () -> {
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

            assertEqual(15, result.getTotalCost(), "Known MST cost");
            assertEqual(3, result.getMstEdgeCount(), "V-1 edges");
        });

        System.out.println();
    }

    private static void runKruskalAlgorithmTests() {
        System.out.println("=== KRUSKAL ALGORITHM TESTS ===");

        // Test 1: Small connected graph
        test("Kruskal - Small Connected Graph", () -> {
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

            assertEqual(3, result.getMstEdgeCount(), "MST edge count");
            assertEqual(6, result.getTotalCost(), "Total cost");
            assertTrue(result.getExecutionTimeMs() >= 0, "Non-negative execution time");
            assertTrue(result.getOperationsCount() > 0, "Positive operations count");
        });

        // Test 2: Consistency with Prim
        test("Kruskal - Consistency with Prim", () -> {
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

            assertEqual(primResult.getTotalCost(), kruskalResult.getTotalCost(), "Same MST cost");
            assertEqual(primResult.getMstEdgeCount(), kruskalResult.getMstEdgeCount(), "Same edge count");
        });

        // Test 3: Disconnected graph
        test("Kruskal - Disconnected Graph", () -> {
            List<String> vertices = Arrays.asList("A", "B", "C", "D");
            List<Edge> edges = Arrays.asList(
                    new Edge("A", "B", 1),
                    new Edge("C", "D", 2)
            );

            Graph graph = Graph.createGraph("test_disconnected", vertices, edges);
            KruskalAlgorithm kruskal = new KruskalAlgorithm();
            MSTResult result = kruskal.findMST(graph);

            assertFalse(graph.isConnected(), "Graph should be disconnected");
            assertTrue(result.getTotalCost() == Integer.MAX_VALUE ||
                            result.getMstEdgeCount() < graph.getVertexCount() - 1,
                    "Should handle disconnected graph gracefully");
        });

        System.out.println();
    }

    private static void runGraphStructureTests() {
        System.out.println("=== GRAPH STRUCTURE TESTS ===");

        // Test 1: Custom graph operations
        test("Graph - Custom Structure Operations", () -> {
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

            // Test properties
            assertEqual(4, graph.getVertexCount(), "Vertex count");
            assertEqual(5, graph.getEdgeCount(), "Edge count");
            assertTrue(graph.isConnected(), "Graph connectivity");
            assertTrue(graph.containsEdge("A", "B"), "Edge existence");
            assertTrue(graph.containsEdge("B", "A"), "Undirected edge");

            // Test incident edges
            List<Edge> incidentToA = graph.getIncidentEdges("A");
            assertEqual(3, incidentToA.size(), "Incident edges count");

            // Test neighbors
            List<String> neighborsOfA = graph.getNeighbors("A");
            assertEqual(3, neighborsOfA.size(), "Neighbors count");
            assertTrue(neighborsOfA.contains("B"), "Neighbor B");
            assertTrue(neighborsOfA.contains("C"), "Neighbor C");
            assertTrue(neighborsOfA.contains("D"), "Neighbor D");

            // Test density
            double expectedDensity = 5.0 / (4 * 3 / 2);
            assertEqual(expectedDensity, graph.getDensity(), 0.001, "Graph density");
        });

        // Test 2: Graph connectivity
        test("Graph - Connectivity Check", () -> {
            // Connected graph
            Graph connectedGraph = Graph.createGraph("connected",
                    Arrays.asList("A", "B", "C"),
                    Arrays.asList(new Edge("A", "B", 1), new Edge("B", "C", 2))
            );
            assertTrue(connectedGraph.isConnected(), "Connected graph");

            // Disconnected graph
            Graph disconnectedGraph = Graph.createGraph("disconnected",
                    Arrays.asList("A", "B", "C", "D"),
                    Arrays.asList(new Edge("A", "B", 1), new Edge("C", "D", 2))
            );
            assertFalse(disconnectedGraph.isConnected(), "Disconnected graph");
            assertEqual(2, disconnectedGraph.getConnectedComponents().size(), "Connected components");
        });

        System.out.println();
    }

    private static void runComprehensiveMSTTests() {
        System.out.println("=== COMPREHENSIVE MST TESTS ===");

        // Test 1: Acyclic property
        test("MST - Acyclic Property", () -> {
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
            assertEqual(vertices.size() - 1, primResult.getMstEdgeCount(), "Prim MST edges");
            assertEqual(vertices.size() - 1, kruskalResult.getMstEdgeCount(), "Kruskal MST edges");
        });

        // Test 2: Connects all vertices
        test("MST - Connects All Vertices", () -> {
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
            assertTrue(connectsAllVertices(primResult.getMstEdges(), vertices), "Prim connects all");
            assertTrue(connectsAllVertices(kruskalResult.getMstEdges(), vertices), "Kruskal connects all");
        });

        // Test 3: Reproducibility
        test("MST - Result Reproducibility", () -> {
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
            assertEqual(primResult1.getTotalCost(), primResult2.getTotalCost(), "Prim reproducibility");
            assertEqual(kruskalResult1.getTotalCost(), kruskalResult2.getTotalCost(), "Kruskal reproducibility");
        });

        System.out.println();
    }

    private static void runIntegrationTests() {
        System.out.println("=== INTEGRATION TESTS ===");

        // Test: Both algorithms on same complex graph
        test("Integration - Complex Graph", () -> {
            List<String> vertices = Arrays.asList("1", "2", "3", "4", "5", "6");
            List<Edge> edges = Arrays.asList(
                    new Edge("1", "2", 4), new Edge("1", "3", 4), new Edge("2", "3", 2),
                    new Edge("3", "4", 3), new Edge("3", "5", 2), new Edge("3", "6", 4),
                    new Edge("4", "6", 3), new Edge("5", "6", 3)
            );

            Graph graph = Graph.createGraph("integration_test", vertices, edges);
            PrimAlgorithm prim = new PrimAlgorithm();
            KruskalAlgorithm kruskal = new KruskalAlgorithm();

            MSTResult primResult = prim.findMST(graph);
            MSTResult kruskalResult = kruskal.findMST(graph);

            // Both should find MST with same cost
            assertEqual(primResult.getTotalCost(), kruskalResult.getTotalCost(), "Same MST cost");
            assertEqual(vertices.size() - 1, primResult.getMstEdgeCount(), "Correct edge count");
            assertTrue(primResult.getTotalCost() > 0, "Positive MST cost");
            assertTrue(graph.isConnected(), "Graph is connected");
        });

        System.out.println();
    }

    // ===== HELPER METHODS =====

    private static void test(String testName, Runnable testCode) {
        try {
            testCode.run();
            System.out.println("✓ PASS: " + testName);
            passedTests++;
        } catch (AssertionError e) {
            System.out.println("✗ FAIL: " + testName + " - " + e.getMessage());
            failedTests++;
        } catch (Exception e) {
            System.out.println("✗ ERROR: " + testName + " - " + e.getMessage());
            failedTests++;
        }
    }

    private static void assertEqual(int expected, int actual, String message) {
        if (expected != actual) {
            throw new AssertionError(message + " - Expected: " + expected + ", Got: " + actual);
        }
    }

    private static void assertEqual(double expected, double actual, double delta, String message) {
        if (Math.abs(expected - actual) > delta) {
            throw new AssertionError(message + " - Expected: " + expected + ", Got: " + actual);
        }
    }

    private static void assertTrue(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message + " - Condition was false");
        }
    }

    private static void assertFalse(boolean condition, String message) {
        if (condition) {
            throw new AssertionError(message + " - Condition was true");
        }
    }

    private static boolean connectsAllVertices(List<Edge> mstEdges, List<String> vertices) {
        if (mstEdges.isEmpty() && vertices.size() > 1) return false;
        if (vertices.size() <= 1) return true;

        Set<String> connected = new HashSet<>();
        for (Edge edge : mstEdges) {
            connected.add(edge.getSource());
            connected.add(edge.getDestination());
        }
        return connected.containsAll(vertices);
    }

    private static void printFinalSummary() {
        System.out.println("=== TEST SUMMARY ===");
        System.out.println("Total Tests: " + (passedTests + failedTests));
        System.out.println("Passed: " + passedTests);
        System.out.println("Failed: " + failedTests);
        System.out.println("Success Rate: " +
                String.format("%.1f%%", (double) passedTests / (passedTests + failedTests) * 100));

        if (failedTests == 0) {
            System.out.println("\n🎉 ALL TESTS PASSED! 🎉");
        } else {
            System.out.println("\n❌ SOME TESTS FAILED!");
        }
    }
}