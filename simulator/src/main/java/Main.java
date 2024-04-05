import java.io.File;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

import com.beust.jcommander.JCommander;

import genetic.EvolutiveStrategy;
import genetic.GeneticAlgorithmParams;
import genetic.reproductors.Reproductor;
import genetic.reproductors.ReprodutorAssexuado;
import genetic.reproductors.ReprodutorSexuado;
import lombok.extern.java.Log;
import model.estados.Estados;
import model.modelos.Heitorzera2;
import model.modelos.ModelParameters;
import model.reticulado.Reticulado;
import model.reticulado.ReticuladoFactory;
import model.reticulado.ReticuladoParameters;
import model.terreno.GeradorLateral;
import model.utils.JsonFileHandler;
import model.utils.MainArgs;
import model.utils.Tuple;
import model.vento.DirecoesVento;

@Log
public class Main {

  static final int ALTURA = 64;
  static final int LARGURA = 64;

  public static void main(String... argv) {
    MainArgs args = new MainArgs();
    JCommander.newBuilder().addObject(args).build().parse(argv);

    switch (args.mode) {
      case "single-simulation" -> singleSimulation(args);
      case "genetic-algorithm" -> algoritmoGenetico(args);
      default -> throw new IllegalArgumentException("Invalid mode");
    }

  }

  private static void singleSimulation(MainArgs args) {
    Reticulado reticulado = new Reticulado(
        ReticuladoFactory.fromJson(args.inputFile, args.maxIterations))
        .setModelo(new Heitorzera2(new ModelParameters(
            1.0,
            0.6,
            1.0,
            0.2,
            0.6,
            1.0,
            0.8)));

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
    log.info("Simulation finished in " + (end - start) + " milliseconds.");
  }

  private static void algoritmoGenetico(MainArgs args) {

    ReticuladoParameters reticuladoParams = new ReticuladoParameters(
        List.of(new Tuple<>(ALTURA / 2, LARGURA / 2)),
        ALTURA,
        LARGURA,
        0.5, // Tanto faz pois o que importa é o ModelParameters
        DirecoesVento.N,
        ReticuladoFactory.getMatrizEstadosDeEstadoInicial(Estados.SAVANICA, ALTURA, LARGURA),
        new GeradorLateral(),
        args.maxIterations);

    Reticulado reticulado = Reticulado.getInstance(reticuladoParams, new Heitorzera2(
        new ModelParameters(
            1.0,
            0.6,
            1.0,
            0.2,
            0.6,
            1.0,
            0.8)));

    GeneticAlgorithmParams geneticAlgorithmParams = new GeneticAlgorithmParams(
        args.numberOfGenerations,
        args.populationSize,
        args.mutationRate,
        args.mutationProb,
        args.crossoverRate,
        args.elitismRate,
        args.tournamentSize,
        args.crossoverBlxAlpha,
        args.numberOfSimulationsPerFitness);
    var goal = reticulado.run();
    // PrintReticulado.printLastIterationOfSimulation(goal);

    new EvolutiveStrategy(
        goal,
        geneticAlgorithmParams,
        reticuladoParams,
        getReproductor(geneticAlgorithmParams, args.typeOfReproduction))
        .evolve();
  }

  private static Reproductor getReproductor(GeneticAlgorithmParams params, String typeOfReproduction) {
    return Map.of(
        "assexuado", new ReprodutorAssexuado(params),
        "sexuado", new ReprodutorSexuado(params))
        .get(typeOfReproduction);
  }

}
