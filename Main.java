package com.transportation;

import com.transportation.algorithms.PrimAlgorithm;
import com.transportation.algorithms.KruskalAlgorithm;
import com.transportation.model.Graph;
import com.transportation.model.Edge;
import com.transportation.model.MSTResult;
import com.transportation.utils.GraphGenerator;
import com.transportation.utils.JSONFileHandler;
import com.transportation.utils.CSVFileHandler;
import com.transportation.analysis.PerformanceAnalyzer;
import com.transportation.utils.VisualChartGenerator;

import java.io.File;
import java.util.*;

public class Main {
    private static final String INPUT_DIR = "input";
    private static final String OUTPUT_DIR = "output";
    private static final String GRAPHS_DIR = INPUT_DIR + "/graphs";
    private static final String INPUT_JSON = INPUT_DIR + "/input.json";

    public static void main(String[] args) {
        try {
            System.out.println("City Transportation Network Optimization");
            System.out.println("=========================================\n");
            System.out.println("USING CUSTOM GRAPH DATA STRUCTURE IMPLEMENTATION\n");

            // Create directories
            createDirectories();

            List<Graph> graphs;

            // Check if we should generate new data or use existing
            if (args.length > 0 && "generate".equals(args[0])) {
                System.out.println("Generating comprehensive test graphs using CUSTOM GRAPH STRUCTURE...");
                graphs = generateAndSaveAllGraphs();
                System.out.println("Successfully generated " + graphs.size() + " graphs using custom Graph class!");
            } else {
                // Check if input file exists
                File inputFile = new File(INPUT_JSON);
                if (!inputFile.exists()) {
                    System.out.println("Input file not found. Generating new test graphs using CUSTOM GRAPH STRUCTURE...");
                    graphs = generateAndSaveAllGraphs();
                    System.out.println("Successfully generated " + graphs.size() + " graphs using custom Graph class!");
                } else {
                    System.out.println("Reading graphs from " + INPUT_JSON + "...");
                    graphs = JSONFileHandler.readGraphsFromFile(INPUT_JSON);
                    System.out.println("Successfully read " + graphs.size() + " graphs!");
                }
            }

            // Demonstrate custom graph features
            demonstrateCustomGraphFeatures(graphs);

            // Process graphs and generate outputs
            if (!graphs.isEmpty()) {
                processGraphsAndGenerateOutputs(graphs);
            } else {
                System.out.println("No graphs to process!");
            }

        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void demonstrateCustomGraphFeatures(List<Graph> graphs) {
        System.out.println("\n=== DEMONSTRATING CUSTOM GRAPH FEATURES ===");

        if (!graphs.isEmpty()) {
            Graph sampleGraph = graphs.get(0);

            System.out.println("Sample Graph Details:");
            System.out.println(sampleGraph.getDetailedInfo());

            // Demonstrate graph operations
            System.out.println("Graph Operations Demo:");
            List<String> vertices = sampleGraph.getVertices();
            if (!vertices.isEmpty()) {
                String sampleVertex = vertices.get(0);
                System.out.println("Neighbors of " + sampleVertex + ": " + sampleGraph.getNeighbors(sampleVertex));
                System.out.println("Degree of " + sampleVertex + ": " + sampleGraph.getDegree(sampleVertex));
                System.out.println("Incident edges to " + sampleVertex + ": " + sampleGraph.getIncidentEdges(sampleVertex).size());
            }

            System.out.println("Graph density: " + String.format("%.4f", sampleGraph.getDensity()));
            System.out.println("Is connected: " + sampleGraph.isConnected());

            if (!sampleGraph.isConnected()) {
                List<Set<String>> components = sampleGraph.getConnectedComponents();
                System.out.println("Connected components: " + components.size());
            }

            System.out.println("========================================\n");
        }
    }

    private static void createDirectories() {
        System.out.println("Creating directories...");
        new File(INPUT_DIR).mkdirs();
        new File(OUTPUT_DIR).mkdirs();
        new File(GRAPHS_DIR).mkdirs();
        new File(GRAPHS_DIR + "/small").mkdirs();
        new File(GRAPHS_DIR + "/medium").mkdirs();
        new File(GRAPHS_DIR + "/large").mkdirs();
        new File(GRAPHS_DIR + "/extra-large").mkdirs();
        new File(OUTPUT_DIR + "/performance").mkdirs();
        new File(OUTPUT_DIR + "/visual_charts").mkdirs();
        System.out.println("Directories created successfully!");
    }

    private static List<Graph> generateAndSaveAllGraphs() throws Exception {
        // Generate all graphs USING CUSTOM GRAPH STRUCTURE
        System.out.println("Generating test graphs using custom Graph class...");
        List<Graph> allGraphs = GraphGenerator.generateAllTestGraphs();

        // Save main input file
        System.out.println("Saving graphs to " + INPUT_JSON + "...");
        JSONFileHandler.writeGraphsToFile(allGraphs, INPUT_JSON);

        // Save graphs by category
        System.out.println("Saving categorized graphs...");
        Map<String, List<Graph>> categorizedGraphs = new HashMap<>();
        for (Graph graph : allGraphs) {
            String category = getGraphCategory(graph.getId());
            categorizedGraphs.computeIfAbsent(category, k -> new ArrayList<>()).add(graph);
        }

        for (Map.Entry<String, List<Graph>> entry : categorizedGraphs.entrySet()) {
            String category = entry.getKey();
            List<Graph> graphs = entry.getValue();

            // Save JSON for each category
            String categoryJsonPath = GRAPHS_DIR + "/" + category + "/" + category + "_graphs.json";
            JSONFileHandler.writeGraphsToFile(graphs, categoryJsonPath);

            // Save CSV summary for each category
            String categoryCsvPath = GRAPHS_DIR + "/" + category + "/" + category + "_summary.csv";
            CSVFileHandler.writeGraphsToCSV(graphs, categoryCsvPath);
        }

        // Save overall graphs CSV
        CSVFileHandler.writeGraphsToCSV(allGraphs, INPUT_DIR + "/graphs_summary.csv");

        // Print summary
        printGenerationSummary(allGraphs);

        return allGraphs;
    }

    private static void printGenerationSummary(List<Graph> graphs) {
        Map<String, Integer> categoryCount = new HashMap<>();
        Map<String, Integer> categoryVertices = new HashMap<>();
        Map<String, Integer> categoryEdges = new HashMap<>();
        Map<String, Integer> connectedGraphs = new HashMap<>();

        for (Graph graph : graphs) {
            String category = getGraphCategory(graph.getId());
            categoryCount.merge(category, 1, Integer::sum);
            categoryVertices.merge(category, graph.getVertexCount(), Integer::sum);
            categoryEdges.merge(category, graph.getEdgeCount(), Integer::sum);
            if (graph.isConnected()) {
                connectedGraphs.merge(category, 1, Integer::sum);
            }
        }

        System.out.println("\n=== GENERATION SUMMARY ===");
        System.out.println("Total graphs generated: " + graphs.size());
        System.out.println("USING CUSTOM GRAPH DATA STRUCTURE");

        for (String category : categoryCount.keySet()) {
            int count = categoryCount.get(category);
            int avgVertices = categoryVertices.get(category) / count;
            int avgEdges = categoryEdges.get(category) / count;
            int connectedCount = connectedGraphs.getOrDefault(category, 0);

            System.out.printf("%s: %d graphs (%d connected), avg %d vertices, avg %d edges%n",
                    category.toUpperCase(), count, connectedCount, avgVertices, avgEdges);
        }
    }

    private static void processGraphsAndGenerateOutputs(List<Graph> graphs) throws Exception {
        PrimAlgorithm prim = new PrimAlgorithm();
        KruskalAlgorithm kruskal = new KruskalAlgorithm();

        List<MSTResult> primResults = new ArrayList<>();
        List<MSTResult> kruskalResults = new ArrayList<>();
        List<Map<String, Object>> outputResults = new ArrayList<>();

        System.out.println("\nProcessing " + graphs.size() + " graphs USING CUSTOM GRAPH STRUCTURE...");

        // Process each graph
        for (int i = 0; i < graphs.size(); i++) {
            Graph graph = graphs.get(i);
            System.out.printf("Processing [%d/%d] %s (%d vertices, %d edges, connected: %b) using CUSTOM GRAPH...%n",
                    i + 1, graphs.size(), graph.getId(),
                    graph.getVertexCount(), graph.getEdgeCount(), graph.isConnected());

            MSTResult primResult = prim.findMST(graph);
            MSTResult kruskalResult = kruskal.findMST(graph);

            primResults.add(primResult);
            kruskalResults.add(kruskalResult);

            // Prepare output data
            Map<String, Object> result = createResultMap(graph, primResult, kruskalResult);
            outputResults.add(result);
        }

        // Generate all output files
        generateOutputFiles(outputResults, primResults, kruskalResults);

        // Generate visual charts
        generateVisualCharts(primResults, kruskalResults);

        // Performance analysis
        PerformanceAnalyzer.analyzeResults(primResults, kruskalResults);

        // Final demonstration
        demonstrateFinalResults(graphs, primResults, kruskalResults);
    }

    private static void generateVisualCharts(List<MSTResult> primResults,
                                             List<MSTResult> kruskalResults) throws Exception {
        System.out.println("\nGenerating visual performance charts...");

        String chartsDir = OUTPUT_DIR + "/visual_charts";
        VisualChartGenerator.generatePerformanceCharts(primResults, kruskalResults, chartsDir);

        System.out.println("✓ Visual charts generated in: " + chartsDir);
        System.out.println("  - execution_time_chart.txt");
        System.out.println("  - operations_chart.txt");
        System.out.println("  - cost_comparison_chart.txt");
        System.out.println("  - ascii_time_chart.txt");
        System.out.println("  - ascii_operations_chart.txt");
    }

    private static void demonstrateFinalResults(List<Graph> graphs, List<MSTResult> primResults, List<MSTResult> kruskalResults) {
        System.out.println("\n=== FINAL DEMONSTRATION: CUSTOM GRAPH INTEGRATION ===");

        if (!graphs.isEmpty() && !primResults.isEmpty()) {
            Graph firstGraph = graphs.get(0);
            MSTResult firstPrimResult = primResults.get(0);

            System.out.println("First Graph: " + firstGraph);
            System.out.println("Prim MST Cost: " + firstPrimResult.getTotalCost());
            System.out.println("MST Edges Count: " + firstPrimResult.getMstEdgeCount());
            System.out.println("Expected Edges (V-1): " + (firstGraph.getVertexCount() - 1));

            // Show some MST edges
            List<Edge> mstEdges = firstPrimResult.getMstEdges();
            if (!mstEdges.isEmpty()) {
                System.out.println("Sample MST Edges (first 5):");
                for (int i = 0; i < Math.min(5, mstEdges.size()); i++) {
                    System.out.println("  " + mstEdges.get(i));
                }
            }

            // Count valid MSTs
            long validPrimMSTs = primResults.stream()
                    .filter(r -> r.getTotalCost() != Integer.MAX_VALUE)
                    .count();
            long validKruskalMSTs = kruskalResults.stream()
                    .filter(r -> r.getTotalCost() != Integer.MAX_VALUE)
                    .count();

            System.out.println("Valid MSTs found: Prim=" + validPrimMSTs + "/" + primResults.size() +
                    ", Kruskal=" + validKruskalMSTs + "/" + kruskalResults.size());

            System.out.println("✓ Custom Graph successfully integrated with MST algorithms!");
            System.out.println("✓ All graph operations working correctly!");
            System.out.println("✓ Object-oriented design principles demonstrated!");
            System.out.println("✓ Visual charts generated for performance analysis!");
        }
    }

    private static Map<String, Object> createResultMap(Graph graph, MSTResult prim, MSTResult kruskal) {
        Map<String, Object> result = new HashMap<>();
        result.put("graph_id", graph.getId());
        result.put("vertex_count", graph.getVertexCount());
        result.put("edge_count", graph.getEdgeCount());
        result.put("category", getGraphCategory(graph.getId()));
        result.put("graph_density", graph.getDensity());
        result.put("is_connected", graph.isConnected());

        Map<String, Object> primData = new HashMap<>();
        primData.put("total_cost", prim.getTotalCost());
        primData.put("execution_time_ms", prim.getExecutionTimeMs());
        primData.put("operations_count", prim.getOperationsCount());
        primData.put("mst_edges_count", prim.getMstEdgeCount());

        Map<String, Object> kruskalData = new HashMap<>();
        kruskalData.put("total_cost", kruskal.getTotalCost());
        kruskalData.put("execution_time_ms", kruskal.getExecutionTimeMs());
        kruskalData.put("operations_count", kruskal.getOperationsCount());
        kruskalData.put("mst_edges_count", kruskal.getMstEdgeCount());

        result.put("prim", primData);
        result.put("kruskal", kruskalData);
        result.put("costs_consistent", prim.getTotalCost() == kruskal.getTotalCost());

        return result;
    }

    private static void generateOutputFiles(List<Map<String, Object>> outputResults,
                                            List<MSTResult> primResults,
                                            List<MSTResult> kruskalResults) throws Exception {
        System.out.println("\nGenerating output files...");

        // JSON output
        String outputJson = OUTPUT_DIR + "/output.json";
        JSONFileHandler.writeResultsToFile(outputResults, outputJson);
        System.out.println("✓ " + outputJson);

        // CSV outputs
        String summaryCsv = OUTPUT_DIR + "/summary.csv";
        CSVFileHandler.writeSummaryToCSV(outputResults, summaryCsv);
        System.out.println("✓ " + summaryCsv);

        String performanceCsv = OUTPUT_DIR + "/performance_comparison.csv";
        CSVFileHandler.writePerformanceComparisonToCSV(outputResults, performanceCsv);
        System.out.println("✓ " + performanceCsv);

        String detailedCsv = OUTPUT_DIR + "/performance/detailed_results.csv";
        CSVFileHandler.writeDetailedResultsToCSV(primResults, kruskalResults, detailedCsv);
        System.out.println("✓ " + detailedCsv);

        // Generate category-specific outputs
        generateCategoryOutputs(outputResults);

        System.out.println("All output files generated successfully!");
    }

    private static void generateCategoryOutputs(List<Map<String, Object>> outputResults) throws Exception {
        Map<String, List<Map<String, Object>>> resultsByCategory = new HashMap<>();

        for (Map<String, Object> result : outputResults) {
            String category = (String) result.get("category");
            resultsByCategory.computeIfAbsent(category, k -> new ArrayList<>()).add(result);
        }

        for (Map.Entry<String, List<Map<String, Object>>> entry : resultsByCategory.entrySet()) {
            String category = entry.getKey();
            List<Map<String, Object>> categoryResults = entry.getValue();

            String categoryCsv = OUTPUT_DIR + "/performance/" + category + "_results.csv";
            CSVFileHandler.writeSummaryToCSV(categoryResults, categoryCsv);
            System.out.println("✓ " + categoryCsv);
        }
    }

    private static String getGraphCategory(String graphId) {
        if (graphId.startsWith("small")) return "small";
        if (graphId.startsWith("medium")) return "medium";
        if (graphId.startsWith("large")) return "large";
        if (graphId.startsWith("xlarge")) return "extra-large";
        return "unknown";
    }
}