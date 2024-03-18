package model.modelos;

import model.estados.Celula;

// Abstract Factory
public interface Modelo {

  void avanca(Celula[][] neighborhood, double[][] matrizVento);

}
