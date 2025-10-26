package com.transportation.utils;

import com.transportation.model.Graph;
import com.transportation.model.MSTResult;
import java.io.*;
import java.util.*;

public class CSVFileHandler {

    public static void writeGraphsToCSV(List<Graph> graphs, String filename) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            // Write header - ВСЕ В ОДНУ СТРОКУ
            writer.println("graph_id,vertex_count,edge_count,density,category,is_connected,graph_structure_info");

            // Write data - ВСЕ В ОДНУ СТРОКУ
            for (Graph graph : graphs) {
                int vertexCount = graph.getVertexCount();
                int edgeCount = graph.getEdgeCount();
                double density = (2.0 * edgeCount) / (vertexCount * (vertexCount - 1));
                String category = getGraphCategory(graph.getId());
                boolean isConnected = graph.isConnected();
                String structureInfo = String.format("V%d_E%d_D%.3f", vertexCount, edgeCount, density);

                writer.printf("%s,%d,%d,%.4f,%s,%b,%s%n",
                        graph.getId(), vertexCount, edgeCount, density, category, isConnected, structureInfo);
            }
        }
    }

    public static void writeSummaryToCSV(List<Map<String, Object>> results, String filename) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            // Write header - ВСЕ В ОДНУ ШИРОКУЮ СТРОКУ
            writer.println("graph_id,vertex_count,edge_count,category,graph_density,is_connected," +
                    "prim_total_cost,prim_execution_time_ms,prim_operations_count,prim_mst_edges_count," +
                    "kruskal_total_cost,kruskal_execution_time_ms,kruskal_operations_count,kruskal_mst_edges_count," +
                    "costs_consistent,performance_ratio,operations_ratio,mst_validation");

            // Write data - ВСЕ В ОДНУ ШИРОКУЮ СТРОКУ
            for (Map<String, Object> result : results) {
                String graphId = (String) result.get("graph_id");
                int vertexCount = (Integer) result.get("vertex_count");
                int edgeCount = (Integer) result.get("edge_count");
                String category = (String) result.get("category");
                double graphDensity = (Double) result.get("graph_density");
                boolean isConnected = (Boolean) result.get("is_connected");

                @SuppressWarnings("unchecked")
                Map<String, Object> prim = (Map<String, Object>) result.get("prim");
                @SuppressWarnings("unchecked")
                Map<String, Object> kruskal = (Map<String, Object>) result.get("kruskal");

                boolean costsConsistent = (Boolean) result.get("costs_consistent");

                // Calculate ratios
                long primTime = getLongValue(prim.get("execution_time_ms"));
                long kruskalTime = getLongValue(kruskal.get("execution_time_ms"));
                double performanceRatio = primTime == 0 ? 0 : (double) kruskalTime / primTime;

                int primOps = getIntValue(prim.get("operations_count"));
                int kruskalOps = getIntValue(kruskal.get("operations_count"));
                double operationsRatio = primOps == 0 ? 0 : (double) kruskalOps / primOps;

                String mstValidation = costsConsistent ? "VALID" : "INVALID";

                // ВСЕ ДАННЫЕ В ОДНУ ДЛИННУЮ СТРОКУ
                writer.printf("%s,%d,%d,%s,%.4f,%b,%d,%d,%d,%d,%d,%d,%d,%d,%b,%.3f,%.3f,%s%n",
                        graphId, vertexCount, edgeCount, category, graphDensity, isConnected,
                        getIntValue(prim.get("total_cost")),
                        primTime,
                        primOps,
                        getIntValue(prim.get("mst_edges_count")),
                        getIntValue(kruskal.get("total_cost")),
                        kruskalTime,
                        kruskalOps,
                        getIntValue(kruskal.get("mst_edges_count")),
                        costsConsistent,
                        performanceRatio,
                        operationsRatio,
                        mstValidation
                );
            }
        }
    }

    public static void writePerformanceComparisonToCSV(List<Map<String, Object>> results, String filename) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            // Write header - ВСЕ В ОДНУ ШИРОКУЮ СТРОКУ
            writer.println("category,graph_count,total_vertices,total_edges,avg_vertices,avg_edges,avg_density,connected_graphs_count," +
                    "avg_prim_time_ms,avg_kruskal_time_ms,avg_time_ratio,min_prim_time,max_prim_time,min_kruskal_time,max_kruskal_time," +
                    "avg_prim_operations,avg_kruskal_operations,avg_operations_ratio,min_prim_ops,max_prim_ops,min_kruskal_ops,max_kruskal_ops," +
                    "consistency_rate,total_mst_edges_verified,algorithm_efficiency_summary");

            Map<String, List<Map<String, Object>>> resultsByCategory = new HashMap<>();

            // Group results by category
            for (Map<String, Object> result : results) {
                String category = (String) result.get("category");
                resultsByCategory.computeIfAbsent(category, k -> new ArrayList<>()).add(result);
            }

            // Calculate statistics for each category - ВСЕ В ОДНУ СТРОКУ НА КАТЕГОРИЮ
            for (Map.Entry<String, List<Map<String, Object>>> entry : resultsByCategory.entrySet()) {
                String category = entry.getKey();
                List<Map<String, Object>> categoryResults = entry.getValue();

                // Basic statistics
                int graphCount = categoryResults.size();
                int totalVertices = categoryResults.stream().mapToInt(r -> (Integer) r.get("vertex_count")).sum();
                int totalEdges = categoryResults.stream().mapToInt(r -> (Integer) r.get("edge_count")).sum();
                double avgVertices = categoryResults.stream().mapToInt(r -> (Integer) r.get("vertex_count")).average().orElse(0);
                double avgEdges = categoryResults.stream().mapToInt(r -> (Integer) r.get("edge_count")).average().orElse(0);
                double avgDensity = categoryResults.stream().mapToDouble(r -> (Double) r.get("graph_density")).average().orElse(0);
                long connectedGraphsCount = categoryResults.stream().filter(r -> (Boolean) r.get("is_connected")).count();

                // Time statistics
                double avgPrimTime = categoryResults.stream().mapToDouble(r -> getDoubleValue(((Map<String, Object>) r.get("prim")).get("execution_time_ms"))).average().orElse(0);
                double avgKruskalTime = categoryResults.stream().mapToDouble(r -> getDoubleValue(((Map<String, Object>) r.get("kruskal")).get("execution_time_ms"))).average().orElse(0);
                double minPrimTime = categoryResults.stream().mapToDouble(r -> getDoubleValue(((Map<String, Object>) r.get("prim")).get("execution_time_ms"))).min().orElse(0);
                double maxPrimTime = categoryResults.stream().mapToDouble(r -> getDoubleValue(((Map<String, Object>) r.get("prim")).get("execution_time_ms"))).max().orElse(0);
                double minKruskalTime = categoryResults.stream().mapToDouble(r -> getDoubleValue(((Map<String, Object>) r.get("kruskal")).get("execution_time_ms"))).min().orElse(0);
                double maxKruskalTime = categoryResults.stream().mapToDouble(r -> getDoubleValue(((Map<String, Object>) r.get("kruskal")).get("execution_time_ms"))).max().orElse(0);
                double avgTimeRatio = avgPrimTime == 0 ? 0 : avgKruskalTime / avgPrimTime;

                // Operations statistics
                double avgPrimOperations = categoryResults.stream().mapToDouble(r -> getDoubleValue(((Map<String, Object>) r.get("prim")).get("operations_count"))).average().orElse(0);
                double avgKruskalOperations = categoryResults.stream().mapToDouble(r -> getDoubleValue(((Map<String, Object>) r.get("kruskal")).get("operations_count"))).average().orElse(0);
                double minPrimOps = categoryResults.stream().mapToDouble(r -> getDoubleValue(((Map<String, Object>) r.get("prim")).get("operations_count"))).min().orElse(0);
                double maxPrimOps = categoryResults.stream().mapToDouble(r -> getDoubleValue(((Map<String, Object>) r.get("prim")).get("operations_count"))).max().orElse(0);
                double minKruskalOps = categoryResults.stream().mapToDouble(r -> getDoubleValue(((Map<String, Object>) r.get("kruskal")).get("operations_count"))).min().orElse(0);
                double maxKruskalOps = categoryResults.stream().mapToDouble(r -> getDoubleValue(((Map<String, Object>) r.get("kruskal")).get("operations_count"))).max().orElse(0);
                double avgOperationsRatio = avgPrimOperations == 0 ? 0 : avgKruskalOperations / avgPrimOperations;

                // Consistency statistics
                double consistencyRate = categoryResults.stream().mapToDouble(r -> ((Boolean) r.get("costs_consistent")) ? 1.0 : 0.0).average().orElse(0) * 100;
                int totalMstEdgesVerified = categoryResults.stream().mapToInt(r -> getIntValue(((Map<String, Object>) r.get("prim")).get("mst_edges_count"))).sum();

                // Efficiency summary
                String efficiencySummary = avgTimeRatio < 1.0 ? "Prim_faster" : avgTimeRatio > 1.0 ? "Kruskal_faster" : "Equal_performance";
                if (avgOperationsRatio < 1.0) efficiencySummary += "_Kruskal_more_efficient";
                else if (avgOperationsRatio > 1.0) efficiencySummary += "_Prim_more_efficient";

                // ВСЕ ДАННЫЕ КАТЕГОРИИ В ОДНУ ОЧЕНЬ ШИРОКУЮ СТРОКУ
                writer.printf("%s,%d,%d,%d,%.2f,%.2f,%.4f,%d,%.2f,%.2f,%.3f,%.2f,%.2f,%.2f,%.2f,%.2f,%.2f,%.3f,%.2f,%.2f,%.2f,%.2f,%.2f,%d,%s%n",
                        category, graphCount, totalVertices, totalEdges, avgVertices, avgEdges, avgDensity, connectedGraphsCount,
                        avgPrimTime, avgKruskalTime, avgTimeRatio, minPrimTime, maxPrimTime, minKruskalTime, maxKruskalTime,
                        avgPrimOperations, avgKruskalOperations, avgOperationsRatio, minPrimOps, maxPrimOps, minKruskalOps, maxKruskalOps,
                        consistencyRate, totalMstEdgesVerified, efficiencySummary
                );
            }
        }
    }

    public static void writeDetailedResultsToCSV(List<MSTResult> primResults,
                                                 List<MSTResult> kruskalResults,
                                                 String filename) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            // Write header - ВСЕ В ОДНУ ШИРОКУЮ СТРОКУ
            writer.println("algorithm,graph_id,vertex_count,edge_count,graph_size_category,total_mst_cost,execution_time_ms,operations_count,mst_edge_count,edges_per_vertex,efficiency_ratio,performance_note");

            // Write Prim results - ВСЕ В ОДНУ СТРОКУ
            for (int i = 0; i < primResults.size(); i++) {
                MSTResult result = primResults.get(i);
                String graphId = "graph_" + (i + 1);
                String category = getSizeCategory(result.getVertexCount());
                double edgesPerVertex = result.getVertexCount() == 0 ? 0 : (double) result.getEdgeCount() / result.getVertexCount();
                double efficiency = result.getOperationsCount() == 0 ? 0 : (double) result.getMstEdgeCount() / result.getOperationsCount() * 1000;
                String performanceNote = result.getExecutionTimeMs() < 100 ? "Fast" : result.getExecutionTimeMs() < 500 ? "Medium" : "Slow";

                writer.printf("Prim,%s,%d,%d,%s,%d,%d,%d,%d,%.3f,%.3f,%s%n",
                        graphId, result.getVertexCount(), result.getEdgeCount(), category,
                        result.getTotalCost(), result.getExecutionTimeMs(), result.getOperationsCount(), result.getMstEdgeCount(),
                        edgesPerVertex, efficiency, performanceNote
                );
            }

            // Write Kruskal results - ВСЕ В ОДНУ СТРОКУ
            for (int i = 0; i < kruskalResults.size(); i++) {
                MSTResult result = kruskalResults.get(i);
                String graphId = "graph_" + (i + 1);
                String category = getSizeCategory(result.getVertexCount());
                double edgesPerVertex = result.getVertexCount() == 0 ? 0 : (double) result.getEdgeCount() / result.getVertexCount();
                double efficiency = result.getOperationsCount() == 0 ? 0 : (double) result.getMstEdgeCount() / result.getOperationsCount() * 1000;
                String performanceNote = result.getExecutionTimeMs() < 100 ? "Fast" : result.getExecutionTimeMs() < 500 ? "Medium" : "Slow";

                writer.printf("Kruskal,%s,%d,%d,%s,%d,%d,%d,%d,%.3f,%.3f,%s%n",
                        graphId, result.getVertexCount(), result.getEdgeCount(), category,
                        result.getTotalCost(), result.getExecutionTimeMs(), result.getOperationsCount(), result.getMstEdgeCount(),
                        edgesPerVertex, efficiency, performanceNote
                );
            }
        }
    }

    private static String getSizeCategory(int vertexCount) {
        if (vertexCount <= 50) return "Small";
        if (vertexCount <= 300) return "Medium";
        if (vertexCount <= 1000) return "Large";
        return "Extra_Large";
    }

    // Helper methods to safely extract values from Object
    private static int getIntValue(Object value) {
        if (value instanceof Integer) return (Integer) value;
        if (value instanceof Double) return ((Double) value).intValue();
        if (value instanceof Long) return ((Long) value).intValue();
        return 0;
    }

    private static long getLongValue(Object value) {
        if (value instanceof Long) return (Long) value;
        if (value instanceof Integer) return ((Integer) value).longValue();
        if (value instanceof Double) return ((Double) value).longValue();
        return 0L;
    }

    private static double getDoubleValue(Object value) {
        if (value instanceof Double) return (Double) value;
        if (value instanceof Integer) return ((Integer) value).doubleValue();
        if (value instanceof Long) return ((Long) value).doubleValue();
        return 0.0;
    }

    private static String getGraphCategory(String graphId) {
        if (graphId.startsWith("small")) return "small";
        if (graphId.startsWith("medium")) return "medium";
        if (graphId.startsWith("large")) return "large";
        if (graphId.startsWith("xlarge")) return "extra-large";
        return "unknown";
    }
}