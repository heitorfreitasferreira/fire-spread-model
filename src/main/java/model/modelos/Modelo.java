package model.modelos;

import model.estados.Celula;
import model.reticulado.Reticulado;

// Abstract Factory
public interface Modelo {

  void avanca(Celula[][] neighborhood);

  int getRadius();

  void mergeSubsolo(Reticulado reticuladoSubsolo);
}
