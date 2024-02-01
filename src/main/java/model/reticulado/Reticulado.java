package model.reticulado;

import com.google.gson.JsonObject;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.java.Log;
import model.analise.Bruto;
import model.analise.observers.SubPegouFogo;
import model.analise.observers.SubReticuladoAvancou;
import model.analise.observers.SubReticuladoTerminou;
import model.estados.Celula;
import model.estados.Estados;
import model.modelos.Heitorzera2;
import model.modelos.Modelo;
import model.terreno.GeradorLateral;
import model.terreno.GeradorTerreno;
import model.utils.Tuple;
import model.vento.DirecoesVento;
import model.vento.MatrizVento;

/**
 * This class represents a grid of cells used in a fire spread simulation. It implements the
 * Runnable interface, allowing it to be executed in a separate thread. It uses the observer pattern
 * to notify other objects when the state of the grid changes.
 */
@Getter
@Log
public class Reticulado implements Runnable {

    public static final int QNT_ITERACOES = 100;
    public static final int QNT_EXECUCOES = 10;
    @Getter
    private final DirecoesVento direcaoVento;
    public List<SubPegouFogo> fofoqueirosPegouFogo;
    private Celula[][] reticulado;
    private int altura;
    private int largura;
    private double umidade;
    @Setter
    private MatrizVento matrizVento;
    private int iteracao;
    private int execucaoAtual;
    @Setter
    private Modelo modelo;
    @Setter
    private Optional<Reticulado> reticuladoSubsolo;
    private List<SubReticuladoAvancou> fofoqueirosAvancou;
    private List<SubReticuladoTerminou> fofoqueirosTerminou;
    private Estados tipoInicial;


    @Override
    public String toString() {
        return "{"
            + "\"direcaoVento\": \""
            + direcaoVento.toString()
            + "\""
            + ", \"QNT_ITERACOES\":"
            + QNT_ITERACOES
            + ", \"QNT_EXECUCOES\":"
            + QNT_EXECUCOES
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
            + ", \"tipoInicial\": \""
            + tipoInicial
            + "\" }";
    }

    /**
     * Constructor for the Reticulado class. Initializes the grid of cells and sets the initial
     * values for the variables.
     */
    public Reticulado(
        List<Tuple<Integer, Integer>> ponto,
        int altura,
        int largura,
        double umidade,
        DirecoesVento direcaoVento,
        Estados estadoInicial,
        GeradorTerreno geradorTerreno,
        boolean hasSubsolo) {

        if (altura < 16) {
            throw new IllegalArgumentException("Tamanho do reticulado deve ser maior que 16");
        }
        this.altura = altura;
        this.largura = largura;
        if (umidade < 0 || umidade > 1) {
            throw new IllegalArgumentException("Umidade deve ser entre 0 e 1");
        }
        this.umidade = umidade;
        this.reticulado = new Celula[altura + 1][largura + 1];
        this.matrizVento = new MatrizVento(1, 0.16, 0.03, direcaoVento);
        this.direcaoVento = direcaoVento;

        fofoqueirosTerminou = new ArrayList<>();
        fofoqueirosAvancou = new ArrayList<>();
        fofoqueirosPegouFogo = new ArrayList<>();
        fofoqueirosAvancou.add(new Bruto(this, getFolderName()));
        //        var est = new Estatistico(this, "estatistico");
        //        fofoqueirosAvancou.add(est);
        //        fofoqueirosPegouFogo.add(est);

        if (hasSubsolo) {
            setupReticuladoSubsolo();
        } else {
            reticuladoSubsolo = Optional.empty();
        }
        this.tipoInicial = estadoInicial;
        setupReticuladoInicial(estadoInicial, ponto, geradorTerreno);
        this.iteracao = 0;
    }

