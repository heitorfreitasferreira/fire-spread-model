package model.modelos;

import model.estados.Celula;
import model.reticulado.Reticulado;

/**
 * Modelo para o BRACIS.
 */
public class Heitorzera3 extends Heitorzera2{
    public Heitorzera3(Reticulado reticulado){
        super(reticulado);
    }
    @Override
    public double influenciaRelevo(Celula central, Celula ondeTemFogo){
        return 1;
    }
}
