package model.modelos;

import model.estados.Celula;
import model.estados.Estados;

import java.util.HashMap;

public class Heitorzera2 implements Modelo{
    private HashMap<Estados, Double> probEspalhamentoFogo;
    private HashMap<Estados, Double> influenciaVegetacao;
    private HashMap<Estados,Estados> transicao;
    private HashMap<Estados, Integer> tempoTransicaoFogo;
    public Heitorzera2(){
        setProbEspalhamentoFogo();
        setInfluenciaVegetacao();
        setTransicoes();
        setTempoTransicaoFogo();
    }
    private void setProbEspalhamentoFogo(){
        this.probEspalhamentoFogo = new HashMap<Estados,Double>();
        probEspalhamentoFogo.put(Estados.INICIO_FOGO, 0.6);
        probEspalhamentoFogo.put(Estados.ARVORE_QUEIMANDO, 1.0);
        probEspalhamentoFogo.put(Estados.QUEIMA_LENTA, 0.6);
    }
    private void setInfluenciaVegetacao(){
        this.influenciaVegetacao = new HashMap<Estados,Double>();
        influenciaVegetacao.put(Estados.CAMPESTRE, 0.6);
        influenciaVegetacao.put(Estados.SAVANICA, 1.0);
        influenciaVegetacao.put(Estados.FLORESTAL, 0.8);
    }
    private void setTempoTransicaoFogo(){
        this.tempoTransicaoFogo = new HashMap<Estados, Integer>();
        tempoTransicaoFogo.put(Estados.INICIO_FOGO, 2);
        tempoTransicaoFogo.put(Estados.ARVORE_QUEIMANDO, 4);
        tempoTransicaoFogo.put(Estados.QUEIMA_LENTA, 10);
    }
    private void setTransicoes(){
        this.transicao = new HashMap<Estados,Estados>();
        transicao.put(Estados.SAVANICA, Estados.INICIO_FOGO);
        transicao.put(Estados.FLORESTAL, Estados.INICIO_FOGO);
        transicao.put(Estados.CAMPESTRE, Estados.INICIO_FOGO);

        transicao.put(Estados.INICIO_FOGO, Estados.ARVORE_QUEIMANDO);
        transicao.put(Estados.ARVORE_QUEIMANDO, Estados.QUEIMA_LENTA);
        transicao.put(Estados.QUEIMA_LENTA, Estados.SOLO_EXPOSTO);
//        transicao.put(Estados.SOLO_EXPOSTO, Estados.CAMPESTRE);
    }
    private boolean[][] getMatrizQueima(Celula n, Celula s, Celula o, Celula l, Celula ne, Celula no, Celula se, Celula so, Celula central, Estados estado) {
        boolean matriz[][] = new boolean[][] {
                {no.getEstado() == estado, n.getEstado() == estado, ne.getEstado() == estado},
                {o.getEstado() == estado, false, l.getEstado() == estado},
                {so.getEstado() == estado, s.getEstado() == estado, se.getEstado() == estado}
        };

        return matriz;
    }
    private boolean[][]  getMatrizQueima(Estados[][] estados, Estados estado){
        boolean matriz[][] = new boolean[3][3];
        for (int i = 0; i<estados.length; i++){
            for (int j = 0; j<estados[i].length; j++){
                if(i == 1 && j == 1){
                    matriz[i][j] = false;
                }else{
                    matriz[i][j] = estados[i][j] == estado;
                }
            }
        }
        return matriz;
    }
//    @Contract(pure = true)
    private int sumMatriz(boolean[][] matriz) {
        int sum = 0;
        for (int i = 0; i < matriz.length; i++) {
            for (int j = 0; j < matriz[i].length; j++) {
                if (matriz[i][j]) {
                    sum++;
                }
            }
        }
        return sum;
    }
    private void avancaFogo(Celula celula){
        var estado = celula.getEstado();
        if(!this.probEspalhamentoFogo.keySet().contains(estado)) return;
        var tempoNoEstado = celula.getTempoNoEstado();
        if(tempoNoEstado == tempoTransicaoFogo.get(estado))
            celula.proxEstado(transicao.get(estado));
    }
    private boolean isQueimavel(Celula celula, HashMap<Estados,boolean[][]> estadoMatriz){
        var temQueimaSoma = sumMatriz(estadoMatriz.get(Estados.INICIO_FOGO));
        var temInicioFogoSoma = sumMatriz(estadoMatriz.get(Estados.ARVORE_QUEIMANDO));
        var temBrasaSoma = sumMatriz(estadoMatriz.get(Estados.QUEIMA_LENTA));
        var temFogo = temQueimaSoma + temInicioFogoSoma + temBrasaSoma > 0;
        return temFogo && (celula.getEstado() == Estados.CAMPESTRE || celula.getEstado() == Estados.SAVANICA || celula.getEstado() == Estados.FLORESTAL);
    }
    private double[][] getRandomMatriz(){
        double[][] matriz = new double[3][3];
        for (int i = 0; i < matriz.length; i++) {
            for (int j = 0; j < matriz[i].length; j++) {
                if(i==1 && j==1)
                    matriz[i][j] = 0;
                else
                    matriz[i][j] = Math.random();
            }
        }
        return matriz;
    }
    private void avancarQueimavel(Celula celula, HashMap<Estados,boolean[][]> estadoMatriz){
        if(!isQueimavel(celula, estadoMatriz)) return;
        double[][] probMatriz= getRandomMatriz();

        for(Estados estado: estadoMatriz.keySet()) {
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    if (estadoMatriz.get(estado)[i][j]) // Vendo se há fogo daquele tipo naquela posição
                        if (probMatriz[i][j] < probEspalhamentoFogo.get(estado) * influenciaVegetacao.get(celula.getEstado()))// TODO Falta influencia do vento
                            celula.proxEstado(transicao.get(estado));
                }
            }
        }
    }
    private Estados[][] getVizinhanca(Celula n, Celula s, Celula o, Celula l, Celula ne, Celula no, Celula se, Celula so, Celula central){
        Estados matriz[][] = new Estados[][] {
                {no.getEstado(), n.getEstado(), ne.getEstado()},
                {o.getEstado(), central.getEstado(), l.getEstado()},
                {so.getEstado(), s.getEstado(), se.getEstado()}
        };

        return matriz;
    }
    private HashMap<Estados,boolean[][]> generateHasQueimaMatriz(Celula n, Celula s, Celula o, Celula l, Celula ne, Celula no, Celula se, Celula so, Celula central){
        var estadoMatriz = new HashMap<Estados,boolean[][]>();
        estadoMatriz.put(Estados.INICIO_FOGO, getMatrizQueima(n, s, o, l, ne, no, se, so, central, Estados.INICIO_FOGO));
        estadoMatriz.put(Estados.ARVORE_QUEIMANDO, getMatrizQueima(n, s, o, l, ne, no, se, so, central, Estados.ARVORE_QUEIMANDO));
        estadoMatriz.put(Estados.QUEIMA_LENTA, getMatrizQueima(n, s, o, l, ne, no, se, so, central, Estados.QUEIMA_LENTA));
        return estadoMatriz;
    }
    public void avanca(Celula n, Celula s, Celula o, Celula l, Celula ne, Celula no, Celula se, Celula so, Celula central) {
        var estadoMatriz = generateHasQueimaMatriz(n, s, o, l, ne, no, se, so, central);

        avancaFogo(central);// Transição temporal
        // TODO implementar diferentes tipos de regeneração com strategy
        avancarQueimavel(central, estadoMatriz);//, getVizinhanca(n,s,o,l,ne,no,se,so,central));


    }
}
