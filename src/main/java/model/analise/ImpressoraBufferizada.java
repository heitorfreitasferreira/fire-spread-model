package model.analise;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.File;
import java.io.FileWriter;
import java.util.Arrays;

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
      printaBufferToJson(file);
      buffer = new int[BUFFER_SIZE][reticulado.getAltura()][reticulado.getLargura()];
    }
  }

  public void printaBufferToJson(File file) {
    Gson gson = new GsonBuilder().create();

    try (FileWriter fileWriter = new FileWriter(file)) {
  fileWriter.write(convertToCustomJson(buffer));
    } catch (Exception e) {
      log.severe(e.getMessage());
    }
  }
  private String convertToCustomJson(int[][][] buffer) {
    StringBuilder jsonBuilder = new StringBuilder();
    jsonBuilder.append("[\n");
    for (int i = 0; i < buffer.length; i++) {
      jsonBuilder.append("  [\n");
      for (int j = 0; j < buffer[i].length; j++) {
        jsonBuilder.append("    [");
        for (int k = 0; k < buffer[i][j].length; k++) {
          jsonBuilder.append(buffer[i][j][k]);
          if (k < buffer[i][j].length - 1) {
            jsonBuilder.append(", ");
          }
        }
        jsonBuilder.append("]");
        if (j < buffer[i].length - 1) {
          jsonBuilder.append(",\n");
        } else {
          jsonBuilder.append("\n");
        }
      }
      jsonBuilder.append("  ]");
      if (i < buffer.length - 1) {
        jsonBuilder.append(",\n");
      } else {
        jsonBuilder.append("\n");
      }
    }
    jsonBuilder.append("]");
    return jsonBuilder.toString();
  }}
