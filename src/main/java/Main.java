import com.beust.jcommander.JCommander;
import genetic.EvolutiveStrategy;
import lombok.extern.java.Log;
import model.estados.Estados;
import model.modelos.Heitorzera2;
import model.modelos.ModelParameters;
import model.modelos.Modelo;
import model.reticulado.Reticulado;
import model.reticulado.ReticuladoFactory;
import model.reticulado.ReticuladoParameters;
import model.terreno.GeradorLateral;
import model.utils.JsonFileHandler;
import model.utils.MainArgs;
import model.utils.RandomDoubleSingleton;
import model.utils.Tuple;
import model.vento.DirecoesVento;

import java.io.File;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;

@Log
public class Main {

  static final int ALTURA = 64;
  static final int LARGURA = 64;


  public static void main(String ... argv) {
    MainArgs args = new MainArgs();
    JCommander.newBuilder().addObject(args).build().parse(argv);

    switch (args.mode) {
      case "single-simulation" -> singleSimulation(args);
      case "genetic-algorithm" -> algoritmoGenetico(args);
      default -> throw new IllegalArgumentException("Invalid mode");
    }

  }

  private static void singleSimulation(MainArgs args) {

    ReticuladoParameters reticuladoParams = ReticuladoFactory.fromJson(args.inputFile);

    Reticulado reticulado = new Reticulado(reticuladoParams);

    ModelParameters modelParameters = new ModelParameters(
            1.0,
            0.6,
            1.0,
            0.2,
            0.6,
            1.0,
            0.8
    );

    Modelo modelo = new Heitorzera2(modelParameters);

    reticulado.setModelo(modelo);

    long start = System.currentTimeMillis();
    int[][][] simulation = reticulado.run();

    String outputToFile = JsonFileHandler.convertToCustomJson(reticulado.toString(), simulation);

    File outputFile = new File(args.outputFilePath);
    try (PrintWriter writer = new PrintWriter(outputFile)) {
      writer.write(outputToFile);
    } catch (Exception e) {
      log.severe(e.getMessage());
    }
    long end = System.currentTimeMillis();
    log.info("Simulation finished in " + (end - start)  + " milliseconds.");
  }

  private static void algoritmoGenetico(MainArgs args) {

    RandomDoubleSingleton randomGenerator = RandomDoubleSingleton.getInstance(0);
    Reticulado reticulado = new Reticulado(new ReticuladoParameters(
            List.of(new Tuple<>(ALTURA / 2, LARGURA / 2)),
            ALTURA,
            LARGURA,
            0.5, // Tanto faz pois o que importa é o ModelParameters
            DirecoesVento.N,
            ReticuladoFactory.getMatrizEstadosDeEstadoInicial(Estados.SAVANICA, ALTURA, LARGURA),
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

    long start = System.currentTimeMillis();
    var goal = reticulado.run();
    EvolutiveStrategy evolutiveStrategy = new EvolutiveStrategy(goal, args.populationSize);
    evolutiveStrategy.evolve(args.numberOfGenerations);
    long end = System.currentTimeMillis();
    log.info("Simulation finished in " + (end - start)  + " milliseconds.");
  }
}
