package model.analise;

import model.analise.observers.SubPegouFogo;
import model.analise.observers.SubReticuladoAvancou;
import model.reticulado.Reticulado;
import model.reticulado.ReticuladoI;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class Estatistico extends Analisador implements SubPegouFogo, SubReticuladoAvancou {
    private ArrayList<ArrayList<Integer>> qntFogo;
    private int qntFogoAcumulado;
    public Estatistico(ReticuladoI reticulado, String pathToFolder) {
        super(reticulado, pathToFolder);
        qntFogo = new ArrayList<>();
        for (int i = 0; i < Reticulado.QNT_EXECUCOES; i++) {
            qntFogo.add(new ArrayList<>());
            for (int j = 0; j < Reticulado.QNT_ITERACOES; j++) {
                qntFogo.get(i).add(0);
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

    public void printaEstados(ReticuladoI reticulado) {

    }

    public String getFileName() {
        return pathToFolder + fileName + ".txt";
    }

    public void pegouFogo(int i, int j) {
        qntFogoAcumulado++;
    }
    public void reticuladoAvancou(ReticuladoI reticuladoAtual){
        qntFogo.get(reticuladoAtual.getExecucaoAtual()).set(reticuladoAtual.getIteracao(), qntFogoAcumulado);
        try {
            var fw = new FileWriter(file);
            fw.write("\t[");
            for(int i : qntFogo.get(reticuladoAtual.getExecucaoAtual())){
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