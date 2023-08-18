package model.analise;

import model.analise.observers.SubReticuladoAvancou;
import model.reticulado.ReticuladoI;

public class Bruto extends Analisador implements SubReticuladoAvancou {
    char[] emojis;
    ImpressoraReticulado impressora;
    public Bruto(ReticuladoI reticulado,String fileName) {
        super(reticulado, fileName);
        this.impressora = new ImpressoraBufferizada(1, reticulado);

    }


    public void printaEstados(ReticuladoI reticulado) {
        int[][] reticuladoNumber = reticulado.getReticulado();
        for (int i = 0; i < reticuladoNumber.length; i++) {
            for (int j = 0; j < reticuladoNumber[i].length; j++) {
                System.out.print(reticuladoNumber[i][j] + " ");
            }
            System.out.println();
        }
        System.out.println();
    }

    @Override
    public void reticuladoAvancou() {
        impressora.printaEstados(getFile(), reticulado);
    }
    public void reticuladoTerminou(ReticuladoI reticuladoAtual){
        System.out.println("Reticulado "+ fileName + " terminou.");
    }

}
