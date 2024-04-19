package genetic.reproductors;

import model.modelos.ModelParameters;
import utils.Tuple;

import java.util.List;

import genetic.GeneticAlgorithmParams;

public interface Reproductor {
    /*
     * @param pais
     *
     * @return filhos gerados a partir dos pais
     */
    public List<Tuple<ModelParameters, Double>> reproduzir(List<Tuple<ModelParameters, Double>> pais);

    public enum Type {
        ASSEXUADO,
        SEXUADO,
        ALEATORIO
    }

    public static Reproductor getReproductor(Type type, GeneticAlgorithmParams params) {
        return switch (type) {
            case ASSEXUADO -> new ReprodutorAssexuado(params);
            case SEXUADO -> new ReprodutorSexuado(params);
            case ALEATORIO -> new ReprodutorAleatorio();
            default -> new ReprodutorSexuado(params);
        };
    }
}
