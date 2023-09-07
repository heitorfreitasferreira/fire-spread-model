package model.reticulado;

import model.estados.Celula;

import java.io.IOException;

public interface ReticuladoI extends Runnable{
    int getAltura();
    int getLargura();

    double[][] getMatrizVento();
    int [][] getReticulado();
    int getIteracao();
    int getExecucaoAtual();
    double getUmidade();

    Celula getCelula(int i, int j);
    String getTipoInicial();
    void avanzarIteracion();
    String getDirecaoVento();
    public void reticuladoAvancou() throws IOException;
    public void reticuladoTerminou() ;
    public void reticuladoPegouFogo(int i, int j) ;

}
