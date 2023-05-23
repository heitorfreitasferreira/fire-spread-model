package model.analise;

import model.analise.observers.SubPegouFogo;
import model.analise.observers.SubReticuladoAvancou;
import model.reticulado.Reticulado;
import model.reticulado.ReticuladoI;
import org.jetbrains.annotations.NotNull;

import java.io.FileWriter;
import java.io.IOException;

public class Estatistico extends Analisador implements SubPegouFogo, SubReticuladoAvancou {
    private int[][] qntFogo;
    private int qntFogoAcumulado;
    public Estatistico(ReticuladoI reticulado, String pathToFolder) {
        super(reticulado, pathToFolder);
        qntFogo = new int[Reticulado.QNT_EXECUCOES][Reticulado.QNT_ITERACOES+1];
        for (int i = 0; i < Reticulado.QNT_EXECUCOES; i++) {
            for (int j = 0; j < Reticulado.QNT_ITERACOES+1; j++) {
                qntFogo[i][j] = 0;
            }
        }
        try{
            var fw = new FileWriter(file);
            fw.write("[\n");
            fw.close();
        }catch (IOException ioe){
            System.out.println("Erro ao abrir o arquivo para escrita.");
            ioe.printStackTrace();
        }
    }

    public void printaEstados(@NotNull ReticuladoI reticulado) {

    }

    public String getFileName() {
        return pathToFolder + fileName + ".txt";
    }

    public void pegouFogo(int i, int j) {
        qntFogoAcumulado++;
    }
    public void reticuladoAvancou(){
        qntFogo[reticulado.getExecucaoAtual()][reticulado.getIteracao()]= qntFogoAcumulado;
        try {
            var fw = new FileWriter(file);
            fw.write("\t[");
            for(int i : qntFogo[reticulado.getExecucaoAtual()]){
                fw.write(i+", ");
            }
            fw.write("],\n");
        }catch (IOException ioe){
            System.out.println("Erro ao abrir o arquivo para escrita.");
            ioe.printStackTrace();
        }
    }
    public void reticuladoTerminou(ReticuladoI reticuladoAtual){
        try{
            var fw = new FileWriter(file);
            fw.write("\n]");
            fw.close();
        }catch (IOException ioe){
            System.out.println("Erro ao abrir o arquivo para escrita.");
            ioe.printStackTrace();
        }
    }
}