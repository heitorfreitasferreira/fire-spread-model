package model.analise;

import model.reticulado.ReticuladoI;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public abstract class Analisador {
    protected String fileName;
    protected final String pathToFolder;
    File file;

    Analisador(ReticuladoI reticulado, String pathToFolder) {
        if (reticulado == null) throw new NullPointerException("Reticulado não pode ser nulo");
        this.pathToFolder = pathToFolder;
        this.fileName = generateFileName(reticulado);
        this.file = new File(getFileName());

        try {
            if (file.createNewFile()){
                System.out.println("Arquivo criado: " + file.getName());
            } else {
                System.out.println("Arquivo "+file.getName()+" já existe.");
            }
        }catch (IOException e){
            System.out.println("Erro ao criar arquivo.");
            e.printStackTrace();
        }

    }
    //public abstract void printaEstados(@NotNull ReticuladoI reticulado);


    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String generateFileName(ReticuladoI reticulado){
        var formatter = new SimpleDateFormat("[dd-MM-yyyy_HH-mm]");

        return formatter.format(new Date()) +
                "_" +
                reticulado.getTipoInicial() +
                "_" +
                reticulado.getUmidade();
    }
    public abstract String getFileName();
}

