package genetic.reproductors;

import model.modelos.ModelParameters;
import model.utils.Tuple;
import java.util.List;

public interface Reproductor {
    /*
     * @param pais
     *
     * @return filhos gerados a partir dos pais
     */
    public List<Tuple<ModelParameters, Double>> reproduzir(List<Tuple<ModelParameters, Double>> pais);
}
