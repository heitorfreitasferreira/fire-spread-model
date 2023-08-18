package model.analise;

import model.reticulado.ReticuladoI;

import java.io.File;
import java.io.FileWriter;

public class ImpressoraBufferizada implements ImpressoraReticulado{
    public final int BUFFER_SIZE;
    public int i;
    public int[][][] buffer;
    ImpressoraBufferizada(int size, ReticuladoI reticulado){
        BUFFER_SIZE = size;
        buffer = new int[BUFFER_SIZE][reticulado.getSize()][reticulado.getSize()];
        i =0;
    }



    public void printaEstados(File file, ReticuladoI reticulado) {
        int[][] reticuladoNumber = reticulado.getReticulado();
        try {
            FileWriter writer = new FileWriter(file);
            for (int i = 0; i < reticuladoNumber.length; i++) {
                for (int j = 0; j < reticuladoNumber[i].length; j++) {
                    writer.write(reticuladoNumber[i][j] + " ");
                }
                writer.write("\n");
            }
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
