package model.reticulado;

import com.google.gson.JsonObject;
import model.estados.Estados;
import model.terreno.GeradorDeMatriz;
import model.terreno.GeradorLateral;
import model.utils.Tuple;
import model.vento.DirecoesVento;

import java.util.ArrayList;

public class ReticuladoFactory {
    public static Reticulado createReticuladoComEstadoIncial(ReticuladoParameters params) {
        if (params.altura() < 16) {
            throw new IllegalArgumentException("Tamanho do reticulado deve ser maior que 16");
        }
        if (params.umidade() < 0 || params.umidade() > 1) {
            throw new IllegalArgumentException("Umidade deve ser entre 0 e 1");
        }
        return new Reticulado(params);
    }

    public static int[][] getMatrizEstadosDeEstadoInicial(Estados estadoInicial, int altura, int largura){
        int[][] ret = new int[altura][largura];
        for (int i = 0; i < altura; i++) {
            for (int j = 0; j < largura; j++) {
                ret[i][j] = estadoInicial.VALOR;
            }
        }
        return ret;
    }

    public static ReticuladoParameters fromJson(JsonObject json, int quantidadeIteracoes) {

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

//            var slope = json.get("slope").getAsJsonArray();
            var slopeArray = new double[terrain.size()][terrain.get(0).getAsJsonArray().size()];
            for (int i = 0; i < terrain.size(); i++) {
                var row = terrain.get(i).getAsJsonArray();
                for (int j = 0; j < row.size(); j++) {
                    slopeArray[i][j] = 0.0;// TODO: adicionar no json
                }
            }

            return new ReticuladoParameters(initialPoints, terrainArray.length, terrainArray[0].length, humidity, windDirection, terrainArray, new GeradorDeMatriz(slopeArray), quantidadeIteracoes);
        }

}
