package model.reticulado;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.java.Log;
import model.estados.Celula;
import model.estados.Estados;
import model.modelos.Modelo;
import model.terreno.GeradorTerreno;
import model.utils.Tuple;
import model.vento.DirecoesVento;
import model.vento.MatrizVento;

/**
 * This class represents a grid of cells used in a fire spread simulation. It
 * implements the
 * Runnable interface, allowing it to be executed in a separate thread. It uses
 * the observer pattern
 * to notify other objects when the state of the grid changes.
 */
@Getter
@Log
public class Reticulado {

    public final int QNT_ITERACOES;
    @Getter
    private final DirecoesVento direcaoVento;
    private Celula[][] reticulado;
    private int altura;
    private int largura;
    private double umidade;
    @Setter
    private MatrizVento matrizVento;
    private int iteracao;
    @Setter
    private Modelo modelo;
    private Estados tipoInicial;

    /**
     * Constructor for the Reticulado class. Initializes the grid of cells and sets
     * the initial
     * values for the variables.
     */
    public Reticulado(ReticuladoParameters params) {

        if (params.altura() < 16) {
            throw new IllegalArgumentException("Tamanho do reticulado deve ser maior que 16");
        }
        this.QNT_ITERACOES = params.QNT_ITERACOES();
        this.altura = params.altura();
        this.largura = params.largura();
        if (params.umidade() < 0 || params.umidade() > 1) {
            throw new IllegalArgumentException("Umidade deve ser entre 0 e 1");
        }
        this.umidade = params.umidade();
        this.reticulado = new Celula[altura + 1][largura + 1];
        this.matrizVento = new MatrizVento(1, 0.16, 0.03, params.direcaoVento());
        this.direcaoVento = params.direcaoVento();

        setupReticuladoInicial(params.estados(), params.ponto(), params.geradorTerreno());
        this.iteracao = 0;
    }

    public int[][] getReticulado() throws NullPointerException {
        int[][] ret = new int[altura][largura];
        for (int i = 0; i < altura; i++) {
            for (int j = 0; j < largura; j++) {
                if (reticulado[i][j] == null || reticulado[i][j].getEstado() == null)
                    throw new NullPointerException("Celula nula na posição [" + i + "][" + j + "]");
                ret[i][j] = reticulado[i][j].getEstado().VALOR;
            }
        }
        return ret;
    }

    public void avanzarIteracion() {
        int neighborhoodSize = 3; // getRadiusMatrixSize();
        int halfSize = neighborhoodSize / 2;
        Celula aguaCelula = new Celula(Estados.AGUA, this, 0.0,
                new Tuple<>(0, 0)); // Example tuple, adjust as needed

        for (int i = 0; i < altura; ++i) {
            for (int j = 0; j < largura; ++j) {
                Celula[][] neighborhood = new Celula[neighborhoodSize][neighborhoodSize];

                for (int di = -halfSize; di <= halfSize; di++) {
                    for (int dj = -halfSize; dj <= halfSize; dj++) {
                        int neighborI = i + di;
                        int neighborJ = j + dj;

                        if (neighborI < 0 || neighborI >= altura || neighborJ < 0 || neighborJ >= largura) {
                            neighborhood[di + halfSize][dj + halfSize] = aguaCelula;
                            continue;
                        }
                        neighborhood[di + halfSize][dj + halfSize] = reticulado[neighborI][neighborJ];
                    }
                }
                modelo.avanca(neighborhood, matrizVento.getMatrizVento());
            }
        }

        for (int i = 0; i < altura; i++) {
            for (int j = 0; j < largura; j++) {
                reticulado[i][j].reticuladoAvancou();
            }
        }
    }

    /*
     * Runs the simulation for the specified number of iterations. Returns a 3D
     * array containing the state of the grid at each iteration.
     * int[][][] ret = new int[QNT_ITERACOES][altura][largura]
     */
    public int[][][] run() throws IllegalStateException {
        int[][][] ret = new int[QNT_ITERACOES][altura][largura];
        if (modelo == null)
            throw new IllegalStateException("Modelo não foi setado");
        for (int i = 0; i < QNT_ITERACOES; i++) {
            avanzarIteracion();
            ret[i] = getReticulado();
            iteracao = i;
        }

        return ret;
    }

    private void setupReticuladoInicial(
            int[][] estadoInicial, List<Tuple<Integer, Integer>> ponto, GeradorTerreno geradorTerreno) {
        var terreno = geradorTerreno.gerarTerreno(altura, largura);
        for (int i = 0; i < altura + 1; i++) {
            for (int j = 0; j < largura + 1; j++) {
                if (i == altura || j == largura) {
                    var NO_SLOPE_INFLUENCE = 0.0;
                    this.reticulado[i][j] = new Celula(Estados.AGUA, this, NO_SLOPE_INFLUENCE, new Tuple<>(i, j));
                } else {
                    this.reticulado[i][j] = new Celula(Estados.valueOf(estadoInicial[i][j]), this, terreno[i][j],
                            new Tuple<>(i, j));
                }
            }
        }
        for (Tuple<Integer, Integer> p : ponto) {
            this.reticulado[p.i][p.j] = new Celula(Estados.INICIO_FOGO, this, terreno[p.i][p.j],
                    new Tuple<>(p.i, p.j));
        }
    }

    @Override
    public String toString() {
        return "{"
                + "\"direcaoVento\": \""
                + direcaoVento.toString()
                + "\""
                + ", \"QNT_ITERACOES\":"
                + QNT_ITERACOES
                + ", \"altura\":"
                + altura
                + ", \"largura\":"
                + largura
                + ", \"umidade\":"
                + umidade
                + ", \"matrizVento\":"
                + matrizVento
                + ", \"modelo\": "
                + (modelo == null ? "null" : modelo)
                + "}";
    }

}
