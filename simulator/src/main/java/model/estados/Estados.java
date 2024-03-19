package model.estados;

import java.util.List;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Estados {
    SOLO_EXPOSTO(0, "solo_exposto"),
    INICIO_FOGO(1, "inicio_fogo"),
    ARVORE_QUEIMANDO(2, "arvore_queimando"),
    QUEIMA_LENTA(3, "brasa"),
    CAMPESTRE(4, "campestre"),
    SAVANICA(5, "savânica"),
    FLORESTAL(6, "florestal"),
    AGUA(7, "agua"),
    RAIZ(8, "raiz");

    public final int VALOR;
    public final String NOME;

    public static Estados valueOf(int valor) {
        for (Estados estado : Estados.values()) {
            if (estado.VALOR == valor) {
                return estado;
            }
        }
        throw new IllegalArgumentException("Valor inválido para o estado.");
    }

    public static List<Estados> getEstadosDeFogo() {
        return List.of(INICIO_FOGO, ARVORE_QUEIMANDO, QUEIMA_LENTA);
    }

    public Boolean isVegetacao() {
        return this == CAMPESTRE || this == SAVANICA || this == FLORESTAL || this == RAIZ;
    }

    public Boolean isFogo() {
        return this == INICIO_FOGO || this == ARVORE_QUEIMANDO || this == QUEIMA_LENTA;
    }
}
