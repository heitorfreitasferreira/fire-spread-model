package model.analise;

import model.analise.observers.SubPegouFogo;
import model.analise.observers.SubReticuladoAvancou;
import model.reticulado.Reticulado;

import java.io.FileWriter;
import java.io.IOException;

public class Estatistico extends Analisador implements SubPegouFogo, SubReticuladoAvancou {
    private int[][] qntFogo;
    private int qntFogoAcumulado;
    public Estatistico(Reticulado reticulado, String pathToFolder) {
        super(reticulado, pathToFolder);
        qntFogo = new int[Reticulado.QNT_EXECUCOES][Reticulado.QNT_ITERACOES+1];
        for (int i = 0; i < Reticulado.QNT_EXECUCOES; i++) {
            for (int j = 0; j < Reticulado.QNT_ITERACOES+1; j++) {
                qntFogo[i][j] = 0;
            }
        }
        try{
            var fw = new FileWriter(getFile());
            fw.write("[\n");
            fw.close();
        }catch (IOException ioe){
            System.out.println("Erro ao abrir o arquivo para escrita.");
            ioe.printStackTrace();
        }
    }


    public void pegouFogo(int i, int j) {
        qntFogoAcumulado++;
    }
    public void reticuladoAvancou() throws IOException {
        qntFogo[reticulado.getExecucaoAtual()][reticulado.getIteracao()]= qntFogoAcumulado;
        FileWriter fw = null;
        try {
            fw = new FileWriter(getFile());
            fw.write("\t[");
            for(int i : qntFogo[reticulado.getExecucaoAtual()]){
                fw.write(i+", ");
            }
            fw.write("],\n");
        }catch (IOException ioe){
            System.out.println("Erro ao abrir o arquivo para escrita.");
            ioe.printStackTrace();
        }
        finally {
            fw.close();
        }
    }
    public void reticuladoTerminou(Reticulado reticuladoAtual) throws IOException {
        FileWriter fw = null;
        try{
            fw = new FileWriter(getFile());
            fw.write("\n]");
            fw.close();
        }catch (IOException ioe){
            System.out.println("Erro ao abrir o arquivo para escrita.");
            ioe.printStackTrace();
        }
        finally {
            if (fw != null)
                fw.close();
        }
    }
}