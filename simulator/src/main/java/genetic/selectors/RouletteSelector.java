package genetic.selectors;

import java.util.ArrayList;
import java.util.List;

import genetic.GeneticAlgorithmParams;
import model.modelos.ModelParameters;
import utils.RandomDoubleSingleton;
import utils.Tuple;

public class RouletteSelector implements Selector {
    private final RandomDoubleSingleton randomGenerator;

    public RouletteSelector(GeneticAlgorithmParams params) {
        this.randomGenerator = RandomDoubleSingleton.getInstance();
    }

    @Override
    public List<Tuple<ModelParameters, Double>> select(List<Tuple<ModelParameters, Double>> population) {
        List<Tuple<ModelParameters, Double>> selectedPopulation = new ArrayList<>();

        // Calculate the total fitness of the population
        double totalFitness = population.stream()
                .mapToDouble(Tuple::getSecond)
                .sum();

        // Calculate the cumulative probabilities
        List<Double> cumulativeProbabilities = new ArrayList<>(population.size());
        double cumulativeProbability = 0.0;
        for (Tuple<ModelParameters, Double> individual : population) {
            double fitness = individual.getSecond();
            double probability = fitness / totalFitness;
            cumulativeProbability += probability;
            cumulativeProbabilities.add(cumulativeProbability);
        }

        // Select individuals using roulette wheel selection
        for (int i = 0; i < population.size(); i++) {
            double randomValue = randomGenerator.nextDouble();
            int selectedIndex = 0;
            for (int j = 0; j < cumulativeProbabilities.size(); j++) {
                if (randomValue <= cumulativeProbabilities.get(j)) {
                    selectedIndex = j;
                    break;
                }
            }
            selectedPopulation.add(population.get(selectedIndex));
        }

        return selectedPopulation;
    }
    // TODO: Revisar se o código acima está correto
}
