package model.modelos;

public record ModelParameters(
        double influenciaUmidade,
        double probEspalhamentoFogoInicial,
        double probEspalhamentoFogoArvoreQueimando,
        double probEspalhamentoFogoQueimaLenta,
        double influenciaVegetacaoCampestre,
        double influenciaVegetacaoSavanica,
        double influenciaVegetacaoFlorestal) {

    @Override
    public String toString() {
        return "(" +
                influenciaUmidade + "|" +
                probEspalhamentoFogoInicial + "|" +
                probEspalhamentoFogoArvoreQueimando + "|" +
                probEspalhamentoFogoQueimaLenta + "|" +
                influenciaVegetacaoCampestre + "|" +
                influenciaVegetacaoSavanica + "|" +
                influenciaVegetacaoFlorestal
                + ")";
    }

    public ModelParameters(double[] values) {
        this(
                values[0],
                values[1],
                values[2],
                values[3],
                values[4],
                values[5],
                values[6]);
    }
}
