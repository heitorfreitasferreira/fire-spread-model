package model.utils;

import com.beust.jcommander.Parameter;
import com.google.gson.JsonObject;

public class MainArgs {
    @Parameter(names = "--mode")
    public String mode = "single-simulation";

    @Parameter(names = "--input", converter = JsonConverter.class)
    public JsonObject inputFile;

    @Parameter(names = "--output")
    public String outputFilePath = "./output.json";

    @Parameter(names = "--number-of-generations")
    public int numberOfGenerations = 100;

    @Parameter(names = "--population-size")
    public int populationSize = 100;
}