    private Reticulado(
        List<Tuple<Integer, Integer>> ponto,
        int altura,
        int largura,
        double umidade,
        DirecoesVento direcaoVento,
        int[][] pontos,
        GeradorTerreno geradorTerreno,
        String output) {
        if (altura < 16) {
            throw new IllegalArgumentException("Tamanho do reticulado deve ser maior que 16");
        }
        this.altura = altura;
        this.largura = largura;
        if (umidade < 0 || umidade > 1) {
            throw new IllegalArgumentException("Umidade deve ser entre 0 e 1");
        }
        this.umidade = umidade;
        this.reticulado = new Celula[altura + 1][largura + 1];
        this.matrizVento = MatrizVento.getInstance(1, 0.16, 0.03, direcaoVento);
        this.direcaoVento = direcaoVento;

        fofoqueirosTerminou = new ArrayList<>();
        fofoqueirosAvancou = new ArrayList<>();
        fofoqueirosPegouFogo = new ArrayList<>();
        fofoqueirosAvancou.add(new Bruto(this, output));
        setupReticuladoInicial(pontos, ponto, geradorTerreno);
        this.iteracao = 0;
    }

    /**
     * { "initialPoints": [ { "x": 1, "y": 1 }, { "x": 1, "y": 2 }, ], "windDirection": "N",
     * "humidity": 0.5, "terrain": [ [1,2,3,4,5], [1,2,3,4,5], [1,2,3,4,5], [1,2,3,4,5], ] }
     *
     * @param json
     */
    public static Reticulado fromJson(JsonObject json, String outputPath) {
        // Get parameters from json:

        // Initial points
        var initialPoints = new ArrayList<Tuple<Integer, Integer>>();
        var points = json.get("initialPoints").getAsJsonArray();
        for (var point : points) {
            var x = point.getAsJsonObject().get("x").getAsInt();
            var y = point.getAsJsonObject().get("y").getAsInt();
            initialPoints.add(new Tuple<>(x, y));
        }

        // Wind direction
        var windDirection = DirecoesVento.valueOf(json.get("windDirection").getAsString());

        // Humidity
        var humidity = json.get("humidity").getAsDouble();

        // Terrain. Is a 2D array of integers of unknown size.
        var terrain = json.get("terrain").getAsJsonArray();
        var terrainArray = new int[terrain.size()][terrain.get(0).getAsJsonArray().size()];
        for (int i = 0; i < terrain.size(); i++) {
            var row = terrain.get(i).getAsJsonArray();
            for (int j = 0; j < row.size(); j++) {
                terrainArray[i][j] = row.get(j).getAsInt();
            }
        }

        // Do not print terrain, it is too big.
        log.info("Initial points: " + initialPoints);
        log.info("Wind direction: " + windDirection);
        log.info("Humidity: " + humidity);

        return new Reticulado(
            initialPoints,
            terrainArray.length,
            terrainArray[0].length,
            humidity,
            windDirection,
            terrainArray,
            new GeradorLateral(),
            outputPath);
    }


    public double[][] getMatrizVento() {
        return matrizVento.getMatrizVento();
    }

    public Celula getCelula(int i, int j) {
        return reticulado[i][j];
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
        int neighborhoodSize = getRadiusMatrixSize();
        int halfSize = neighborhoodSize / 2;
        Celula aguaCelula =
            new Celula(Estados.AGUA, this, 0.0,
                new Tuple<>(0, 0)); // Example tuple, adjust as needed

        for (int i = 0; i < altura; ++i) {
            for (int j = 0; j < largura; ++j) {
                Celula[][] neighborhood = new Celula[neighborhoodSize][neighborhoodSize];

                for (int di = -halfSize; di <= halfSize; di++) {
                    for (int dj = -halfSize; dj <= halfSize; dj++) {
                        int neighborI = i + di;
                        int neighborJ = j + dj;

                        neighborhood[di + halfSize][dj + halfSize] =
                            (neighborI < 0 || neighborI >= altura || neighborJ < 0
                                || neighborJ >= largura)
                                ? aguaCelula
                                : reticulado[neighborI][neighborJ];
                    }
                }
                modelo.avanca(neighborhood);
            }
        }

        reticuladoSubsolo.ifPresent(
            it -> {
                it.avanzarIteracion();
                modelo.mergeSubsolo(it);
            });

        try {
            reticuladoAvancou();
        } catch (IOException e) {
            log.severe("Erro ao avisar os fofoqueiros");
        }
    }

    public void run() throws IllegalStateException {
        this.execucaoAtual = 0;
        if (modelo == null) throw new IllegalStateException("Modelo não foi setado");
        for (int i = 0; i < QNT_EXECUCOES; i++) {
            for (int j = 0; j < QNT_ITERACOES; j++) {
                avanzarIteracion();
                iteracao = j;
            }
            reticuladoTerminou();
            this.execucaoAtual++;
        }
    }

