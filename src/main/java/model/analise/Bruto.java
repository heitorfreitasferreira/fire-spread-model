package model.analise;

import model.analise.observers.SubReticuladoAvancou;
import model.reticulado.Reticulado;
import model.reticulado.ReticuladoI;

public class Bruto extends Analisador implements SubReticuladoAvancou {
    ImpressoraReticulado impressora;
    public Bruto(Reticulado reticulado, String fileName) {
        super(reticulado, fileName);
        this.impressora = new ImpressoraBufferizada(Reticulado.QNT_ITERACOES, reticulado);
    }

    @Override
    public void reticuladoAvancou() {
        impressora.printaEstados(getFile(), reticulado);
    }
    public void reticuladoTerminou(ReticuladoI reticuladoAtual){
        System.out.println("Reticulado "+ fileName + " terminou.");
    }

}
