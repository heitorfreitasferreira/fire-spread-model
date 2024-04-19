package genetic;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import lombok.extern.java.Log;
import model.modelos.ModelParameters;
import utils.Tuple;

@Log
public class Statistics {
    List<Double> bestFitness;
    List<Double> averageFitness;
    List<Double> worstFitness;
    List<Double> standardDeviation;
    List<ModelParameters> bestParameters;

    public Statistics() {
        bestFitness = new ArrayList<>();
        averageFitness = new ArrayList<>();
        worstFitness = new ArrayList<>();
        standardDeviation = new ArrayList<>();
        bestParameters = new ArrayList<>();
    }

    public void logToFile(GeneticAlgorithmParams params) {
        Path directory = Paths.get("../model_evolution");
        try {
            if (!Files.exists(directory)) {
                Files.createDirectory(directory);
            }
        } catch (IOException e) {
            log.severe("Error creating directory");
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
        String filename = "statistics_" +
        // params.numberOfGenerations() +
        // "gens_" + params.populationSize() +
        // "pop_" + params.tournamentSize() +
        // "k_" + params.mutationRate() +
        // "mutRate_" +
        // "blxalpha_" +
        // params.crossoverBlxAlpha() +
        // "date_" +
                LocalDateTime.now().format(formatter) +
                ".csv";
        String metadata = "#" + params.toString() + "\n";
        Path file = directory.resolve(filename);
        try (BufferedWriter writer = Files.newBufferedWriter(file)) {
            writer.write(metadata);
            writer.write("Best Fitness,Average Fitness,Worst Fitness,Standard Deviation,Best Parameters\n");
            for (int i = 0; i < bestFitness.size(); i++) {
                writer.write(bestFitness.get(i) + ",");
                writer.write(averageFitness.get(i) + ",");
                writer.write(worstFitness.get(i) + ",");
                writer.write(standardDeviation.get(i) + ",");
                writer.write(bestParameters.get(i).toString() + "\n");
            }
        } catch (IOException e) {
            log.severe("Error writing statistics to file");
        }
    }

    public void updateStatistics(List<Tuple<ModelParameters, Double>> population) {
        double best = Double.MIN_VALUE;
        double worst = Double.MAX_VALUE;
        double sum = 0;
        for (Tuple<ModelParameters, Double> tuple : population) {
            if (tuple.getSecond() > best) {
                best = tuple.getSecond();
            }
            if (tuple.getSecond() < worst && !Objects.equals(tuple.getSecond(), EvolutiveStrategy.ZERO_FITNESS)) {
                worst = tuple.getSecond();
            }
            sum += tuple.getSecond();
        }
        bestFitness.add(best);
        worstFitness.add(worst);
        averageFitness.add(sum / population.size());
        double variance = 0;
        for (Tuple<ModelParameters, Double> tuple : population) {
            variance += Math.pow(tuple.getSecond() - averageFitness.get(averageFitness.size() - 1), 2);
        }
        standardDeviation.add(Math.sqrt(variance / population.size()));
        bestParameters.add(population.get(0).getFirst());
    }
}
