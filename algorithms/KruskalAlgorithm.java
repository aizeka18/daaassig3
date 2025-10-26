package com.transportation.algorithms;

import com.transportation.model.Graph;
import com.transportation.model.Edge;
import com.transportation.model.MSTResult;
import java.util.*;

public class KruskalAlgorithm {

    public MSTResult findMST(Graph graph) {
        long startTime = System.nanoTime();
        int operations = 0;

        if (graph.getVertexCount() == 0) {
            return new MSTResult("Kruskal", Collections.emptyList(), 0, 0, 0, 0, 0);
        }

        List<Edge> mstEdges = new ArrayList<>();
        List<Edge> sortedEdges = new ArrayList<>(graph.getEdges());
        Collections.sort(sortedEdges);
        operations += sortedEdges.size(); // Sort operations

        UnionFind uf = new UnionFind(graph.getVertices());
        operations += graph.getVertexCount();

        for (Edge edge : sortedEdges) {
            operations++;
            if (mstEdges.size() == graph.getVertexCount() - 1) {
                break;
            }

            String root1 = uf.find(edge.getSource());
            String root2 = uf.find(edge.getDestination());
            operations += 2;

            if (!root1.equals(root2)) {
                mstEdges.add(edge);
                uf.union(edge.getSource(), edge.getDestination());
                operations++;
            }
        }

        long endTime = System.nanoTime();
        long executionTimeMs = (endTime - startTime) / 1_000_000;

        // Check if we found a complete MST
        int totalCost = Integer.MAX_VALUE;
        if (mstEdges.size() == graph.getVertexCount() - 1) {
            totalCost = mstEdges.stream().mapToInt(Edge::getWeight).sum();
        } else {
            mstEdges = Collections.emptyList(); // No MST for disconnected graph
        }

        return new MSTResult("Kruskal", mstEdges, totalCost, executionTimeMs,
                operations, graph.getVertexCount(), graph.getEdgeCount());
    }

    // Union-Find implementation optimized for String vertices
    private static class UnionFind {
        private final Map<String, String> parent;
        private final Map<String, Integer> rank;

        public UnionFind(List<String> vertices) {
            parent = new HashMap<>();
            rank = new HashMap<>();

            for (String vertex : vertices) {
                parent.put(vertex, vertex);
                rank.put(vertex, 0);
            }
        }

        public String find(String vertex) {
            if (!parent.get(vertex).equals(vertex)) {
                parent.put(vertex, find(parent.get(vertex))); // Path compression
            }
            return parent.get(vertex);
        }

        public void union(String vertex1, String vertex2) {
            String root1 = find(vertex1);
            String root2 = find(vertex2);

            if (!root1.equals(root2)) {
                // Union by rank
                if (rank.get(root1) < rank.get(root2)) {
                    parent.put(root1, root2);
                } else if (rank.get(root1) > rank.get(root2)) {
                    parent.put(root2, root1);
                } else {
                    parent.put(root2, root1);
                    rank.put(root1, rank.get(root1) + 1);
                }
            }
        }
    }
}