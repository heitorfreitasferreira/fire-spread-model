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
        this.tempoNoEstado = 0;
    }

    public void setEstado(Estados estado) {
        tempoNoEstado = 0;
        this.estado = estado;
    }
    public void proxEstado(Estados proximoEstado) {
        this.estadoAux = proximoEstado;
    }
    public Estados getProxEstado(){
        return estadoAux;
    }
    public int getTempoNoEstado() {
        return tempoNoEstado;
    }
    public int getEstadoInicial(){
        return estadoInicial.VALOR;
    }
    public Estados getEstado(){
        return estado;
    }
    public void reticuladoAvancou(ReticuladoI reticuladoAtual){
        tempoNoEstado++;
        if(estado != estadoAux)
            setEstado(estadoAux);
    }

}

