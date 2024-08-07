package model.reticulado;

import java.util.List;

import model.terreno.GeradorTerreno;
import model.vento.DirecoesVento;
import utils.Tuple;

public record ReticuladoParameters(
                List<Tuple<Integer, Integer>> ponto,
                int altura,
                int largura,
                double umidade,
                DirecoesVento direcaoVento,
                int[][] estados,
                GeradorTerreno geradorTerreno,
                int QNT_ITERACOES) {
}
