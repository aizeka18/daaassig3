package com.transportation.utils;

import com.transportation.model.Graph;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.io.*;
import java.lang.reflect.Type;
import java.util.*;

public class JSONFileHandler {
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public static void writeGraphsToFile(List<Graph> graphs, String filename) throws IOException {
        Map<String, Object> data = new HashMap<>();
        data.put("graphs", graphs);
        data.put("generated_at", new Date().toString());
        data.put("total_graphs", graphs.size());

        // Create parent directories if they don't exist
        File file = new File(filename);
        file.getParentFile().mkdirs();

        try (FileWriter writer = new FileWriter(file)) {
            gson.toJson(data, writer);
        }
    }

    public static List<Graph> readGraphsFromFile(String filename) throws IOException {
        File file = new File(filename);
        if (!file.exists()) {
            throw new FileNotFoundException("File not found: " + filename);
        }

        try (FileReader reader = new FileReader(file)) {
            Type type = new TypeToken<Map<String, List<Graph>>>(){}.getType();
            Map<String, List<Graph>> data = gson.fromJson(reader, type);
            return data != null ? data.get("graphs") : new ArrayList<>();
        }
    }

    public static void writeResultsToFile(List<Map<String, Object>> results, String filename) throws IOException {
        // Convert all numeric values to Double for consistent JSON serialization
        List<Map<String, Object>> processedResults = new ArrayList<>();
        for (Map<String, Object> result : results) {
            Map<String, Object> processedResult = new HashMap<>();

            for (Map.Entry<String, Object> entry : result.entrySet()) {
                Object value = entry.getValue();

                // Convert numeric types to Double for consistent JSON handling
                if (value instanceof Integer) {
                    processedResult.put(entry.getKey(), ((Integer) value).doubleValue());
                } else if (value instanceof Long) {
                    processedResult.put(entry.getKey(), ((Long) value).doubleValue());
                } else if (value instanceof Map) {
                    // Process nested maps
                    @SuppressWarnings("unchecked")
                    Map<String, Object> nestedMap = (Map<String, Object>) value;
                    Map<String, Object> processedNestedMap = new HashMap<>();

                    for (Map.Entry<String, Object> nestedEntry : nestedMap.entrySet()) {
                        Object nestedValue = nestedEntry.getValue();
                        if (nestedValue instanceof Integer) {
                            processedNestedMap.put(nestedEntry.getKey(), ((Integer) nestedValue).doubleValue());
                        } else if (nestedValue instanceof Long) {
                            processedNestedMap.put(nestedEntry.getKey(), ((Long) nestedValue).doubleValue());
                        } else {
                            processedNestedMap.put(nestedEntry.getKey(), nestedValue);
                        }
                    }
                    processedResult.put(entry.getKey(), processedNestedMap);
                } else {
                    processedResult.put(entry.getKey(), value);
                }
            }
            processedResults.add(processedResult);
        }

        Map<String, Object> output = new HashMap<>();
        output.put("results", processedResults);
        output.put("timestamp", new Date().toString());
        output.put("total_graphs", processedResults.size());

        // Create parent directories if they don't exist
        File file = new File(filename);
        file.getParentFile().mkdirs();

        try (FileWriter writer = new FileWriter(file)) {
            gson.toJson(output, writer);
        }
    }
}