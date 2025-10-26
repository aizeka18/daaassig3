package com.transportation.model;

import java.util.List;

public class MSTResult {
    private String algorithm;
    private List<Edge> mstEdges;
    private int totalCost;
    private long executionTimeMs;
    private int operationsCount;
    private int vertexCount;
    private int edgeCount;

    public MSTResult(String algorithm, List<Edge> mstEdges, int totalCost,
                     long executionTimeMs, int operationsCount,
                     int vertexCount, int edgeCount) {
        this.algorithm = algorithm;
        this.mstEdges = mstEdges;
        this.totalCost = totalCost;
        this.executionTimeMs = executionTimeMs;
        this.operationsCount = operationsCount;
        this.vertexCount = vertexCount;
        this.edgeCount = edgeCount;
    }

    // Getters
    public String getAlgorithm() { return algorithm; }
    public List<Edge> getMstEdges() { return mstEdges; }
    public int getTotalCost() { return totalCost; }
    public long getExecutionTimeMs() { return executionTimeMs; }
    public int getOperationsCount() { return operationsCount; }
    public int getVertexCount() { return vertexCount; }
    public int getEdgeCount() { return edgeCount; }

    public int getMstEdgeCount() {
        return mstEdges != null ? mstEdges.size() : 0;
    }

    @Override
    public String toString() {
        return String.format(
                "MSTResult{algorithm='%s', totalCost=%d, executionTime=%dms, operations=%d, edges=%d}",
                algorithm, totalCost, executionTimeMs, operationsCount, getMstEdgeCount()
        );
    }
}