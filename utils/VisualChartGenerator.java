package com.transportation.utils;

import com.transportation.model.MSTResult;
import java.io.*;
import java.util.*;

/**
 * Generates ASCII visual charts for performance comparison
 * Creates text-based charts that can be included in reports
 */
public class VisualChartGenerator {

    public static void generatePerformanceCharts(List<MSTResult> primResults,
                                                 List<MSTResult> kruskalResults,
                                                 String outputDir) throws IOException {

        // Create output directory
        new File(outputDir).mkdirs();

        // Generate execution time comparison chart
        generateExecutionTimeChart(primResults, kruskalResults, outputDir + "/execution_time_chart.txt");

        // Generate operations count comparison chart
        generateOperationsChart(primResults, kruskalResults, outputDir + "/operations_chart.txt");

        // Generate cost consistency chart
        generateCostComparisonChart(primResults, kruskalResults, outputDir + "/cost_comparison_chart.txt");

        // Generate ASCII bar charts
        generateASCIITimeChart(primResults, kruskalResults, outputDir + "/ascii_time_chart.txt");
        generateASCIIOperationsChart(primResults, kruskalResults, outputDir + "/ascii_operations_chart.txt");

        System.out.println("Performance charts generated in: " + outputDir);
    }

    private static void generateExecutionTimeChart(List<MSTResult> primResults,
                                                   List<MSTResult> kruskalResults,
                                                   String outputPath) throws IOException {

        try (PrintWriter writer = new PrintWriter(new FileWriter(outputPath))) {
            writer.println("EXECUTION TIME COMPARISON - Prim vs Kruskal");
            writer.println("=============================================");
            writer.println();

            writer.println("Graph Size        | Prim (ms) | Kruskal (ms) | Ratio (K/P)");
            writer.println("------------------|-----------|--------------|------------");

            for (int i = 0; i < primResults.size(); i++) {
                String graphName = "Graph_" + (i + 1);
                long primTime = primResults.get(i).getExecutionTimeMs();
                long kruskalTime = kruskalResults.get(i).getExecutionTimeMs();
                double ratio = primTime == 0 ? 0 : (double) kruskalTime / primTime;

                writer.printf("%-16s | %9d | %12d | %10.2f%n",
                        graphName, primTime, kruskalTime, ratio);
            }

            // Summary statistics
            writer.println();
            writer.println("SUMMARY STATISTICS:");
            double avgPrim = primResults.stream().mapToLong(MSTResult::getExecutionTimeMs).average().orElse(0);
            double avgKruskal = kruskalResults.stream().mapToLong(MSTResult::getExecutionTimeMs).average().orElse(0);
            writer.printf("Average Time - Prim: %.2f ms, Kruskal: %.2f ms%n", avgPrim, avgKruskal);
            writer.printf("Overall Ratio (Kruskal/Prim): %.2f%n", avgKruskal / avgPrim);
        }
    }

    private static void generateOperationsChart(List<MSTResult> primResults,
                                                List<MSTResult> kruskalResults,
                                                String outputPath) throws IOException {

        try (PrintWriter writer = new PrintWriter(new FileWriter(outputPath))) {
            writer.println("OPERATIONS COUNT COMPARISON - Prim vs Kruskal");
            writer.println("==============================================");
            writer.println();

            writer.println("Graph Size        | Prim Ops  | Kruskal Ops | Ratio (K/P)");
            writer.println("------------------|-----------|-------------|------------");

            for (int i = 0; i < primResults.size(); i++) {
                String graphName = "Graph_" + (i + 1);
                int primOps = primResults.get(i).getOperationsCount();
                int kruskalOps = kruskalResults.get(i).getOperationsCount();
                double ratio = primOps == 0 ? 0 : (double) kruskalOps / primOps;

                writer.printf("%-16s | %9d | %11d | %10.2f%n",
                        graphName, primOps, kruskalOps, ratio);
            }

            // Summary statistics
            writer.println();
            writer.println("SUMMARY STATISTICS:");
            double avgPrim = primResults.stream().mapToInt(MSTResult::getOperationsCount).average().orElse(0);
            double avgKruskal = kruskalResults.stream().mapToInt(MSTResult::getOperationsCount).average().orElse(0);
            writer.printf("Average Operations - Prim: %.0f, Kruskal: %.0f%n", avgPrim, avgKruskal);
            writer.printf("Overall Ratio (Kruskal/Prim): %.2f%n", avgKruskal / avgPrim);
        }
    }

