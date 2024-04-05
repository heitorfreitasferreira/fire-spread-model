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
    public int numberOfGenerations = 20;

    @Parameter(names = "--population-size")
    public int populationSize = 30;

    @Parameter(names = "--mutation-rate")
    public double mutationRate = 0.1;

    @Parameter(names = "--mutation-prob")
    public double mutationProb = 0.1;

    @Parameter(names = "--crossover-rate")
    public double crossoverRate = 0.9;

    @Parameter(names = "--elitism-rate")
    public double elitismRate = 0.1;

    @Parameter(names = "--tournament-size")
    public int tournamentSize = 2;

    @Parameter(names = "--max-iterations")
    public int maxIterations = 100;

    @Parameter(names = "--crossover-blx-alpha")
    public double crossoverBlxAlpha = 0.001;

    @Parameter(names = "--number-of-simulations-per-fitness")
    public int numberOfSimulationsPerFitness = 30;

    @Parameter(names = "--type-of-reproduction")
    public String typeOfReproduction = "sexuado";
}
