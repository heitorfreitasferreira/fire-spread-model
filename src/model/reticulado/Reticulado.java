package model.reticulado;

import model.analise.observers.SubPegouFogo;
import model.analise.observers.SubReticuladoAvancou;
import model.analise.observers.SubReticuladoTerminou;
import model.estados.Celula;
import model.estados.Estados;
import model.modelos.Modelo;
import model.utils.Tuple;
import model.vento.DirecoesVento;
import model.vento.MatrizVento;

import java.io.File;
import java.util.ArrayList;

public class Reticulado implements ReticuladoI {

    private Celula[][] reticulado;
    private int size;
    private double umidade;

    private MatrizVento matrizVento;
    private int iteracao;
    public static final int QNT_ITERACOES = 300;
    public static final int QNT_EXECUCOES = 300;

    private Modelo modelo;
    private ArrayList<SubReticuladoAvancou> fofoqueirosAvancou;
    private ArrayList<SubReticuladoTerminou> fofoqueirosTerminou;

    private String tipoInicial;
    public Reticulado(ArrayList<Tuple<Integer,Integer>> ponto, int size, double umidade, DirecoesVento direcaoVento, Modelo modelo, Estados estadoInicial){
        if(size<16) throw new IllegalArgumentException("Tamanho do reticulado deve ser maior que 16");
        this.size = size;
        if(umidade<0 || umidade>1) throw new IllegalArgumentException("Umidade deve ser entre 0 e 1");
        this.umidade = umidade;
        this.reticulado = new Celula[size+1][size];
        this.matrizVento = MatrizVento.getInstance(1, 0.16, 0.03, direcaoVento);
        this.tipoInicial = estadoInicial.NOME;

        fofoqueirosTerminou = new ArrayList<>();
        fofoqueirosAvancou = new ArrayList<>();
        ArrayList<SubPegouFogo> fofoqueirosPegouFogo = new ArrayList<>();

        setupReticuladoInicial(estadoInicial, ponto);
        this.iteracao = 0;
        this.modelo = modelo;
    }

    private void setupReticuladoInicial(Estados estadoInicial, ArrayList<Tuple<Integer,Integer>> ponto){
        for(int i = 0; i < size + 1; i++){
            for(int j = 0; j < size; j++){
                if (i == size){
                    this.reticulado[i][j] = new Celula(Estados.SOLO_EXPOSTO,this);
                }else{
                    this.reticulado[i][j] = new Celula(estadoInicial,this);
                    fofoqueirosAvancou.add(reticulado[i][j]);

                }
            }
        }
        for(Tuple<Integer,Integer> p : ponto){
            this.reticulado[p.i][p.j] = new Celula(Estados.INICIO_FOGO,this);
        }
    }

    //TODO implementar construtor baseado no arquivo JSON
    Reticulado(File file){

    }

    public int getSize() {
        return size;
    }

    public double[][] getMatrizVento() {
        return matrizVento.getMatrizVento();
    }

    public int[][] getReticulado() {
        int[][] ret = new int[size][size];
        for(int i = 0; i < size; i++){
            for(int j = 0; j < size; j++){
                ret[i][j] = reticulado[i][j].getEstado();
            }
        }
        return ret;
    }

    public int getIteracao() {
        return iteracao;
    }
    public int getExecucaoAtual() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    public double getUmidade() {
        return umidade;
    }

    public void setUmidade(double umidade) {
        this.umidade = umidade;
    }

    public Celula getCelula(int i, int j) {
        return this.reticulado[i][j];
    }

    public String getTipoInicial() {
        return tipoInicial;
    }



    public void avanzarIteracion() {
        //CALCULA O ESTADO FUTURO DO RETICULADO
        for (int i = 0; i < size; ++i) {
            for (int j = 0; j < size; ++j) {
                //TODO essa parte ta saindo da matriz
                int up, down, left, right;
                if (j == 0)
                    left = size;
                else
                    left = j - 1;
                right = j + 1;
                if (i == 0)
                    up = size;
                else
                    up = i - 1;
                down = i + 1;

                modelo.avanca(
                        reticulado[up][j],      // n
                        reticulado[down][j],    // s
                        reticulado[i][left],    // o
                        reticulado[i][right],   // l
                        reticulado[up][right],  // ne
                        reticulado[up][left],   // no
                        reticulado[down][right],// se
                        reticulado[down][left], // so,
                        reticulado[i][j]
                );

            }
        }
        reticuladoAvancou(this);
        iteracao++;
    }
    //TODO Execução de uma simulação
    public void run() {
        for (int i = 0; i < QNT_EXECUCOES; i++) {
            for (int j = 0; j < QNT_ITERACOES; j++) {

                avanzarIteracion();
            }
        }
        reticuladoTerminou(this);
    }





    public void reticuladoAvancou(ReticuladoI reticuladoAtual) {
        for (SubReticuladoAvancou subscriber : fofoqueirosAvancou) {
            subscriber.reticuladoAvancou(reticuladoAtual);
        }
    }


    public void reticuladoTerminou(ReticuladoI reticuladoAtual) {
        for (SubReticuladoTerminou subscriber : fofoqueirosTerminou) {
            subscriber.reticuladoTerminou(reticuladoAtual);
        }
    }
}
