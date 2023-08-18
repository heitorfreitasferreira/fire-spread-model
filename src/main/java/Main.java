import model.estados.Estados;
import model.modelos.Heitorzera2;
import model.reticulado.Reticulado;
import model.terreno.GeradorLateral;
import model.utils.Tuple;
import model.vento.DirecoesVento;

import java.time.Instant;
import java.util.ArrayList;

public class Main {

    final static int TAMANHO = 128;


    public static void main(String[] args) {
        Instant start = Instant.now();
        var focosIniciais =   new ArrayList<Tuple<Integer, Integer>>();
        focosIniciais.add(new Tuple<>(TAMANHO / 2, TAMANHO / 2));
        var reticulados = new ArrayList<Reticulado>();
        for (double UMIDADE = 0.25; UMIDADE<=1.0; UMIDADE+=0.25) {
            reticulados.add(new Reticulado(focosIniciais, TAMANHO, UMIDADE, DirecoesVento.N, Estados.CAMPESTRE, new GeradorLateral()));
            reticulados.add(new Reticulado(focosIniciais, TAMANHO, UMIDADE, DirecoesVento.S, Estados.CAMPESTRE, new GeradorLateral()));
            reticulados.add(new Reticulado(focosIniciais, TAMANHO, UMIDADE, DirecoesVento.E, Estados.CAMPESTRE, new GeradorLateral()));
            reticulados.add(new Reticulado(focosIniciais, TAMANHO, UMIDADE, DirecoesVento.O, Estados.CAMPESTRE, new GeradorLateral()));
        }


        for (var reticulado: reticulados){
            var modelo = new Heitorzera2(reticulado);
            reticulado.setModelo(modelo);
            reticulado.run();
        }
        Instant end = Instant.now();
        System.out.println("Tempo de execução:\n" +
                "\tTamanho: " + TAMANHO + "x" + TAMANHO + "\n" +
                "\t Execuções: " + Reticulado.QNT_EXECUCOES + "\n" +
                "\t Iterações: " + Reticulado.QNT_ITERACOES + "\n" +
                + (end.toEpochMilli() - start.toEpochMilli())/1000 + "seg");
    }
}
