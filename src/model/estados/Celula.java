package model.estados;

import model.analise.observers.SubReticuladoAvancou;
import model.reticulado.ReticuladoI;
import model.utils.Tuple;
import org.jetbrains.annotations.NotNull;

public class Celula implements SubReticuladoAvancou {
    private Estados estado;
    private final Estados estadoInicial;
    protected final ReticuladoI reticulado;
    private int tempoNoEstado;
    private Estados estadoAux;
    private boolean temQueTrocar;
    private double altura;
    public final Tuple<Integer, Integer> posicao;

    public Celula(Estados estado, ReticuladoI reticulado, double altura, Tuple<Integer, Integer> posicao){
        this.posicao = posicao;
        this.estado = estado;
        this.estadoAux = estado;
        this.estadoInicial = estado;
        this.reticulado = reticulado;
        this.tempoNoEstado = 0;
        this.temQueTrocar = false;
        this.altura = altura;
    }

    public void setEstado(@NotNull Estados estado) {
        if(estado == Estados.AGUA)
            throw new IllegalArgumentException("Não é possível alterar o estado de uma célula de água");
        if(estado == this.estado)
            throw new IllegalArgumentException("Não é possível alterar o estado de uma célula para o mesmo estado atual");

        tempoNoEstado = 0;
        temQueTrocar = false;
        this.estado = estado;
    }
    public void proxEstado(@NotNull Estados proximoEstado) {
        this.estadoAux = proximoEstado;
        temQueTrocar = true;
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
    public void reticuladoAvancou(){
        tempoNoEstado++;
        if(temQueTrocar) {
            setEstado(estadoAux);
            if(estadoAux == Estados.INICIO_FOGO){
                reticulado.reticuladoPegouFogo(posicao.i, posicao.j);
            }
        }

    }

}

