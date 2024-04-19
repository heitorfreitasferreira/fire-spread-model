package genetic.selectors;

import java.util.ArrayList;
import java.util.List;

import genetic.GeneticAlgorithmParams;
import model.modelos.ModelParameters;
import utils.ProgressBarSingleton;
import utils.RandomDoubleSingleton;
import utils.Tuple;

public class TournamentSelector implements Selector {
    private final GeneticAlgorithmParams params;
    private final RandomDoubleSingleton randomGenerator;

    public TournamentSelector(GeneticAlgorithmParams params) {
        this.params = params;
        this.randomGenerator = RandomDoubleSingleton.getInstance();
    }

    @Override
    public List<Tuple<ModelParameters, Double>> select(List<Tuple<ModelParameters, Double>> population) {
        List<Tuple<ModelParameters, Double>> newPopulation = new ArrayList<>(params.populationSize());
        while (newPopulation.size() < params.populationSize()) {
            ProgressBarSingleton.getInstance(0).step();

            List<Tuple<ModelParameters, Double>> tournament = new ArrayList<>();
            for (int j = 0; j < params.tournamentSize(); j++) {
                tournament.add(population.get(randomGenerator.nextInt(params.populationSize())));
            }

            tournament.sort((a, b) -> b.getSecond().compareTo(a.getSecond()));
            var bestIndividual = tournament.get(0);
            newPopulation.add(bestIndividual);
        }
        return newPopulation;
    }

}