    /**
     * Avisa as celulas para trocar o estado
     *
     * @throws IOException
     */
    public void reticuladoAvancou() throws IOException {
        for (SubReticuladoAvancou subscriber : fofoqueirosAvancou) {
            subscriber.reticuladoAvancou();
        }
    }

    public void reticuladoTerminou() {
        for (SubReticuladoTerminou subscriber : fofoqueirosTerminou) {
            subscriber.reticuladoTerminou();
        }
    }

    public void reticuladoPegouFogo(int i, int j) {
        for (SubPegouFogo subscriber : fofoqueirosPegouFogo) {
            subscriber.pegouFogo(i, j);
        }
    }

    public int getRadiusMatrixSize() throws IllegalStateException {
        if (modelo == null) {
            throw new IllegalStateException("Modelo não foi setado");
        }

        var raioDoModelo = modelo.getRadius();

        if (raioDoModelo == 1) {
            return 3;
        } else if (raioDoModelo == 2) {
            return 5;
        }
        throw new IllegalStateException("Tamanho de vizinhança não suportado");
    }

    protected String getFolderName() {
        var formatter = new SimpleDateFormat("[dd-MM-yyyy_HH-mm]");
        return "reticulados/"
            + this.getTipoInicial()
            + "/"
            + this.getUmidade()
            + "/"
            + this.getDirecaoVento()
            + "/"
            + formatter.format(new Date())
            + "/simulation_"
            + this.getExecucaoAtual();
    }

    private void setupReticuladoSubsolo() {
        var subsolo =
            new Reticulado(
                List.of(),
                altura,
                largura,
                1,
                DirecoesVento.N,
                Estados.SAVANICA,
                new GeradorLateral(),
                false);
        Modelo modeloSub = new Heitorzera2(subsolo);
        subsolo.setModelo(modeloSub);
        subsolo.setMatrizVento(new MatrizVento(0.5, 0.16, 0.03, DirecoesVento.N));
        this.reticuladoSubsolo = Optional.of(subsolo);
    }

    private void setupReticuladoInicial(
        Estados estadoInicial, List<Tuple<Integer, Integer>> ponto, GeradorTerreno geradorTerreno) {
        var terreno = geradorTerreno.gerarTerreno(altura, largura);
        for (int i = 0; i < altura + 1; i++) {
            for (int j = 0; j < largura + 1; j++) {
                if (i == altura || j == largura) {
                    var NO_SLOPE_INFLUENCE = 0.0;
                    this.reticulado[i][j] =
                        new Celula(Estados.AGUA, this, NO_SLOPE_INFLUENCE, new Tuple<>(i, j));
                } else {
                    this.reticulado[i][j] = new Celula(estadoInicial, this, terreno[i][j],
                        new Tuple<>(i, j));
                    fofoqueirosAvancou.add(reticulado[i][j]);
                }
            }
        }
        for (Tuple<Integer, Integer> p : ponto) {
            this.reticulado[p.i][p.j].proxEstado(Estados.INICIO_FOGO);
        }
    }

    private void setupReticuladoInicial(
        int[][] pontos, List<Tuple<Integer, Integer>> ponto, GeradorTerreno geradorTerreno) {
        var terreno = geradorTerreno.gerarTerreno(altura + 1, largura + 1);
        for (int i = 0; i < altura + 1; i++) {
            for (int j = 0; j < largura + 1; j++) {
                if (i == altura || j == largura) {
                    var NO_SLOPE_INFLUENCE = 0.0;
                    this.reticulado[i][j] =
                        new Celula(Estados.AGUA, this, NO_SLOPE_INFLUENCE, new Tuple<>(i, j));
                } else {
                    this.reticulado[i][j] =
                        new Celula(Estados.valueOf(pontos[i][j]), this, terreno[i][j],
                            new Tuple<>(i, j));
                    fofoqueirosAvancou.add(reticulado[i][j]);
                }
            }
        }
        for (Tuple<Integer, Integer> p : ponto) {
            this.reticulado[p.i][p.j].proxEstado(Estados.INICIO_FOGO);
        }
    }
}
