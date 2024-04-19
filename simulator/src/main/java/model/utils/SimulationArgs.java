package model.utils;

import com.beust.jcommander.Parameter;
import com.google.gson.JsonObject;

import genetic.GeneticAlgorithmParams;
import genetic.reproductors.Reproductor;
import lombok.Setter;
import lombok.ToString;

@ToString
public class SimulationArgs implements GeneticAlgorithmParams {
    @Parameter(names = "--mode")
    private SimulationArgs.Type mode = SimulationArgs.Type.SINGLE_SIMULATION;// "single-simulation";

    @Parameter(names = "--input", converter = JsonConverter.class)
    private JsonObject inputFile;

    @Parameter(names = "--output")
    private String outputFilePath = "./output.json";

    @Parameter(names = "--number-of-generations")
    private int numberOfGenerations = 50;

    @Parameter(names = "--population-size")
    private int populationSize = 100;

    @Parameter(names = "--mutation-rate")
    private double mutationRate = 0.1;

    @Parameter(names = "--mutation-prob")
    private double mutationProb = 0.1;

    @Parameter(names = "--crossover-rate")
    private double crossoverRate = 0.5;

    @Parameter(names = "--elitism-rate")
    private double elitismRate = 0.1;

    @Parameter(names = "--reverse-elitism-n")
    @Setter
    private int reverseElitismN = 3;

    @Parameter(names = "--tournament-size")
    private int tournamentSize = 2;

    @Parameter(names = "--max-iterations")
    private int maxIterations = 100;

    @Parameter(names = "--crossover-blx-alpha")
    private double crossoverBlxAlpha = 0.01;

    @Parameter(names = "--number-of-simulations-per-fitness")
    private int numberOfSimulationsPerFitness = 10;

    @Parameter(names = "--type-of-reproduction")
    private Reproductor.Type typeOfReproduction = Reproductor.Type.ASSEXUADO;

    public SimulationArgs.Type mode() {
        return mode;
    }

    public JsonObject inputFile() {
        return inputFile;
    }

    public String outputFilePath() {
        return outputFilePath;
    }

    public int numberOfGenerations() {
        return numberOfGenerations;
    }

    public int populationSize() {
        return populationSize;
    }

    public double mutationRate() {
        return mutationRate;
    }

    public double mutationProb() {
        return mutationProb;
    }

    public double crossoverRate() {
        return crossoverRate;
    }

    public double elitismRate() {
        return elitismRate;
    }

    public int reverseElitismN() {
        return reverseElitismN;
    }

    public int tournamentSize() {
        return tournamentSize;
    }

    public int maxIterations() {
        return maxIterations;
    }

    public double crossoverBlxAlpha() {
        return crossoverBlxAlpha;
    }

    public int numberOfSimulationsPerFitness() {
        return numberOfSimulationsPerFitness;
    }

    public Reproductor.Type typeOfReproduction() {
        return typeOfReproduction;
    }

    public enum Type {
        SINGLE_SIMULATION,
        GENETIC_ALGORITHM;
    }
}
