package genetic.selectors;

import java.util.List;

import genetic.GeneticAlgorithmParams;
import model.modelos.ModelParameters;
import utils.RandomDoubleSingleton;
import utils.Tuple;

public class RandomSelector implements Selector {

    private final GeneticAlgorithmParams params;
    private final RandomDoubleSingleton randomGenerator;
    private final List<Selector.Type> selectors;

    public RandomSelector(GeneticAlgorithmParams params) {
        this.params = params;
        this.randomGenerator = RandomDoubleSingleton.getInstance();
        this.selectors = Selector.Type.getAllExceptRandom();
    }

    @Override
    public List<Tuple<ModelParameters, Double>> select(List<Tuple<ModelParameters, Double>> population) {
        int randomSelectorIndex = randomGenerator.nextInt(selectors.size());
        Selector.Type selectorType = selectors.get(randomSelectorIndex);
        Selector chosenSelector = Selector.getSelector(selectorType, params);

        return chosenSelector.select(population);
    }

}
