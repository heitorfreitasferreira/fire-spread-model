import genetic.EvolutiveStrategy;
import lombok.extern.java.Log;
import model.estados.Estados;
import model.modelos.Heitorzera2;
import model.modelos.ModelParameters;
import model.modelos.Modelo;
import model.reticulado.Reticulado;
import model.reticulado.ReticuladoParameters;
import model.terreno.GeradorLateral;
import model.utils.RandomDoubleSingleton;
import model.utils.Tuple;
import model.vento.DirecoesVento;

import java.util.List;
import java.util.Random;

@Log
public class Main {

  static final int ALTURA = 64;
  static final int LARGURA = 64;


  public static void main(String[] args) {

    RandomDoubleSingleton randomGenerator = RandomDoubleSingleton.getInstance(0);

    Reticulado reticulado = getReticulado();

    long start = System.currentTimeMillis();
    var goal = reticulado.run();
    long end = System.currentTimeMillis();
    for(int k = 0; k < Reticulado.QNT_ITERACOES; k++) {
      for (int i = 0; i < ALTURA; i++) {
        for (int j = 0; j < LARGURA; j++) {
          System.out.print(goal[k][i][j] + " ");
        }
        System.out.println();
      }
      System.out.println();
    }

    System.out.println("Simulation finished in " + (end - start)  + " milliseconds.");

//    EvolutiveStrategy evolutiveStrategy = new EvolutiveStrategy(goal);
//    evolutiveStrategy.evolve(30);

  }

  private static Reticulado getReticulado() {
    Reticulado reticulado = new Reticulado(new ReticuladoParameters(
            List.of(new Tuple<>(ALTURA / 2, LARGURA / 2)),
            ALTURA,
            LARGURA,
            0.5, // Tanto faz pois o que importa é o ModelParameters
            DirecoesVento.N,
            Estados.SAVANICA,
            new GeradorLateral()
    ));

    ModelParameters modelParameters = new ModelParameters(
            1.0,
            0.6,
            1.0,
            0.2,
            0.6,
            1.0,
            0.8
    );

    reticulado.setModelo(new Heitorzera2(modelParameters));
    return reticulado;
  }
}
