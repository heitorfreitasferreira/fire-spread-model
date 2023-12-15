package model.analise;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.File;
import java.io.FileWriter;
import lombok.extern.java.Log;
import model.reticulado.Reticulado;

@Log
public class ImpressoraBufferizada implements ImpressoraReticulado {

  public final int BUFFER_SIZE;
  public int[][][] buffer;

  ImpressoraBufferizada(int size, Reticulado reticulado) {
    BUFFER_SIZE = size;
    buffer = new int[BUFFER_SIZE][reticulado.getAltura()][reticulado.getLargura()];
  }

  public void printaEstados(File file, Reticulado reticulado) {
    if (reticulado.getReticuladoSubsolo().isEmpty()) {
      return;// Se não tem subsolo, o proprio é um subsolo, logo não deve ser printado
    }
    if (reticulado.getIteracao() < BUFFER_SIZE - 1) {
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
        fileWriter.append(i < BUFFER_SIZE - 1 ? json + ",\n" : json);
      }
      fileWriter.append("\n]");
    } catch (Exception e) {
      log.severe(e.getMessage());
    }
  }
}
