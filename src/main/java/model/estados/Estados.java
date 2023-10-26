package model.estados;

public enum Estados {
    SOLO_EXPOSTO(0, "solo_exposto"),
    INICIO_FOGO(1, "inicio_fogo"),
    ARVORE_QUEIMANDO(2, "arvore_queimando"),
    QUEIMA_LENTA(3, "brasa"),
    CAMPESTRE(4, "campestre"),
    SAVANICA(5, "savânica"),
    FLORESTAL(6, "florestal"),
    AGUA(7, "agua");

    public final int VALOR;
    public final String NOME;

    Estados(int VALOR, String nome) {
        this.VALOR = VALOR;
        this.NOME = nome;
    }
    public static Estados valueOf(int valor) {
        for (Estados estado : Estados.values()) {
            if (estado.VALOR == valor) {
                return estado;
            }
        }
        throw new IllegalArgumentException("Valor inválido para o estado.");
    }


}
