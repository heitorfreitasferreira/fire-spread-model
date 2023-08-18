package model.terreno;

public class GeradorLateral implements GeradorTerreno{
    /**
     * Gera uma matriz representando a altitude de cada célula do reticulado, onde quanto menor o J, menor a altitude, em j=0 a altitude é a menor possível relativamente à maior altitude situada na posição j=tamanhoReticulado-1
     * @param tamanhoReticulado
     * @return matriz de altitudes
     */
    public double[][] gerarTerreno(int tamanhoReticulado) {
        double[][] terreno = new double[tamanhoReticulado][tamanhoReticulado];
        for(int i = 0; i < tamanhoReticulado; i++){
            for(int j = 0; j < tamanhoReticulado; j++){
                terreno[i][j] = j/(double)tamanhoReticulado;
            }
        }
        return terreno;
    }
}
