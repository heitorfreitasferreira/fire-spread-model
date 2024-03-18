package model.terreno;

public class GeradorDeMatriz implements GeradorTerreno {
    private double[][] matriz;

    public GeradorDeMatriz(double[][] matriz) {
        this.matriz = matriz;
    }

    public double[][] gerarTerreno(int altura, int largura) {
        return this.matriz;
    }
}
