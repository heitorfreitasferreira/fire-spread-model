import model.estados.Estados;
import model.modelos.Heitorzera2;
import model.reticulado.Reticulado;
import model.terreno.GeradorLateral;
import model.utils.JsonFileHandler;
import model.utils.Tuple;
import model.vento.DirecoesVento;

import java.time.Instant;
import java.util.ArrayList;

public class Main {

    final static int ALTURA = 256;
    final static int LARGURA = 256;

    public static void main(String[] args) {
        // If "-f" is set, read data from json file.
        if (args.length > 0 && args[0].equals("-f")) {
            if (args.length != 3) {
                System.out.println("Usage: java -jar <program_jar> -f <input_file> <output_file>");
                System.out.println("<program_jar>: Path to the program jar file.");
                System.out.println("<input_file> : Path to the input json file.");
                System.out.println("<output_file>: Path to the output json file.");
                return;
            }

            // Read from file.
            var inputPath = args[1];
            System.out.println("Reading from file: " + inputPath);

            // Write to file.
            // TODO: write results to output file.
            var outputPath = args[2];
            System.out.println("Writing to file: " + outputPath);

            try {
                var jsonHandler = new JsonFileHandler();
                var json = jsonHandler.readJson(inputPath);

                new Reticulado(json);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return;
        }

        Instant start = Instant.now();
        var focosIniciais = new ArrayList<Tuple<Integer, Integer>>();
        focosIniciais.add(new Tuple<>(ALTURA / 2, ALTURA / 2));
        var reticulados = new ArrayList<Reticulado>();
        for (double UMIDADE = 0.25; UMIDADE<=1.0; UMIDADE+=0.25) {
            reticulados.add(new Reticulado(focosIniciais, ALTURA, LARGURA, UMIDADE, DirecoesVento.N, Estados.SAVANICA, new GeradorLateral()));
            reticulados.add(new Reticulado(focosIniciais, ALTURA, LARGURA, UMIDADE, DirecoesVento.S, Estados.SAVANICA, new GeradorLateral()));
            reticulados.add(new Reticulado(focosIniciais, ALTURA, LARGURA, UMIDADE, DirecoesVento.E, Estados.SAVANICA, new GeradorLateral()));
            reticulados.add(new Reticulado(focosIniciais, ALTURA, LARGURA, UMIDADE, DirecoesVento.O, Estados.SAVANICA, new GeradorLateral()));
        }

        for (var reticulado : reticulados) {
            var modelo = new Heitorzera2(reticulado);
            reticulado.setModelo(modelo);
            reticulado.run();
        }
        Instant end = Instant.now();
        System.out.println("Tempo de execução:\n" +
                "\tTamanho: " + ALTURA + "x" + ALTURA + "\n" +
                "\t Execuções: " + Reticulado.QNT_EXECUCOES + "\n" +
                "\t Iterações: " + Reticulado.QNT_ITERACOES + "\n" +
                +(end.toEpochMilli() - start.toEpochMilli()) / 1000 + "seg");
    }
}
