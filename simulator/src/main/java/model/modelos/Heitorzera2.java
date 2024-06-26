package model.modelos;

import java.util.EnumMap;

import model.estados.Celula;
import model.estados.Estados;
import model.reticulado.Reticulado;
import utils.RandomDoubleSingleton;

/**
 * This class represents a model for a fire spread simulation. It uses a
 * cellular automaton
 * approach, where each cell can be in a certain state and the transition
 * between states is
 * determined by certain rules.
 */
public class Heitorzera2 implements Modelo {

    // Maps to store the probabilities of fire spread, influence of vegetation,
    // state transitions and time spent in each state
    protected EnumMap<Estados, Double> probEspalhamentoFogo;
    protected EnumMap<Estados, Double> influenciaVegetacao;
    protected EnumMap<Estados, Estados> transicao;
    protected EnumMap<Estados, Integer> tempoNoEstado;

    // The grid of cells
    protected Reticulado reticulado;

    // Variables to store the influence of humidity and the probabilities of fire
    // spread from surface to subsurface and vice versa
    protected double influenciaUmidade;
    protected double probPassarSubPraSuperficie;
    protected double probPassarSuperficiePraSub;

    protected RandomDoubleSingleton randomGenerator;

    /**
     * Constructor for the Heitorzera2 model. Initializes the grid of cells and sets
     * the initial
     * values for the variables.
     */
    public Heitorzera2(ModelParameters params) {
        this.randomGenerator = RandomDoubleSingleton.getInstance();
        setInfluenciaVegetacao(params);
        setProbEspalhamentoFogo(params);
        setTransicoes();
        setTempoNoEstado();
        setInfluenciaUmidade(params);
    }

    /**
     * Advances the state of the central cell in the neighborhood based on the
     * current state and the
     * states of the neighboring cells.
     */
    public void avanca(Celula[][] neighborhood, double[][] matrizVento) {
        Celula central = neighborhood[neighborhood.length / 2][neighborhood[0].length / 2];

        if (Boolean.TRUE.equals(central.getEstado().isFogo())) {
            avancaFogo(central);
        }
        avancarQueimavel(central, generateHasQueimaMatriz(neighborhood), neighborhood, matrizVento);
    }

    /**
     * Sets the time spent in each state based on the humidity of the grid.
     */
    protected void setTempoNoEstado() {
        this.tempoNoEstado = new EnumMap<>(Estados.class);
        // if (reticulado.getUmidade() <= 0.25) {
        // tempoNoEstado.put(Estados.INICIO_FOGO, 3);
        // tempoNoEstado.put(Estados.ARVORE_QUEIMANDO, 8);
        // tempoNoEstado.put(Estados.QUEIMA_LENTA, 7);
        // } else {
        tempoNoEstado.put(Estados.INICIO_FOGO, 3);
        tempoNoEstado.put(Estados.ARVORE_QUEIMANDO, 3);
        tempoNoEstado.put(Estados.QUEIMA_LENTA, 10);
        // }
    }

    /**
     * Sets the influence of humidity based on the humidity of the grid.
     */
    protected void setInfluenciaUmidade(ModelParameters parameters) throws IllegalStateException {
        // if (reticulado.getUmidade() > 0.0 && reticulado.getUmidade() <= 0.25) {
        // this.influenciaUmidade = 1.5;
        // } else if (reticulado.getUmidade() > 0.25 && reticulado.getUmidade() <= 0.5)
        // {
        // this.influenciaUmidade = 1.0;
        // } else if (reticulado.getUmidade() > 0.5 && reticulado.getUmidade() <= 0.75)
        // {
        // this.influenciaUmidade = 0.8;
        // } else if (reticulado.getUmidade() > 0.75 && reticulado.getUmidade() <= 1.0)
        // {
        // this.influenciaUmidade = 0.6;
        // } else {
        // throw new IllegalStateException("Umidade fora do intervalo [0,1]");
        // }
        this.influenciaUmidade = parameters.influenciaUmidade();
    }

