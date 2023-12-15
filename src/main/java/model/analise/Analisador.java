package model.analise;

import model.reticulado.Reticulado;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public abstract class Analisador {
    protected String fileName;
    protected final String pathToFolder;
    protected final Reticulado reticulado;

    Analisador(Reticulado reticulado, String pathToFolder) {
        if (reticulado == null) throw new NullPointerException("Reticulado não pode ser nulo");
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
                e.printStackTrace();
            }
        }
        return file;
    }

    protected String getFolderName(){
        var formatter = new SimpleDateFormat("[dd-MM-yyyy_HH-mm]");
        return pathToFolder + "/" +
                reticulado.getTipoInicial() +
                "/" +
                reticulado.getUmidade() +
                "/" +
                reticulado.getDirecaoVento() +
                "/" +
                formatter.format(new Date()) +
                "/simulation_" +
                reticulado.getExecucaoAtual();
    }

    protected File getFolder(){
        var folder = new File(getFolderName());
        if (!folder.exists()) folder.mkdirs();
        return folder;
    }

    protected String getFileName() {
        return reticulado.getIteracao() + ".json";
    }
}