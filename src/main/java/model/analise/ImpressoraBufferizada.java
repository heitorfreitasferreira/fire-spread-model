package model.analise;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import model.reticulado.ReticuladoI;

import java.io.File;
import java.io.FileWriter;

public class ImpressoraBufferizada implements ImpressoraReticulado {
    public final int BUFFER_SIZE;
    public int[][][] buffer;

    ImpressoraBufferizada(int size, ReticuladoI reticulado) {
        BUFFER_SIZE = size;
        buffer = new int[BUFFER_SIZE][reticulado.getAltura()][reticulado.getLargura()];
    }

    public void printaEstados(File file, ReticuladoI reticulado) {
        if (reticulado.getIteracao() < BUFFER_SIZE-1) {
            buffer[reticulado.getIteracao()] = reticulado.getReticulado();
        } else {
            printaBuffer(file);
            buffer = new int[BUFFER_SIZE][reticulado.getAltura()][reticulado.getLargura()];
        }
    }

    public void printaBuffer(File file) {
        Gson gson = new GsonBuilder().create();

        try (FileWriter fileWriter = new FileWriter(file)) {
            fileWriter.write("[\n");
            for (int i = 0; i < BUFFER_SIZE; i++) {
                int[][] reticulado = buffer[i];
                String json = gson.toJson(reticulado);
                fileWriter.write(i < BUFFER_SIZE - 1 ? json + ",\n" : json);
            }
            fileWriter.write("\n]");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
