import model.estados.Estados;
import model.modelos.Heitorzera2;
import model.reticulado.Reticulado;
import model.terreno.GeradorLateral;
import model.utils.Tuple;
import model.vento.DirecoesVento;

import java.util.ArrayList;

public class Main {

    final static int TAMANHO = 1024;


    public static void main(String[] args) {
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
    }
}
