package model.analise;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import lombok.extern.java.Log;
import model.reticulado.Reticulado;

@Log
public abstract class Analisador {
    protected String fileName;
    protected final String pathToFolder;
    protected final Reticulado reticulado;

    Analisador(Reticulado reticulado, String pathToFolder) {
        if (reticulado == null)
            throw new NullPointerException("Reticulado não pode ser nulo");
        this.pathToFolder = pathToFolder;
        this.reticulado = reticulado;
    }

    public File getFile() {
        File file = new File(getFolder(), getFileName());

        // Try to create the file if it doesn't exist
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (Exception e) {
                log.severe("Não foi possível criar o arquivo " + file.getName() + " no diretório " + file.getParent());
                log.severe(e.getMessage());
            }
        }
        return file;
    }

    protected String getFolderName() {
        var formatter = new SimpleDateFormat("[dd-MM-yyyy_HH-mm]");
        return "simulacoes/" +
                reticulado.getTipoInicial() +
                "/" +
                reticulado.getUmidade() +
                "/" +
                reticulado.getDirecaoVento() +
                "/" +
                formatter.format(new Date());
        // + "/simulation_" +
        // reticulado.getExecucaoAtual();
    }

    protected File getFolder() {
        var folder = new File(getFolderName());
        if (!folder.exists())
            folder.mkdirs();
        return folder;
    }

    protected String getFileName() {
        return "simulacao_" + ".json";
    }
}