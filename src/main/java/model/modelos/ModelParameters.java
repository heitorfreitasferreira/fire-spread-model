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

}
