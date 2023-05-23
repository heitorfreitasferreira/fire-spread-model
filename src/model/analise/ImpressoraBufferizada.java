package model.analise;

import model.reticulado.ReticuladoI;

import java.io.File;

public class ImpressoraBufferizada implements ImpressoraReticulado{
    public final int BUFFER_SIZE;
    public int i;
    public int[][][] buffer;
    public final File file;
    ImpressoraBufferizada(int size, ReticuladoI reticulado, File fileToSave){
        BUFFER_SIZE = size;
        buffer = new int[BUFFER_SIZE][reticulado.getSize()][reticulado.getSize()];
        i =0;
        file = fileToSave;
    }

    private void salvaBuffer() {
        for (int k = 0; k < BUFFER_SIZE; k++) {

            for (int i = 0; i < buffer.length; i++) {
                for (int j = 0; j < buffer[k][i].length; j++) {
                    System.out.print(buffer[k][i][j]);
                }
                System.out.println();
            }
            System.out.println();
        }
    }


    public void printaEstados(File file, ReticuladoI reticulado) {
        buffer[i] = reticulado.getReticulado();
        i++;
        if(i == BUFFER_SIZE){
            salvaBuffer();
            i = 0;
        }
    }
}
