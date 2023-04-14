package model.modelos;

import model.estados.Celula;
import model.estados.Estados;
import model.reticulado.ReticuladoI;
import org.jetbrains.annotations.Contract;

import java.util.HashMap;

public class Heitorzera2 implements Modelo{
    private HashMap<Estados, Double> probEspalhamentoFogo;
    private HashMap<Estados, Double> influenciaVegetacao;
    private HashMap<Estados,Estados> transicao;
    private HashMap<Estados, Integer> tempoNoEstado;
    private ReticuladoI reticulado;
    private double influenciaUmidade;
    public Heitorzera2(ReticuladoI reticulado){
        this.reticulado = reticulado;

        this.probEspalhamentoFogo = new HashMap<Estados,Double>();
        probEspalhamentoFogo.put(Estados.INICIO_FOGO, 0.6);
        probEspalhamentoFogo.put(Estados.ARVORE_QUEIMANDO, 1.0);
        probEspalhamentoFogo.put(Estados.QUEIMA_LENTA, 0.2);
        this.influenciaVegetacao = new HashMap<Estados,Double>();
        influenciaVegetacao.put(Estados.CAMPESTRE, 0.6);
        influenciaVegetacao.put(Estados.SAVANICA, 1.0);
        influenciaVegetacao.put(Estados.FLORESTAL, 0.8);

        this.transicao = new HashMap<Estados,Estados>();
        transicao.put(Estados.SAVANICA, Estados.INICIO_FOGO);
        transicao.put(Estados.FLORESTAL, Estados.INICIO_FOGO);
        transicao.put(Estados.CAMPESTRE, Estados.INICIO_FOGO);
        transicao.put(Estados.INICIO_FOGO, Estados.ARVORE_QUEIMANDO);
        transicao.put(Estados.ARVORE_QUEIMANDO, Estados.QUEIMA_LENTA);
        transicao.put(Estados.AGUA, Estados.AGUA);
        transicao.put(Estados.QUEIMA_LENTA, Estados.SOLO_EXPOSTO);

        setTempoNoEstado();
        setInfluenciaUmidade();

    }
    private void setTempoNoEstado(){
        this.tempoNoEstado = new HashMap<Estados,Integer>();
        if(reticulado.getUmidade()<=0.25){
            tempoNoEstado.put(Estados.INICIO_FOGO, 3);
            tempoNoEstado.put(Estados.ARVORE_QUEIMANDO, 8);
            tempoNoEstado.put(Estados.QUEIMA_LENTA, 7);
        }else{
            tempoNoEstado.put(Estados.INICIO_FOGO, 3);
            tempoNoEstado.put(Estados.ARVORE_QUEIMANDO, 3);
            tempoNoEstado.put(Estados.QUEIMA_LENTA, 10);
        }
    }
    private void setInfluenciaUmidade() throws IllegalStateException{
        if(reticulado.getUmidade() > 0.0 && reticulado.getUmidade() <=0.25){
            this.influenciaUmidade = 1.5;
        }
        else if(reticulado.getUmidade() > 0.25 && reticulado.getUmidade() <=0.5){
            this.influenciaUmidade = 1.0;
        }
        else if(reticulado.getUmidade() > 0.5 && reticulado.getUmidade() <=0.75){
            this.influenciaUmidade = 0.8;
        }
        else if(reticulado.getUmidade() > 0.75 && reticulado.getUmidade() <=1.0){
            this.influenciaUmidade = 0.6;
        }else{
        throw new IllegalStateException("Umidade fora do intervalo [0,1]");
        }
    }
    private boolean[][] getMatrizQueima(Celula n, Celula s, Celula o, Celula l, Celula ne, Celula no, Celula se, Celula so, Celula central, Estados estado) {
        boolean matriz[][] = new boolean[][] {
                {no.getEstado() == estado, n.getEstado() == estado, ne.getEstado() == estado},
                {o.getEstado() == estado, false, l.getEstado() == estado},
                {so.getEstado() == estado, s.getEstado() == estado, se.getEstado() == estado}
        };

        return matriz;
    }
    @Contract(pure = true)
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
        if(!isFogo(celula)){
            throw new IllegalStateException("Celula no estado: "+celula.getEstado()+", nao pode ser queimada");
        }
        if(celula.getTempoNoEstado() >= tempoNoEstado.get(celula.getEstado())){
            celula.proxEstado(transicao.get(celula.getEstado()));
        }
//        if (celula.getEstado() == Estados.INICIO_FOGO && celula.getTempoNoEstado() == 2){
//            celula.proxEstado(this.transicao.get(Estados.INICIO_FOGO));
//        } else if (celula.getEstado() ==Estados.ARVORE_QUEIMANDO && celula.getTempoNoEstado() == 2) {
//            celula.proxEstado(this.transicao.get(Estados.ARVORE_QUEIMANDO));
//        } else if(celula.getEstado() ==Estados.QUEIMA_LENTA && celula.getTempoNoEstado() == 9){
//            celula.proxEstado(this.transicao.get(Estados.QUEIMA_LENTA));
//        }

    }
    public boolean isFogo(Celula celula){
        return celula.getEstado() == Estados.INICIO_FOGO || celula.getEstado() == Estados.ARVORE_QUEIMANDO || celula.getEstado() == Estados.QUEIMA_LENTA;
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
                        if (probMatriz[i][j] <
                                probEspalhamentoFogo.get(estado) *
                                influenciaVegetacao.get(celula.getEstado()) *
                                reticulado.getMatrizVento()[i][j] *
                                influenciaUmidade
                        ) {
                            celula.proxEstado(transicao.get(estado));
                        }
                }
            }
        }
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
        if(isFogo(central)) avancaFogo(central);
        // TODO implementar diferentes tipos de regeneração com strategy
        avancarQueimavel(central, estadoMatriz);


    }
}
