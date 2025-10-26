package com.transportation.analysis;

import com.transportation.model.MSTResult;
import java.util.*;
import java.util.stream.Collectors;

public class PerformanceAnalyzer {

    public static void analyzeResults(List<MSTResult> primResults, List<MSTResult> kruskalResults) {
        System.out.println("\n=== PERFORMANCE ANALYSIS ===");

        // Categorize results
        Map<String, List<MSTResult>> primByCategory = categorizeResults(primResults);
        Map<String, List<MSTResult>> kruskalByCategory = categorizeResults(kruskalResults);

        // Analyze by category
        for (String category : primByCategory.keySet()) {
            List<MSTResult> primCategory = primByCategory.get(category);
            List<MSTResult> kruskalCategory = kruskalByCategory.get(category);

            analyzeCategory(category, primCategory, kruskalCategory);
        }

        // Overall summary
        analyzeOverall(primResults, kruskalResults);
    }

    private static Map<String, List<MSTResult>> categorizeResults(List<MSTResult> results) {
        Map<String, List<MSTResult>> categorized = new HashMap<>();

        for (MSTResult result : results) {
            String graphId = "graph_" + (results.indexOf(result) + 1);
            String category = getGraphCategory(graphId);
            categorized.computeIfAbsent(category, k -> new ArrayList<>()).add(result);
        }

        return categorized;
    }

    private static void analyzeCategory(String category, List<MSTResult> prim, List<MSTResult> kruskal) {
        System.out.printf("\n--- %s Graphs (%d instances) ---\n", category.toUpperCase(), prim.size());

        // Filter out invalid MSTs (disconnected graphs)
        List<MSTResult> validPrim = prim.stream()
                .filter(r -> r.getTotalCost() != Integer.MAX_VALUE)
                .collect(Collectors.toList());
        List<MSTResult> validKruskal = kruskal.stream()
                .filter(r -> r.getTotalCost() != Integer.MAX_VALUE)
                .collect(Collectors.toList());

        if (validPrim.isEmpty() || validKruskal.isEmpty()) {
            System.out.println("No valid MSTs found for this category (possibly disconnected graphs)");
            return;
        }

        double avgPrimTime = validPrim.stream().mapToLong(MSTResult::getExecutionTimeMs).average().orElse(0);
        double avgKruskalTime = validKruskal.stream().mapToLong(MSTResult::getExecutionTimeMs).average().orElse(0);
        double avgPrimOps = validPrim.stream().mapToInt(MSTResult::getOperationsCount).average().orElse(0);
        double avgKruskalOps = validKruskal.stream().mapToInt(MSTResult::getOperationsCount).average().orElse(0);

        int consistentCount = 0;
        for (int i = 0; i < prim.size(); i++) {
            if (prim.get(i).getTotalCost() != Integer.MAX_VALUE &&
                    kruskal.get(i).getTotalCost() != Integer.MAX_VALUE &&
                    prim.get(i).getTotalCost() == kruskal.get(i).getTotalCost()) {
                consistentCount++;
            }
        }
        double consistencyRate = (double) consistentCount / prim.size() * 100;

        System.out.printf("Average Execution Time: Prim=%.2fms, Kruskal=%.2fms\n", avgPrimTime, avgKruskalTime);
        System.out.printf("Time Ratio (Kruskal/Prim): %.2f\n", avgKruskalTime / avgPrimTime);
        System.out.printf("Average Operations: Prim=%.0f, Kruskal=%.0f\n", avgPrimOps, avgKruskalOps);
        System.out.printf("Operations Ratio (Kruskal/Prim): %.2f\n", avgKruskalOps / avgPrimOps);
        System.out.printf("Cost Consistency: %.1f%% (%d/%d)\n", consistencyRate, consistentCount, prim.size());

        // Additional validation
        System.out.printf("Valid MSTs found: Prim=%d/%d, Kruskal=%d/%d\n",
                validPrim.size(), prim.size(), validKruskal.size(), kruskal.size());
    }

    private static void analyzeOverall(List<MSTResult> primResults, List<MSTResult> kruskalResults) {
        System.out.println("\n=== OVERALL SUMMARY ===");

        // Filter valid results
        List<MSTResult> validPrim = primResults.stream()
                .filter(r -> r.getTotalCost() != Integer.MAX_VALUE)
                .collect(Collectors.toList());
        List<MSTResult> validKruskal = kruskalResults.stream()
                .filter(r -> r.getTotalCost() != Integer.MAX_VALUE)
                .collect(Collectors.toList());

        double primTotalTime = validPrim.stream().mapToLong(MSTResult::getExecutionTimeMs).sum();
        double kruskalTotalTime = validKruskal.stream().mapToLong(MSTResult::getExecutionTimeMs).sum();

        double primAvgTime = validPrim.stream().mapToLong(MSTResult::getExecutionTimeMs).average().orElse(0);
        double kruskalAvgTime = validKruskal.stream().mapToLong(MSTResult::getExecutionTimeMs).average().orElse(0);

        double primAvgOps = validPrim.stream().mapToInt(MSTResult::getOperationsCount).average().orElse(0);
        double kruskalAvgOps = validKruskal.stream().mapToInt(MSTResult::getOperationsCount).average().orElse(0);

        int consistentCount = 0;
        for (int i = 0; i < primResults.size(); i++) {
            if (primResults.get(i).getTotalCost() != Integer.MAX_VALUE &&
                    kruskalResults.get(i).getTotalCost() != Integer.MAX_VALUE &&
                    primResults.get(i).getTotalCost() == kruskalResults.get(i).getTotalCost()) {
                consistentCount++;
            }
        }

        System.out.printf("Total Graphs Processed: %d\n", primResults.size());
        System.out.printf("Valid Connected Graphs: %d\n", validPrim.size());
        System.out.printf("Total Execution Time: Prim=%.2fms, Kruskal=%.2fms\n", primTotalTime, kruskalTotalTime);
        System.out.printf("Average Execution Time: Prim=%.2fms, Kruskal=%.2fms\n", primAvgTime, kruskalAvgTime);
        System.out.printf("Average Operations: Prim=%.0f, Kruskal=%.0f\n", primAvgOps, kruskalAvgOps);
        System.out.printf("Overall Cost Consistency: %.1f%% (%d/%d)\n",
                (double) consistentCount / primResults.size() * 100,
                consistentCount, primResults.size());
    }

    private static String getGraphCategory(String graphId) {
        if (graphId.contains("small")) return "small";
        if (graphId.contains("medium")) return "medium";
        if (graphId.contains("large")) return "large";
        if (graphId.contains("xlarge")) return "extra-large";
        return "unknown";
    }
}