package model.terreno;

public class GeradorComCume implements GeradorTerreno {
    public double[][] gerarTerreno(int altura, int largura) {
        double[][] terreno = new double[altura][largura];
        for (int i = 0; i < altura; i++) {
            for (int j = 0; j < largura; j++) {
                terreno[i][j] = j / (double) largura;
            }
        }
        return terreno;
    }
}
