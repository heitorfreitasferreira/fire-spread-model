package model.reticulado;

import model.estados.Estados;
import model.terreno.GeradorTerreno;
import model.utils.Tuple;
import model.vento.DirecoesVento;

import java.util.List;

public record ReticuladoParameters(
        List<Tuple<Integer, Integer>> ponto,
        int altura,
        int largura,
        double umidade,
        DirecoesVento direcaoVento,
        Estados estadoInicial,
        GeradorTerreno geradorTerreno) {
}
