package com.transportation.utils;

import com.transportation.model.Graph;
import com.transportation.model.Edge;
import java.util.*;

public class GraphGenerator {

    public enum GraphSize {
        SMALL(30, 0.3, 0.6, 5),
        MEDIUM(300, 0.2, 0.4, 10),
        LARGE(1000, 0.1, 0.3, 10),
        EXTRA_LARGE_1(1300, 0.05, 0.1, 1),
        EXTRA_LARGE_2(1600, 0.04, 0.08, 1),
        EXTRA_LARGE_3(2000, 0.03, 0.06, 1);

        private final int nodes;
        private final double minDensity;
        private final double maxDensity;
        private final int count;

        GraphSize(int nodes, double minDensity, double maxDensity, int count) {
            this.nodes = nodes;
            this.minDensity = minDensity;
            this.maxDensity = maxDensity;
            this.count = count;
        }

        public int getNodes() { return nodes; }
        public double getMinDensity() { return minDensity; }
        public double getMaxDensity() { return maxDensity; }
        public int getCount() { return count; }
    }

    public static Graph generateGraph(String id, int vertexCount, double density) {
        // CREATE CUSTOM GRAPH INSTANCE
        Graph graph = new Graph(id);
        Random random = new Random();

        // Generate vertices using custom graph methods
        for (int i = 1; i <= vertexCount; i++) {
            graph.addVertex("D" + i);
        }

        // Calculate target edges based on density
        int maxPossibleEdges = vertexCount * (vertexCount - 1) / 2;
        int targetEdges = Math.max(vertexCount - 1, (int) (maxPossibleEdges * density));

        // Ensure connectivity with a minimum spanning tree using custom graph
        for (int i = 1; i < vertexCount; i++) {
            int weight = random.nextInt(50) + 1;
            Edge edge = new Edge("D" + i, "D" + (i + 1), weight);
            graph.addEdge(edge);
        }

        // Add additional random edges to achieve desired density using custom graph
        while (graph.getEdgeCount() < targetEdges) {
            int v1 = random.nextInt(vertexCount) + 1;
            int v2 = random.nextInt(vertexCount) + 1;

            if (v1 != v2) {
                String source = "D" + v1;
                String destination = "D" + v2;
                Edge edge = new Edge(source, destination, random.nextInt(100) + 1);

                // Use custom graph method to check and add edge
                if (!graph.containsEdge(source, destination)) {
                    graph.addEdge(edge);
                }
            }
        }

        return graph;
    }

    public static List<Graph> generateAllTestGraphs() {
        List<Graph> graphs = new ArrayList<>();
        Random random = new Random(42); // Fixed seed for reproducibility

        // Generate exactly 5 small graphs (30 nodes)
        for (int i = 1; i <= 5; i++) {
            double density = GraphSize.SMALL.getMinDensity() +
                    random.nextDouble() * (GraphSize.SMALL.getMaxDensity() - GraphSize.SMALL.getMinDensity());
            graphs.add(generateGraph(
                    String.format("small_%d", i),
                    30, // Exactly 30 nodes
                    density
            ));
        }

        // Generate exactly 10 medium graphs (300 nodes)
        for (int i = 1; i <= 10; i++) {
            double density = GraphSize.MEDIUM.getMinDensity() +
                    random.nextDouble() * (GraphSize.MEDIUM.getMaxDensity() - GraphSize.MEDIUM.getMinDensity());
            graphs.add(generateGraph(
                    String.format("medium_%d", i),
                    300, // Exactly 300 nodes
                    density
            ));
        }

        // Generate exactly 10 large graphs (1000 nodes)
        for (int i = 1; i <= 10; i++) {
            double density = GraphSize.LARGE.getMinDensity() +
                    random.nextDouble() * (GraphSize.LARGE.getMaxDensity() - GraphSize.LARGE.getMinDensity());
            graphs.add(generateGraph(
                    String.format("large_%d", i),
                    1000, // Exactly 1000 nodes
                    density
            ));
        }

        // Generate exactly 3 extra large graphs
        int[] extraLargeSizes = {1300, 1600, 2000};
        for (int i = 0; i < extraLargeSizes.length; i++) {
            double density = 0.05 + random.nextDouble() * 0.05; // 5-10% density
            graphs.add(generateGraph(
                    String.format("xlarge_%d", i + 1),
                    extraLargeSizes[i],
                    density
            ));
        }

        // Verify we have exactly 28 graphs total
        System.out.println("Generated " + graphs.size() + " test graphs:");
        System.out.println(" - 5 small graphs (30 nodes)");
        System.out.println(" - 10 medium graphs (300 nodes)");
        System.out.println(" - 10 large graphs (1000 nodes)");
        System.out.println(" - 3 extra large graphs (1300, 1600, 2000 nodes)");

        return graphs;
    }
}