package com.transportation.model;

/**
 * Custom Edge class representing a road between two districts
 * Implements Comparable for sorting in Kruskal's algorithm
 */
public class Edge implements Comparable<Edge> {
    private final String source;
    private final String destination;
    private final int weight;
    private final String id;

    public Edge(String source, String destination, int weight) {
        if (source == null || destination == null) {
            throw new IllegalArgumentException("Source and destination cannot be null");
        }
        if (weight < 0) {
            throw new IllegalArgumentException("Weight cannot be negative");
        }

        this.source = source;
        this.destination = destination;
        this.weight = weight;
        // Create consistent ID for undirected graph
        this.id = source.compareTo(destination) < 0 ?
                source + "-" + destination : destination + "-" + source;
    }

    // Getters
    public String getSource() { return source; }
    public String getDestination() { return destination; }
    public int getWeight() { return weight; }
    public String getId() { return id; }

    /**
     * Gets the other vertex of this edge
     * @param vertex one vertex of the edge
     * @return the other vertex
     */
    public String getOtherVertex(String vertex) {
        if (vertex.equals(source)) return destination;
        if (vertex.equals(destination)) return source;
        throw new IllegalArgumentException("Vertex " + vertex + " is not part of this edge");
    }

    /**
     * Checks if this edge contains the given vertex
     * @param vertex vertex to check
     * @return true if edge contains the vertex
     */
    public boolean containsVertex(String vertex) {
        return source.equals(vertex) || destination.equals(vertex);
    }

    @Override
    public int compareTo(Edge other) {
        return Integer.compare(this.weight, other.weight);
    }

    @Override
    public String toString() {
        return String.format("Edge[%s --%d--> %s]", source, weight, destination);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Edge edge = (Edge) obj;
        return id.equals(edge.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}