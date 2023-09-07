package model.analise;

import model.reticulado.ReticuladoI;

import java.io.File;
import java.io.FileWriter;

public class ImpressoraBufferizada implements ImpressoraReticulado {
    public final int BUFFER_SIZE;
    public int[][][] buffer;

    ImpressoraBufferizada(int size, ReticuladoI reticulado){
        BUFFER_SIZE = size;
        buffer = new int[BUFFER_SIZE][reticulado.getAltura()][reticulado.getLargura()];
    }

    public void printaEstados(File file, ReticuladoI reticulado) {
        int[][] reticuladoNumber = reticulado.getReticulado();
        FileWriter writer = null;
        try {
            writer = new FileWriter(file);
            for (int[] linhaReticulado : reticuladoNumber) {
                for (int valorCelula : linhaReticulado) {
                    writer.write(valorCelula);
                }
                writer.write("\n");
            }
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (writer != null)
                writer.close();
        }
    }
}