    /**
     * Returns a matrix indicating whether each cell in the neighborhood is in the
     * given state.
     */
    protected boolean[][] getMatrizQueima(Celula[][] neighborhood, Estados estado) {
        int size = neighborhood.length;
        boolean[][] matrizQueima = new boolean[size][size];

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                matrizQueima[i][j] = (i != size / 2 || j != size / 2) && neighborhood[i][j].getEstado() == estado;
            }
        }

        return matrizQueima;
    }

    /**
     * Returns the sum of the values in the matrix.
     */
    protected int sumMatriz(boolean[][] matriz) {
        int sum = 0;
        for (boolean[] booleanList : matriz) {
            for (boolean isBurned : booleanList) {
                sum += isBurned ? 1 : 0;
            }
        }
        return sum;
    }

    /**
     * Advances the state of a cell that is on fire.
     */
    protected void avancaFogo(Celula celula) {
        if (celula.getTempoNoEstado() >= tempoNoEstado.get(celula.getEstado())) {
            celula.proxEstado(transicao.get(celula.getEstado()));
        }
    }

    /**
     * Returns the influence of the relief on the spread of fire from the central
     * cell to the cell
     * where the fire is.
     */
    protected double influenciaRelevo(Celula central, Celula ondeTemFogo) {
        return 1;
    }

    /**
     * Returns whether a cell is flammable based on the states of the neighboring
     * cells.
     */
    protected boolean isQueimavel(Celula celula, EnumMap<Estados, boolean[][]> estadoMatriz) {

        return celula.getEstado().isVegetacao()
                &&
                (sumMatriz(estadoMatriz.get(Estados.INICIO_FOGO)) > 0
                        || sumMatriz(estadoMatriz.get(Estados.ARVORE_QUEIMANDO)) > 0
                        || sumMatriz(estadoMatriz.get(Estados.QUEIMA_LENTA)) > 0);
    }

    /**
     * Returns a matrix of random values.
     */
    protected double[][] getRandomMatriz() {
        int neighborhoodSize = 3;// reticulado.getRadiusMatrixSize();
        double[][] matriz = new double[neighborhoodSize][neighborhoodSize];
        for (int i = 0; i < matriz.length; i++) {
            for (int j = 0; j < matriz[i].length; j++) {
                if (i == 1 && j == 1) {
                    matriz[i][j] = 0;
                } else {
                    matriz[i][j] = randomGenerator.nextDouble();
                }
            }
        }
        return matriz;
    }

    /**
     * Advances the state of a flammable cell based on the states of the neighboring
     * cells and the
     * random values in the probability matrix.
     */
    protected void avancarQueimavel(
            Celula celula, EnumMap<Estados, boolean[][]> estadoMatriz, Celula[][] neighborhood,
            double[][] matrizVento) {
        if (!isQueimavel(celula, estadoMatriz)) {
            return;
        }
        double[][] probMatriz = getRandomMatriz();

        int neighborhoodSize = neighborhood.length;// reticulado.getRadiusMatrixSize()

        for (Estados estado : Estados.getEstadosDeFogo()) {
            for (int i = 0; i < neighborhoodSize; i++) {
                for (int j = 0; j < neighborhoodSize; j++) {
                    if (estadoMatriz.get(estado)[i][j]) // Vendo se há fogo daquele tipo naquela posição
                    {
                        if (probMatriz[i][j] < probEspalhamentoFogo.get(estado)
                                * influenciaVegetacao.get(celula.getEstado())
                                * matrizVento[i][j]
                                * influenciaUmidade
                                * influenciaRelevo(celula, neighborhood[i][j])) {
                            celula.proxEstado(transicao.get(celula.getEstado()));
                        }
                    }
                }
            }
        }
    }

    /**
     * Returns a map of matrices indicating whether each cell in the neighborhood is
     * in each state.
     */
    protected EnumMap<Estados, boolean[][]> generateHasQueimaMatriz(Celula[][] neighborhood) {
        EnumMap<Estados, boolean[][]> estadoMatriz = new EnumMap<>(Estados.class);
        for (Estados estado : Estados.getEstadosDeFogo()) {
            estadoMatriz.put(estado, getMatrizQueima(neighborhood, estado));
        }
        return estadoMatriz;
    }

    /**
     * Sets the influence of vegetation on the spread of fire.
     */
    private void setInfluenciaVegetacao(ModelParameters parameters) {
        this.influenciaVegetacao = new EnumMap<>(Estados.class);
        influenciaVegetacao.put(Estados.CAMPESTRE, parameters.influenciaVegetacaoCampestre());
        influenciaVegetacao.put(Estados.SAVANICA, parameters.influenciaVegetacaoSavanica());
        influenciaVegetacao.put(Estados.FLORESTAL, parameters.influenciaVegetacaoFlorestal());
    }

    /**
     * Sets the probabilities of fire spread for each state.
     */
    private void setProbEspalhamentoFogo(ModelParameters parameters) {
        this.probEspalhamentoFogo = new EnumMap<>(Estados.class);
        probEspalhamentoFogo.put(Estados.INICIO_FOGO, parameters.probEspalhamentoFogoInicial());
        probEspalhamentoFogo.put(Estados.ARVORE_QUEIMANDO, parameters.probEspalhamentoFogoArvoreQueimando());
        probEspalhamentoFogo.put(Estados.QUEIMA_LENTA, parameters.probEspalhamentoFogoQueimaLenta());
        probEspalhamentoFogo.put(Estados.CAMPESTRE, 0.0);
        probEspalhamentoFogo.put(Estados.SAVANICA, 0.0);
        probEspalhamentoFogo.put(Estados.FLORESTAL, 0.0);
        probEspalhamentoFogo.put(Estados.RAIZ, 0.0);
        probEspalhamentoFogo.put(Estados.AGUA, 0.0);
        probEspalhamentoFogo.put(Estados.SOLO_EXPOSTO, 0.0);
    }

    /**
     * Sets the state transitions for each state.
     */
    private void setTransicoes() {
        this.transicao = new EnumMap<>(Estados.class);
        transicao.put(Estados.SAVANICA, Estados.INICIO_FOGO);
        transicao.put(Estados.FLORESTAL, Estados.INICIO_FOGO);
        transicao.put(Estados.CAMPESTRE, Estados.INICIO_FOGO);
        transicao.put(Estados.INICIO_FOGO, Estados.ARVORE_QUEIMANDO);
        transicao.put(Estados.ARVORE_QUEIMANDO, Estados.QUEIMA_LENTA);
        transicao.put(Estados.AGUA, Estados.AGUA);
        transicao.put(Estados.QUEIMA_LENTA, Estados.SOLO_EXPOSTO);
        transicao.put(Estados.RAIZ, Estados.SOLO_EXPOSTO);
        // TODO: Ver com o Luiz se esse comportamento faz sentido (problema: solo
        // exposto irá transicionar para vegetação??)
    }

    @Override
    public String toString() {
        return "{" +
                "\"nome\": " +
                "\"" + this.getClass().getName() + "\"" +
                ", \"probEspalhamentoFogo\": \"" + probEspalhamentoFogo +
                "\", \"influenciaVegetacao\": \"" + influenciaVegetacao +
                "\", \"transicao\": \"" + transicao +
                "\", \"tempoNoEstado\": \"" + tempoNoEstado +
                "\", \"influenciaUmidade\": " + influenciaUmidade +
                ", \"probPassarSubPraSuperficie\": " + probPassarSubPraSuperficie +
                ", \"probPassarSuperficiePraSub\": " + probPassarSuperficiePraSub +
                '}';
    }
}
