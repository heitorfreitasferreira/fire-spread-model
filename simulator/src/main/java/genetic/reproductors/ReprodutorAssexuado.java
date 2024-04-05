package genetic.reproductors;

import java.util.List;

import genetic.EvolutiveStrategy;
import genetic.GeneticAlgorithmParams;

import java.util.ArrayList;

import model.modelos.ModelParameters;
import model.utils.RandomDoubleSingleton;
import model.utils.Tuple;

public class ReprodutorAssexuado implements Reproductor {
    private GeneticAlgorithmParams params;
    private RandomDoubleSingleton randomGenerator;

    public ReprodutorAssexuado(GeneticAlgorithmParams params) {
        this.randomGenerator = RandomDoubleSingleton.getInstance();
        this.params = params;
    }

    public List<Tuple<ModelParameters, Double>> reproduzir(List<Tuple<ModelParameters, Double>> pais) {
        List<Tuple<ModelParameters, Double>> filhos = new ArrayList<>();

        for (int i = 0; i < params.populationSize(); i++) {
            ModelParameters modelParameters = pais.get(i).getFirst();
            ModelParameters newModelParameters = new ModelParameters(
                    modelParameters.influenciaUmidade()
                            + randomGenerator.nextDouble(-params.mutationRate(),
                                    params.mutationRate()),
                    modelParameters.probEspalhamentoFogoInicial()
                            + randomGenerator.nextDouble(-params.mutationRate(),
                                    params.mutationRate()),
                    modelParameters.probEspalhamentoFogoArvoreQueimando()
                            + randomGenerator.nextDouble(-params.mutationRate(),
                                    params.mutationRate()),
                    modelParameters.probEspalhamentoFogoQueimaLenta()
                            + randomGenerator.nextDouble(-params.mutationRate(),
                                    params.mutationRate()),
                    modelParameters.influenciaVegetacaoCampestre()
                            + randomGenerator.nextDouble(-params.mutationRate(),
                                    params.mutationRate()),
                    modelParameters.influenciaVegetacaoSavanica()
                            + randomGenerator.nextDouble(-params.mutationRate(),
                                    params.mutationRate()),
                    modelParameters.influenciaVegetacaoFlorestal()
                            + randomGenerator.nextDouble(-params.mutationRate(),
                                    params.mutationRate()));
            filhos.add(new Tuple<>(newModelParameters, EvolutiveStrategy.INVALID_FITNESS));
        }
        return filhos;
    }
}