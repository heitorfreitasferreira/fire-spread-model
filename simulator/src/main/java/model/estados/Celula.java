package model.estados;

import lombok.Getter;
import lombok.NonNull;
import model.analise.observers.SubReticuladoAvancou;
import model.reticulado.Reticulado;
import model.utils.Tuple;

@Getter
public class Celula implements SubReticuladoAvancou {

  public final Tuple<Integer, Integer> posicao;
  protected final Reticulado reticulado;
  private final Estados estadoInicial;
  private Estados estado;
  private int tempoNoEstado;
  private Estados estadoAux;
  private boolean temQueTrocar;
  private double altura;

  public Celula(
      Estados estado, Reticulado reticulado, double altura, Tuple<Integer, Integer> posicao) {
    this.posicao = posicao;
    this.estado = estado;
    this.estadoAux = null;
    this.estadoInicial = estado;
    this.reticulado = reticulado;
    this.tempoNoEstado = 0;
    this.temQueTrocar = false;
    this.altura = altura;
  }

  public void setEstado(@NonNull Estados estado) {
    if (estado == Estados.AGUA) {
      throw new IllegalArgumentException("Não é possível alterar o estado de uma célula de água");
    }
    tempoNoEstado = 0;
    temQueTrocar = false;
    this.estado = estado;
    this.estadoAux = null;
  }

  public void proxEstado(@NonNull Estados proximoEstado) {
    this.estadoAux = proximoEstado;
    temQueTrocar = true;
  }

  public void reticuladoAvancou() {
    tempoNoEstado++;
    if (temQueTrocar) {
      setEstado(estadoAux);
    }
  }
}
