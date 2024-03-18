package model.terreno;

public class GeradorLateral implements GeradorTerreno {
    /**
     * Gera uma matriz representando a altitude de cada célula do reticulado, onde
     * quanto menor o J, menor a altitude, em j=0 a altitude é a menor possível
     * relativamente à maior altitude situada na posição j=tamanhoReticulado-1
     * 
     * @param altura
     * @param largura
     * @return matriz de altitudes
     */
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
