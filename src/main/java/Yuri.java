import lombok.extern.java.Log;
import model.modelos.Heitorzera2;
import model.reticulado.Reticulado;
import model.utils.JsonFileHandler;

@Log
public class Yuri {
    public static void yurizada(String[] args) {
        if (isFileMode(args)) {
            String inputPath = args[1];
            String outputPath = args[2];
            processFile(inputPath, outputPath);
        }
    }
    private static boolean isFileMode(String[] args) {
        if (args.length > 0 && args[0].equals("-f")) {
            if (args.length != 3) {
                log.info("Usage: java -jar <program_jar> -f <input_file> <output_file>");
                log.info("<program_jar>: Path to the program jar file.");
                log.info("<input_file> : Path to the input json file.");
                log.info("<output_file>: Path to the output json file.");
                return false;
            }
            return true;
        }
        return false;
    }

    private static void processFile(String inputPath, String outputPath) {
        log.info("Reading from file: " + inputPath);
        log.info("Writing to file: " + outputPath);

        try {
            var jsonHandler = new JsonFileHandler();
            var json = jsonHandler.readJson(inputPath);

            Reticulado r = Reticulado.fromJson(json, outputPath);
            r.setModelo(new Heitorzera2(r));
            r.run();

        } catch (Exception e) {
            log.severe("An error occurred while processing the file: " + e.getMessage());
        }
    }
}
