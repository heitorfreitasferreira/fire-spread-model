import java.io.File;
import java.io.PrintWriter;
import java.util.List;

import com.beust.jcommander.JCommander;

import genetic.EvolutiveStrategy;
import genetic.GeneticAlgorithmParams;
import genetic.reproductors.Reproductor;
import genetic.reproductors.ReprodutorAleatorio;
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
import model.utils.SimulationArgs;
import model.utils.Tuple;
import model.vento.DirecoesVento;

@Log
public class Main {

  static final int ALTURA = 64;
  static final int LARGURA = 64;

  public static void main(String... argv) {
    SimulationArgs args = new SimulationArgs();
    JCommander.newBuilder().addObject(args).build().parse(argv);

    chooseMode(args);
  }

  private static void chooseMode(SimulationArgs args) {
    switch (args.mode()) {
      case SimulationArgs.Type.SINGLE_SIMULATION:
        singleSimulation(args);
        break;
      case SimulationArgs.Type.GENETIC_ALGORITHM:
        algoritmoGenetico(args);
        break;
      default:
        throw new IllegalArgumentException(
            "Invalid mode of execution, please choose between" + SimulationArgs.Type.values() + ".");
    }
  }

  private static void singleSimulation(SimulationArgs args) {
    Reticulado reticulado = new Reticulado(
        ReticuladoFactory.fromJson(args.inputFile(), args.maxIterations()))
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

    File outputFile = new File(args.outputFilePath());
    try (PrintWriter writer = new PrintWriter(outputFile)) {
      writer.write(outputToFile);
    } catch (Exception e) {
      log.severe(e.getMessage());
    }
    long end = System.currentTimeMillis();
    log.info("Simulation finished in " + (end - start) + " milliseconds.");
  }

  private static void algoritmoGenetico(SimulationArgs args) {

    ReticuladoParameters reticuladoParams = new ReticuladoParameters(
        List.of(new Tuple<>(ALTURA / 2, LARGURA / 2)),
        ALTURA,
        LARGURA,
        0.5, // Tanto faz pois o que importa é o ModelParameters
        DirecoesVento.N,
        ReticuladoFactory.getMatrizEstadosDeEstadoInicial(Estados.SAVANICA, ALTURA, LARGURA),
        new GeradorLateral(),
        args.maxIterations());

    var goal = Reticulado.getInstance(reticuladoParams, new Heitorzera2(
        new ModelParameters(
            1.0,
            0.6,
            1.0,
            0.2,
            0.6,
            1.0,
            0.8)))
        .run();
    for (int i = 0; i < 6; i++) {
      args.setReverseElitismN(i * 10);
      new EvolutiveStrategy(
          goal,
          args,
          reticuladoParams,
          getReproductor(args, args.typeOfReproduction()))
          .evolve();
    }
  }

  private static Reproductor getReproductor(GeneticAlgorithmParams params, Reproductor.Type typeOfReproduction) {
    return switch (typeOfReproduction) {
      case ALEATORIO -> new ReprodutorAleatorio();
      case ASSEXUADO -> new ReprodutorAssexuado(params);
      case SEXUADO -> new ReprodutorSexuado(params);
      default -> throw new IllegalArgumentException("Invalid type of reproduction");
    };
  }
}
