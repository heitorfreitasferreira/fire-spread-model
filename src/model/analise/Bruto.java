package model.analise;

import model.analise.observers.SubReticuladoAvancou;
import model.reticulado.ReticuladoI;

import java.io.FileWriter;
import java.io.IOException;

public class Bruto extends Analisador implements SubReticuladoAvancou {
    public Bruto(ReticuladoI reticulado,String fileName) {
        super(reticulado, fileName);
    }


    public void printaEstados(ReticuladoI reticulado) {
        int[][] reticuladoNumber = reticulado.getReticulado();
        for (int i = 0; i < reticuladoNumber.length; i++) {
            for (int j = 0; j < reticuladoNumber[i].length; j++) {
                System.out.print(reticuladoNumber[i][j] + " ");
            }
            System.out.println();
        }
    }

    @Override
    public void reticuladoAvancou(ReticuladoI reticuladoAtual) {
        printaEstados(reticuladoAtual);
        System.out.println();
        try {
            var fw = new FileWriter(file);
            var matriz = reticuladoAtual.getReticulado();
            for (int i = 0; i < matriz.length; i++) {
                for (int j = 0; j < matriz.length; j++)
                    fw.write(matriz[i][j] + " ");
                fw.write("\n");
            }
            fw.write("-\n");
            fw.close();
        } catch (IOException e) {
            System.out.println("Erro ao abrir o arquivo para escrita.");
            e.printStackTrace();
        }
    }
    public void reticuladoTerminou(ReticuladoI reticuladoAtual){
        System.out.println("Reticulado "+ fileName + " terminou.");
    }
    public void pegouFogo() {

    }
    public String getFileName() {
        return pathToFolder + fileName+".txt";
    }
}
