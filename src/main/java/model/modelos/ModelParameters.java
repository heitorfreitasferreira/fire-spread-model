package model.modelos;


public record ModelParameters(
        double influenciaUmidade,
        double probEspalhamentoFogoInicial,
        double probEspalhamentoFogoArvoreQueimando,
        double probEspalhamentoFogoQueimaLenta,
        double influenciaVegetacaoCampestre,
        double influenciaVegetacaoSavanica,
        double influenciaVegetacaoFlorestal
) {

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
}
