package genetic.selectors;

import java.util.List;

import genetic.GeneticAlgorithmParams;
import model.modelos.ModelParameters;
import utils.Tuple;

public class ElitismSelector implements Selector {

    private final GeneticAlgorithmParams params;

    public ElitismSelector(GeneticAlgorithmParams params) {
        this.params = params;
    }

    @Override
    public List<Tuple<ModelParameters, Double>> select(List<Tuple<ModelParameters, Double>> population) {
        population.sort((a, b) -> b.getSecond().compareTo(a.getSecond()));
        return population.subList(0, params.populationSize());
    }

}
