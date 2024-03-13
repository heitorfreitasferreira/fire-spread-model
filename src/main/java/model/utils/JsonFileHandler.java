package model.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.extern.java.Log;

@Log
public class JsonFileHandler {

    public JsonObject readJson(String path)  {
        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            // Create a BufferedReader to read from stdin

            var jsonContent = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                // Read all lines from stdin
                jsonContent.append(line);
            }

            // Parse json
            return JsonParser.parseString(jsonContent.toString()).getAsJsonObject();
        } catch (Exception e) {
            log.severe("Error reading json file");
            return null;
        }
    }

    public void writeJson(String path, JsonObject json) throws Exception {
        var outputFile = new File(path);
        PrintWriter jsonWriter = new PrintWriter(outputFile);

        jsonWriter.write(json.toString());

        jsonWriter.close();
    }

    public static String convertToCustomJson(String metaInfo, int[][][] buffer) {
        StringBuilder jsonBuilder = new StringBuilder();
        jsonBuilder.append("{\n");
        jsonBuilder.append("  \"config\": ").append(metaInfo).append(",\n");
        jsonBuilder.append("  \"execution\": [\n");

        for (int i = 0; i < buffer.length; i++) {
            jsonBuilder.append("    [\n");
            for (int j = 0; j < buffer[i].length; j++) {
                jsonBuilder.append("      [");
                for (int k = 0; k < buffer[i][j].length; k++) {
                    jsonBuilder.append(buffer[i][j][k]);
                    if (k < buffer[i][j].length - 1) {
                        jsonBuilder.append(", ");
                    }
                }
                jsonBuilder.append("]");
                if (j < buffer[i].length - 1) {
                    jsonBuilder.append(",\n");
                } else {
                    jsonBuilder.append("\n");
                }
            }
            jsonBuilder.append("    ]");
            if (i < buffer.length - 1) {
                jsonBuilder.append(",\n");
            } else {
                jsonBuilder.append("\n");
            }
        }

        jsonBuilder.append("  ]\n");
        jsonBuilder.append("}");
        String temp = jsonBuilder.toString();
        return temp;
    }
}