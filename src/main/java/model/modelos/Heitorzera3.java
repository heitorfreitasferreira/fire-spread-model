package model.modelos;

import model.estados.Celula;
import model.reticulado.ReticuladoI;

/**
 * Modelo para o BRACIS.
 */
public class Heitorzera3 extends Heitorzera2{
    public Heitorzera3(ReticuladoI reticulado){
        super(reticulado);
    }
    @Override
    public double influenciaRelevo(Celula central, Celula ondeTemFogo){
        return 1;
    }
}
