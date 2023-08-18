package model.vento;

public enum DirecoesVento {
    N, NE, E, SE, S, SO, O, NO;

    @Override
    public String toString(){
        return switch (this) {
            case N -> "n";
            case NE -> "ne";
            case E -> "e";
            case SE -> "se";
            case S -> "s";
            case SO -> "so";
            case O -> "o";
            case NO -> "no";
            };
    }
}