    private static void generateCostComparisonChart(List<MSTResult> primResults,
                                                    List<MSTResult> kruskalResults,
                                                    String outputPath) throws IOException {

        try (PrintWriter writer = new PrintWriter(new FileWriter(outputPath))) {
            writer.println("MST COST COMPARISON - Consistency Check");
            writer.println("========================================");
            writer.println();

            writer.println("Graph Size        | Prim Cost | Kruskal Cost | Consistent");
            writer.println("------------------|-----------|--------------|-----------");

            int consistentCount = 0;

            for (int i = 0; i < primResults.size(); i++) {
                String graphName = "Graph_" + (i + 1);
                int primCost = primResults.get(i).getTotalCost();
                int kruskalCost = kruskalResults.get(i).getTotalCost();
                boolean consistent = primCost == kruskalCost;

                if (consistent) consistentCount++;

                writer.printf("%-16s | %9d | %12d | %10s%n",
                        graphName, primCost, kruskalCost, consistent ? "✓" : "✗");
            }

            // Summary statistics
            writer.println();
            writer.println("SUMMARY STATISTICS:");
            double consistencyRate = (double) consistentCount / primResults.size() * 100;
            writer.printf("Consistency Rate: %.1f%% (%d/%d)%n", consistencyRate, consistentCount, primResults.size());
        }
    }

    private static void generateASCIITimeChart(List<MSTResult> primResults,
                                               List<MSTResult> kruskalResults,
                                               String outputPath) throws IOException {

        try (PrintWriter writer = new PrintWriter(new FileWriter(outputPath))) {
            writer.println("ASCII EXECUTION TIME COMPARISON CHART");
            writer.println("======================================");
            writer.println();
            writer.println("Each '#' represents 5ms");
            writer.println();

            for (int i = 0; i < primResults.size(); i++) {
                String graphName = "G" + (i + 1);
                long primTime = primResults.get(i).getExecutionTimeMs();
                long kruskalTime = kruskalResults.get(i).getExecutionTimeMs();

                writer.printf("%-4s: Prim [%-30s] %d ms%n",
                        graphName, repeat('#', (int)(primTime/5)), primTime);
                writer.printf("%-4s: Krus [%-30s] %d ms%n",
                        "", repeat('#', (int)(kruskalTime/5)), kruskalTime);
                writer.println();
            }
        }
    }

    private static void generateASCIIOperationsChart(List<MSTResult> primResults,
                                                     List<MSTResult> kruskalResults,
                                                     String outputPath) throws IOException {

        try (PrintWriter writer = new PrintWriter(new FileWriter(outputPath))) {
            writer.println("ASCII OPERATIONS COUNT COMPARISON CHART");
            writer.println("========================================");
            writer.println();
            writer.println("Each '#' represents 500 operations");
            writer.println();

            for (int i = 0; i < primResults.size(); i++) {
                String graphName = "G" + (i + 1);
                int primOps = primResults.get(i).getOperationsCount();
                int kruskalOps = kruskalResults.get(i).getOperationsCount();

                writer.printf("%-4s: Prim [%-30s] %d ops%n",
                        graphName, repeat('#', primOps/500), primOps);
                writer.printf("%-4s: Krus [%-30s] %d ops%n",
                        "", repeat('#', kruskalOps/500), kruskalOps);
                writer.println();
            }
        }
    }

    private static String repeat(char c, int count) {
        if (count <= 0) return "";
        return String.valueOf(c).repeat(Math.max(0, count));
    }
}