package model.reticulado;

import model.estados.Celula;

public interface ReticuladoI extends Runnable{
    int getSize();

    double[][] getMatrizVento();
    int [][] getReticulado();
    int getIteracao();
    int getExecucaoAtual();
    double getUmidade();

    Celula getCelula(int i, int j);
    String getTipoInicial();
    void avanzarIteracion();
    String getDirecaoVento();
    public void reticuladoAvancou();
    public void reticuladoTerminou() ;
    public void reticuladoPegouFogo(int i, int j) ;

}
