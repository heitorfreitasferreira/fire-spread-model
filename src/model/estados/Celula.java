package model.estados;

import model.analise.observers.SubReticuladoAvancou;
import model.reticulado.ReticuladoI;

public class Celula implements SubReticuladoAvancou {
    private Estados estado;
    private final Estados estadoInicial;
    protected final ReticuladoI reticulado;
    private int tempoNoEstado;
    private Estados estadoAux;

    public Celula(Estados estado, ReticuladoI reticulado){
        this.estado = estado;
        this.estadoAux = estado;
        this.estadoInicial = estado;
        this.reticulado = reticulado;
    }

    public void setEstado(Estados estado) {
        this.estado = estado;
    }

    public int getEstadoInicial(){
        return estadoInicial.VALOR;
    }
    public int getEstado(){
        return estado.VALOR;
    }
    public void reticuladoAvancou(ReticuladoI reticuladoAtual){
        tempoNoEstado++;
        estado = estadoAux;
    }

}

