package model.reticulado;

import lombok.Getter;
import lombok.Setter;
import model.analise.Bruto;
import model.analise.Estatistico;
import model.analise.observers.SubPegouFogo;
import model.analise.observers.SubReticuladoAvancou;
import model.analise.observers.SubReticuladoTerminou;
import model.estados.Celula;
import model.estados.Estados;
import model.modelos.Modelo;
import model.terreno.GeradorTerreno;
import model.utils.Tuple;
import model.vento.DirecoesVento;
import model.vento.MatrizVento;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

@Getter
public class Reticulado implements ReticuladoI {

    private Celula[][] reticulado;
    private int size;
    private double umidade;

    private MatrizVento matrizVento;
    private int iteracao;
    public static final int QNT_ITERACOES = 100;
    public static final int QNT_EXECUCOES = 10;
    private int execucaoAtual;

    private Modelo modelo;
    private ArrayList<SubReticuladoAvancou> fofoqueirosAvancou;
    private ArrayList<SubReticuladoTerminou> fofoqueirosTerminou;
    public ArrayList<SubPegouFogo> fofoqueirosPegouFogo;

    private String tipoInicial;
    private String direcaoVento;
    public Reticulado(ArrayList<Tuple<Integer,Integer>> ponto, int size, double umidade, DirecoesVento direcaoVento, Estados estadoInicial, GeradorTerreno geradorTerreno){
        if(size<16) throw new IllegalArgumentException("Tamanho do reticulado deve ser maior que 16");
        this.size = size;
        if(umidade<0 || umidade>1) throw new IllegalArgumentException("Umidade deve ser entre 0 e 1");
        this.umidade = umidade;
        this.reticulado = new Celula[size+1][size+1];
        this.matrizVento = MatrizVento.getInstance(1, 0.16, 0.03, direcaoVento);
        this.direcaoVento = direcaoVento.toString();
        this.tipoInicial = estadoInicial.NOME;

        fofoqueirosTerminou = new ArrayList<>();
        fofoqueirosAvancou = new ArrayList<>();
        fofoqueirosPegouFogo = new ArrayList<>();
        fofoqueirosAvancou.add(new Bruto(this, "bruto"));
//        var est = new Estatistico(this, "estatistico");
//        fofoqueirosAvancou.add(est);
//        fofoqueirosPegouFogo.add(est);
        ArrayList<SubPegouFogo> fofoqueirosPegouFogo = new ArrayList<>();

        setupReticuladoInicial(estadoInicial, ponto, geradorTerreno);
        this.iteracao = 0;
        //this.modelo = modelo;

    }

    public String getDirecaoVento() {
        return direcaoVento;
    }

    private void setupReticuladoInicial(Estados estadoInicial, ArrayList<Tuple<Integer,Integer>> ponto, GeradorTerreno geradorTerreno){
        var terreno = geradorTerreno.gerarTerreno(size);
        for(int i = 0; i < size + 1; i++){
            for(int j = 0; j < size+ 1 ; j++){
                if (i == size || j == size){
                    var NO_SLOPE_INFLUENCE = 0.0;
                    this.reticulado[i][j] = new Celula(Estados.AGUA,this, NO_SLOPE_INFLUENCE, new Tuple<>(i,j));
                }else{
                    this.reticulado[i][j] = new Celula(estadoInicial,this, terreno[i][j], new Tuple<>(i,j));
                    fofoqueirosAvancou.add(reticulado[i][j]);
                }
            }
        }
        for(Tuple<Integer,Integer> p : ponto){
            this.reticulado[p.i][p.j].proxEstado(Estados.INICIO_FOGO);
        }
    }

    //TODO implementar construtor baseado no arquivo JSON
    Reticulado(File file){

        direcaoVento = null;
    }


    @Override
    public double[][] getMatrizVento() {
        return matrizVento.getMatrizVento();
    }

    @Override
    public int[][] getReticulado() throws NullPointerException{
        int[][] ret = new int[size][size];
        for(int i = 0; i < size; i++){
            for(int j = 0; j < size; j++){
                if(reticulado[i][j].getEstado() == null) throw new NullPointerException("Celula nula na posição [" + i + "][" + j + "]");
                ret[i][j] = reticulado[i][j].getEstado().VALOR;
            }
        }
        return ret;
    }

    public Celula getCelula(int i, int j) {
        return this.reticulado[i][j];
    }
    public void setModelo(Modelo modelo) {
        this.modelo = modelo;
    }

    public void avanzarIteracion() {
        //CALCULA O ESTADO FUTURO DO RETICULADO
        for (int i = 0; i < size; ++i) {
            for (int j = 0; j < size; ++j) {

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
        try {
            reticuladoAvancou();
        }
        catch (IOException e){
            System.out.println("Erro ao avisar os fofoqueiros");
        }
    }

    public void run() throws IllegalStateException{
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




    //Está avisando as celulas para trocar o estado
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
            subscriber.pegouFogo( i , j);
        }
    }
}
