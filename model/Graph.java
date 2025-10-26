package com.transportation.model;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Custom Graph data structure representing the city transportation network
 * Supports both directed and undirected graphs with comprehensive operations
 */
public class Graph {
    private final String id;
    private final Map<String, Vertex> vertices;
    private final Set<Edge> edges;
    private final boolean directed;

    // Inner class for vertex representation
    private static class Vertex {
        String id;
        Map<String, Edge> adjacentEdges;

        Vertex(String id) {
            this.id = id;
            this.adjacentEdges = new HashMap<>();
        }
    }

    public Graph(String id) {
        this(id, false);
    }

    public Graph(String id, boolean directed) {
        this.id = id;
        this.vertices = new HashMap<>();
        this.edges = new HashSet<>();
        this.directed = directed;
    }

    /**
     * Factory method to create graph from vertex and edge lists
     */
    public static Graph createGraph(String id, List<String> vertexList, List<Edge> edgeList) {
        Graph graph = new Graph(id);

        // Add all vertices
        for (String vertex : vertexList) {
            graph.addVertex(vertex);
        }

        // Add all edges
        for (Edge edge : edgeList) {
            graph.addEdge(edge);
        }

        return graph;
    }

    // Vertex operations
    public void addVertex(String vertexId) {
        vertices.putIfAbsent(vertexId, new Vertex(vertexId));
    }

    public boolean containsVertex(String vertexId) {
        return vertices.containsKey(vertexId);
    }

    public List<String> getVertices() {
        return new ArrayList<>(vertices.keySet());
    }

    public int getVertexCount() {
        return vertices.size();
    }

    // Edge operations
    public void addEdge(Edge edge) {
        if (!containsVertex(edge.getSource()) || !containsVertex(edge.getDestination())) {
            throw new IllegalArgumentException("Both vertices must exist in the graph");
        }

        edges.add(edge);

        // Update adjacency lists
        Vertex sourceVertex = vertices.get(edge.getSource());
        sourceVertex.adjacentEdges.put(edge.getDestination(), edge);

        if (!directed) {
            Vertex destVertex = vertices.get(edge.getDestination());
            destVertex.adjacentEdges.put(edge.getSource(), edge);
        }
    }

    public boolean containsEdge(String source, String destination) {
        Vertex vertex = vertices.get(source);
        return vertex != null && vertex.adjacentEdges.containsKey(destination);
    }

    public List<Edge> getEdges() {
        return new ArrayList<>(edges);
    }

    public int getEdgeCount() {
        return edges.size();
    }

    /**
     * Gets all edges incident to a vertex
     */
    public List<Edge> getIncidentEdges(String vertexId) {
        Vertex vertex = vertices.get(vertexId);
        if (vertex == null) return Collections.emptyList();

        return new ArrayList<>(vertex.adjacentEdges.values());
    }

    /**
     * Gets neighbors of a vertex
     */
    public List<String> getNeighbors(String vertexId) {
        Vertex vertex = vertices.get(vertexId);
        if (vertex == null) return Collections.emptyList();

        return new ArrayList<>(vertex.adjacentEdges.keySet());
    }

    /**
     * Gets the degree of a vertex (number of incident edges)
     */
    public int getDegree(String vertexId) {
        Vertex vertex = vertices.get(vertexId);
        return vertex != null ? vertex.adjacentEdges.size() : 0;
    }

    /**
     * Checks if graph is connected using BFS
     */
    public boolean isConnected() {
        if (vertices.isEmpty()) return true;

        Set<String> visited = new HashSet<>();
        Queue<String> queue = new LinkedList<>();

        String startVertex = vertices.keySet().iterator().next();
        queue.offer(startVertex);
        visited.add(startVertex);

        while (!queue.isEmpty()) {
            String current = queue.poll();
            for (String neighbor : getNeighbors(current)) {
                if (!visited.contains(neighbor)) {
                    visited.add(neighbor);
                    queue.offer(neighbor);
                }
            }
        }

        return visited.size() == vertices.size();
    }

    /**
     * Gets connected components of the graph
     */
    public List<Set<String>> getConnectedComponents() {
        List<Set<String>> components = new ArrayList<>();
        Set<String> visited = new HashSet<>();

        for (String vertex : vertices.keySet()) {
            if (!visited.contains(vertex)) {
                Set<String> component = new HashSet<>();
                bfsComponent(vertex, component);
                components.add(component);
                visited.addAll(component);
            }
        }

        return components;
    }

    private void bfsComponent(String start, Set<String> component) {
        Queue<String> queue = new LinkedList<>();
        queue.offer(start);
        component.add(start);

        while (!queue.isEmpty()) {
            String current = queue.poll();
            for (String neighbor : getNeighbors(current)) {
                if (!component.contains(neighbor)) {
                    component.add(neighbor);
                    queue.offer(neighbor);
                }
            }
        }
    }

    /**
     * Calculates graph density
     */
    public double getDensity() {
        int n = getVertexCount();
        if (n <= 1) return 0.0;

        int maxEdges = directed ? n * (n - 1) : n * (n - 1) / 2;
        return (double) getEdgeCount() / maxEdges;
    }

    // Getters
    public String getId() { return id; }
    public boolean isDirected() { return directed; }

    /**
     * Creates a subgraph with specified vertices
     */
    public Graph createSubgraph(Set<String> vertexSubset) {
        Graph subgraph = new Graph(id + "_subgraph", directed);

        for (String vertex : vertexSubset) {
            if (containsVertex(vertex)) {
                subgraph.addVertex(vertex);
            }
        }

        for (Edge edge : edges) {
            if (vertexSubset.contains(edge.getSource()) &&
                    vertexSubset.contains(edge.getDestination())) {
                subgraph.addEdge(edge);
            }
        }

        return subgraph;
    }

    @Override
    public String toString() {
        return String.format(
                "Graph{id='%s', vertices=%d, edges=%d, density=%.3f, connected=%s}",
                id, getVertexCount(), getEdgeCount(), getDensity(), isConnected()
        );
    }

    /**
     * Generates detailed graph information
     */
    public String getDetailedInfo() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== Graph Details ===\n");
        sb.append("ID: ").append(id).append("\n");
        sb.append("Vertices: ").append(getVertexCount()).append("\n");
        sb.append("Edges: ").append(getEdgeCount()).append("\n");
        sb.append("Density: ").append(String.format("%.4f", getDensity())).append("\n");
        sb.append("Connected: ").append(isConnected()).append("\n");
        sb.append("Directed: ").append(directed).append("\n");

        if (!isConnected()) {
            List<Set<String>> components = getConnectedComponents();
            sb.append("Connected Components: ").append(components.size()).append("\n");
            for (int i = 0; i < components.size(); i++) {
                sb.append("  Component ").append(i + 1)
                        .append(": ").append(components.get(i).size()).append(" vertices\n");
            }
        }

        // Vertex degrees
        sb.append("\nVertex Degrees:\n");
        vertices.keySet().stream()
                .sorted()
                .forEach(v -> sb.append("  ").append(v).append(": degree ").append(getDegree(v)).append("\n"));

        return sb.toString();
    }
}