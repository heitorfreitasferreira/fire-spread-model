package genetic.reproductors;

import java.util.List;

import genetic.EvolutiveStrategy;
import model.modelos.ModelParameters;
import model.utils.ProgressBarSingleton;
import model.utils.RandomDoubleSingleton;
import model.utils.Tuple;

public class ReprodutorAleatorio implements Reproductor {
    private RandomDoubleSingleton randomGenerator;

    public ReprodutorAleatorio() {
        this.randomGenerator = RandomDoubleSingleton.getInstance();
    }

    @Override
    public List<Tuple<ModelParameters, Double>> reproduzir(List<Tuple<ModelParameters, Double>> pais) {
        var popSize = pais.size();
        for (int i = 0; i < popSize; i++) {
            pais.remove(0);
        }
        for (int i = 0; i < popSize; i++) {
            ProgressBarSingleton.getInstance(0).step();
            pais.add(new Tuple<>(new ModelParameters(
                    randomGenerator.nextDouble(),
                    randomGenerator.nextDouble(),
                    randomGenerator.nextDouble(),
                    randomGenerator.nextDouble(),
                    randomGenerator.nextDouble(),
                    randomGenerator.nextDouble(),
                    randomGenerator.nextDouble()), EvolutiveStrategy.INVALID_FITNESS));
        }
        return List.of();
    }
}
