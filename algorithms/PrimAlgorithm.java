package com.transportation.algorithms;

import com.transportation.model.Graph;
import com.transportation.model.Edge;
import com.transportation.model.MSTResult;
import java.util.*;

public class PrimAlgorithm {

    public MSTResult findMST(Graph graph) {
        long startTime = System.nanoTime();
        int operations = 0;

        if (graph.getVertexCount() == 0) {
            return new MSTResult("Prim", Collections.emptyList(), 0, 0, 0, 0, 0);
        }

        // Check if graph is connected
        if (!graph.isConnected()) {
            long endTime = System.nanoTime();
            return new MSTResult("Prim", Collections.emptyList(), Integer.MAX_VALUE,
                    (endTime - startTime) / 1_000_000, operations,
                    graph.getVertexCount(), graph.getEdgeCount());
        }

        List<Edge> mstEdges = new ArrayList<>();
        Set<String> visited = new HashSet<>();
        PriorityQueue<Edge> minHeap = new PriorityQueue<>();

        // Start with first vertex
        String startVertex = graph.getVertices().get(0);
        visited.add(startVertex);
        operations++;

        // Add all edges from start vertex
        minHeap.addAll(graph.getIncidentEdges(startVertex));
        operations += graph.getIncidentEdges(startVertex).size();

        while (!minHeap.isEmpty() && visited.size() < graph.getVertexCount()) {
            Edge currentEdge = minHeap.poll();
            operations++;

            String nextVertex = findUnvisitedVertex(currentEdge, visited);
            if (nextVertex != null) {
                visited.add(nextVertex);
                mstEdges.add(currentEdge);
                operations++;

                // Add edges from the new vertex to unvisited vertices
                for (Edge edge : graph.getIncidentEdges(nextVertex)) {
                    operations++;
                    if (!visited.contains(edge.getOtherVertex(nextVertex))) {
                        minHeap.add(edge);
                    }
                }
            }
        }

        long endTime = System.nanoTime();
        long executionTimeMs = (endTime - startTime) / 1_000_000;

        int totalCost = mstEdges.stream().mapToInt(Edge::getWeight).sum();

        return new MSTResult("Prim", mstEdges, totalCost, executionTimeMs,
                operations, graph.getVertexCount(), graph.getEdgeCount());
    }

    private String findUnvisitedVertex(Edge edge, Set<String> visited) {
        if (!visited.contains(edge.getSource())) {
            return edge.getSource();
        } else if (!visited.contains(edge.getDestination())) {
            return edge.getDestination();
        }
        return null;
    }
}