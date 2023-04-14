import model.estados.Estados;
import model.modelos.Heitorzera2;
import model.reticulado.Reticulado;
import model.utils.Tuple;
import model.vento.DirecoesVento;

import java.util.ArrayList;

public class Main {

    final static int TAMANHO = 32;
    final static double UMIDADE = 0.1;


    public static void main(String[] args) {
        var focosIniciais =   new ArrayList<Tuple<Integer, Integer>>();
        focosIniciais.add(new Tuple<>(TAMANHO / 2, TAMANHO / 2));
        var reticulado = new Reticulado(focosIniciais, TAMANHO, UMIDADE, DirecoesVento.S,  Estados.CAMPESTRE);
        var modelo = new Heitorzera2(reticulado);
        reticulado.setModelo(modelo);
        reticulado.run();
    }
}
