package model.analise;

import lombok.extern.java.Log;
import model.reticulado.Reticulado;

import java.io.File;
import java.io.FileWriter;

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
      printaBufferToJson(file, reticulado.toString());
      buffer = new int[BUFFER_SIZE][reticulado.getAltura()][reticulado.getLargura()];
    }
  }

  public void printaBufferToJson(File file, String metaInfo) {
    try (FileWriter fileWriter = new FileWriter(file)) {
      fileWriter.write(convertToCustomJson(metaInfo));
    } catch (Exception e) {
      log.severe(e.getMessage());
    }
  }
  public String convertToCustomJson(String metaInfo) {
    StringBuilder jsonBuilder = new StringBuilder();
    jsonBuilder.append("{\n");
    jsonBuilder.append("  \"config\": ").append(metaInfo).append(",\n");
    jsonBuilder.append("  \"execution\": [\n");

    for (int i = 0; i < buffer.length; i++) {
      jsonBuilder.append("    [\n");
      for (int j = 0; j < buffer[i].length; j++) {
        jsonBuilder.append("      [");
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
      jsonBuilder.append("    ]");
      if (i < buffer.length - 1) {
        jsonBuilder.append(",\n");
      } else {
        jsonBuilder.append("\n");
      }
    }

    jsonBuilder.append("  ]\n");
    jsonBuilder.append("}");
    String temp = jsonBuilder.toString();
    return temp;
  }
}
