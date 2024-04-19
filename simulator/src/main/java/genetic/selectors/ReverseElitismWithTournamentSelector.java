package genetic.selectors;

import java.util.List;

import genetic.GeneticAlgorithmParams;
import model.modelos.ModelParameters;
import utils.Tuple;

public class ReverseElitismWithTournamentSelector implements Selector {

    private final GeneticAlgorithmParams params;
    private TournamentSelector tournamentSelector;

    public ReverseElitismWithTournamentSelector(GeneticAlgorithmParams params) {
        this.params = params;
        this.tournamentSelector = new TournamentSelector(params);
    }

    @Override
    public List<Tuple<ModelParameters, Double>> select(List<Tuple<ModelParameters, Double>> population) {
        population.sort((a, b) -> b.getSecond().compareTo(a.getSecond()));
        // Removing n best individuals
        for (int i = 0; i < (int) (params.reverseElitismPercentage() * params.populationSize()); i++) {
            population.remove(0);
        }
        return tournamentSelector.select(population);

    }

}
