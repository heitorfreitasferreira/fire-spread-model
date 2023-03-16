package model.modelos;

import model.estados.Celula;
//Abstract Factory
public interface Modelo {
    Celula handleSavanica(Celula n, Celula s, Celula o, Celula l, Celula ne, Celula no, Celula se, Celula so, Celula central);
    Celula handleFlorestal(Celula n, Celula s, Celula o, Celula l, Celula ne, Celula no, Celula se, Celula so, Celula central);
    Celula handleCampestre(Celula n, Celula s, Celula o, Celula l, Celula ne, Celula no, Celula se, Celula so, Celula central);
    Celula handleSoloExposto();
    Celula handleFogoInicial();
    Celula handleFogoEstavel();
    Celula handleBrasa();

    Celula avanca(Celula n, Celula s, Celula o, Celula l, Celula ne, Celula no, Celula se, Celula so, Celula central);
}
