package model.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class JsonFileHandler {

  public JsonObject readJson(String path) throws Exception {
    // Create a BufferedReader to read from stdin
    BufferedReader reader = new BufferedReader(new FileReader(path));

    var jsonContent = new StringBuilder();
    String line = null;
    while ((line = reader.readLine()) != null) {
      // Read all lines from stdin
      jsonContent.append(line);
    }

    reader.close();

    // Parse json
    var json = JsonParser.parseString(jsonContent.toString()).getAsJsonObject();

    return json;
  }

  public void writeJson(String path, JsonObject json) throws Exception {
    var outputFile = new File(path);
    PrintWriter jsonWriter = new PrintWriter(outputFile);

    jsonWriter.write(json.toString());
    
    jsonWriter.close();
  }
}
