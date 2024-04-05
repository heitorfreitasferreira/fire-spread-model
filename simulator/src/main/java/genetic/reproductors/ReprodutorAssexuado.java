package genetic.reproductors;

import java.util.List;
import java.util.stream.Collectors;

import genetic.EvolutiveStrategy;
import genetic.GeneticAlgorithmParams;
import model.modelos.ModelParameters;
import model.utils.ProgressBarSingleton;
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
        List<Tuple<ModelParameters, Double>> filhos = pais.parallelStream()
                .map(pai -> {
                    ProgressBarSingleton.getInstance(0).step();
                    return new Tuple<>(getChild(pai.getFirst()), EvolutiveStrategy.INVALID_FITNESS);
                })
                .collect(Collectors.toList());
        return filhos;
    }

    private ModelParameters getChild(ModelParameters parent) {
        return new ModelParameters(
                parent.influenciaUmidade()
                        + randomGenerator.nextDouble(-params.mutationRate(),
                                params.mutationRate()),
                parent.probEspalhamentoFogoInicial()
                        + randomGenerator.nextDouble(-params.mutationRate(),
                                params.mutationRate()),
                parent.probEspalhamentoFogoArvoreQueimando()
                        + randomGenerator.nextDouble(-params.mutationRate(),
                                params.mutationRate()),
                parent.probEspalhamentoFogoQueimaLenta()
                        + randomGenerator.nextDouble(-params.mutationRate(),
                                params.mutationRate()),
                parent.influenciaVegetacaoCampestre()
                        + randomGenerator.nextDouble(-params.mutationRate(),
                                params.mutationRate()),
                parent.influenciaVegetacaoSavanica()
                        + randomGenerator.nextDouble(-params.mutationRate(),
                                params.mutationRate()),
                parent.influenciaVegetacaoFlorestal()
                        + randomGenerator.nextDouble(-params.mutationRate(),
                                params.mutationRate()));
    }
}