import genetic.EvolutiveStrategy;
import lombok.extern.java.Log;
import model.estados.Estados;
import model.modelos.Heitorzera2;
import model.modelos.ModelParameters;
import model.modelos.Modelo;
import model.reticulado.Reticulado;
import model.terreno.GeradorLateral;
import model.utils.RandomDoubleSingleton;
import model.utils.Tuple;
import model.vento.DirecoesVento;

import java.util.List;
import java.util.Random;

@Log
public class Main {

  static final int ALTURA = 32;
  static final int LARGURA = 32;


  public static void main(String[] args) {

    RandomDoubleSingleton randomGenerator = RandomDoubleSingleton.getInstance(0);

    Reticulado reticulado = new Reticulado(
            List.of(new Tuple<>(ALTURA / 2, LARGURA / 2)),
            ALTURA,
            LARGURA,
            0.5, // Tanto faz pois o que importa é o ModelParameters
            DirecoesVento.N,
            Estados.SAVANICA,
            new GeradorLateral()
    );

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

    var goal = reticulado.run();

    EvolutiveStrategy evolutiveStrategy = new EvolutiveStrategy(goal);
    evolutiveStrategy.evolve(100);

  }


}
