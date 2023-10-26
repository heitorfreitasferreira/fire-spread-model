package model.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class JsonFileHandler {

    public JsonObject readJson(String path) throws Exception {
        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            // Create a BufferedReader to read from stdin


            var jsonContent = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                // Read all lines from stdin
                jsonContent.append(line);
            }

            // Parse json
            return JsonParser.parseString(jsonContent.toString()).getAsJsonObject();
        }
    }

    public void writeJson(String path, JsonObject json) throws Exception {
        var outputFile = new File(path);
        PrintWriter jsonWriter = new PrintWriter(outputFile);

        jsonWriter.write(json.toString());

        jsonWriter.close();
    }
}