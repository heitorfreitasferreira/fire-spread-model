package model.analise;

import lombok.extern.java.Log;
import model.analise.observers.SubReticuladoAvancou;
import model.reticulado.Reticulado;

@Log
public class Bruto extends Analisador implements SubReticuladoAvancou {

  ImpressoraReticulado impressora;

  public Bruto(Reticulado reticulado, String fileName) {
    super(reticulado, fileName);
    this.impressora = new ImpressoraBufferizada(Reticulado.QNT_ITERACOES, reticulado);
  }

  @Override
  public void reticuladoAvancou() {
    impressora.printaEstados(getFile(), reticulado);
  }

  public void reticuladoTerminou(Reticulado reticuladoAtual) {
    log.info("Reticulado " + fileName + " terminou.");
  }
}
