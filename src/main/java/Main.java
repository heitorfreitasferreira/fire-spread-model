import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import lombok.extern.java.Log;
import model.estados.Estados;
import model.modelos.Heitorzera2;
import model.modelos.Modelo;
import model.reticulado.Reticulado;
import model.terreno.GeradorLateral;
import model.utils.Tuple;
import model.vento.DirecoesVento;

@Log
public class Main {

  static final int ALTURA = 64;
  static final int LARGURA = 64;

  public static void main(String[] args) {
    if (args.length > 0) {
      Yuri.yurizada(args);
    } else {
      Instant start = Instant.now();
      var focosIniciais = initializeFocosIniciais();
      //    var reticulados = createReticulados(focosIniciais);
      Reticulado reticulados =
          new Reticulado(
              focosIniciais,
              ALTURA,
              LARGURA,
              0.25,
              DirecoesVento.N,
              Estados.SAVANICA,
              new GeradorLateral(),
              true);

      Modelo modelo = new Heitorzera2(reticulados);
      reticulados.setModelo(modelo);
      reticulados.run();

      //    processReticulados(reticulados);
      
      logExecutionTimeAndConfigurations(start);
    }
  }

  private static List<Tuple<Integer, Integer>> initializeFocosIniciais() {
    return Collections.singletonList(new Tuple<>(ALTURA / 2, ALTURA / 2));
  }

  private static List<Reticulado> createReticulados(List<Tuple<Integer, Integer>> focosIniciais) {
    return IntStream.rangeClosed(1, 4)
        .boxed()
        .flatMap(
            i ->
                Stream.iterate(0.25, j -> j + 0.25)
                    .limit(4)
                    .map(
                        umidade ->
                            new Reticulado(
                                focosIniciais,
                                ALTURA,
                                LARGURA,
                                umidade,
                                DirecoesVento.values()[i - 1],
                                Estados.SAVANICA,
                                new GeradorLateral(),
                                true)))
        .collect(Collectors.toCollection(() -> new ArrayList<>(16))); // Pre-allocate size
  }

  private static void processReticulados(List<Reticulado> reticulados) {
    ExecutorService executor =
        Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    for (Reticulado reticulado : reticulados) {
      executor.submit(
          () -> {
            Modelo modelo = new Heitorzera2(reticulado);
            reticulado.setModelo(modelo);
            reticulado.run();
          });
    }
    executor.shutdown();
    try {
      executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
    } catch (InterruptedException e) {
      log.severe("Thread interrupted: " + e.getMessage());
    }
  }

  private static void logExecutionTimeAndConfigurations(Instant start) {
    String executionTime =
        "Tempo de execução: " + (Duration.between(start, Instant.now()).toSeconds()) + " seg";
    String configurations =
        "\tTamanho: "
            + ALTURA
            + "x"
            + ALTURA
            + "\n"
            + "\tExecuções: "
            + Reticulado.QNT_EXECUCOES
            + "\n"
            + "\tIterações: "
            + Reticulado.QNT_ITERACOES;

    log.info(executionTime + "\n" + configurations);
  }
}
