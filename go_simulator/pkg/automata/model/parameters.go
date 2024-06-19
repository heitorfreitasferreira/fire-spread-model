package model

import "math/rand"

type Parameters struct {
	InfluenciaUmidade                   float64
	ProbEspalhamentoFogoInicial         float64
	ProbEspalhamentoFogoArvoreQueimando float64
	ProbEspalhamentoFogoQueimaLenta     float64
	InfluenciaVegetacaoCampestre        float64
	InfluenciaVegetacaoSavanica         float64
	InfluenciaVegetacaoFlorestal        float64
}

func RandomParams() Parameters {
	modelParameters := Parameters{}
	modelParameters.InfluenciaUmidade = rand.Float64()
	modelParameters.ProbEspalhamentoFogoInicial = rand.Float64()
	modelParameters.ProbEspalhamentoFogoArvoreQueimando = rand.Float64()
	modelParameters.ProbEspalhamentoFogoQueimaLenta = rand.Float64()
	modelParameters.InfluenciaVegetacaoCampestre = rand.Float64()
	modelParameters.InfluenciaVegetacaoSavanica = rand.Float64()
	modelParameters.InfluenciaVegetacaoFlorestal = rand.Float64()
	return modelParameters
}

func (modelParameters *Parameters) AreValuesInOrder() bool {
	return modelParameters.InfluenciaVegetacaoCampestre <
		modelParameters.InfluenciaVegetacaoFlorestal &&
		modelParameters.InfluenciaVegetacaoFlorestal <
			modelParameters.InfluenciaVegetacaoSavanica &&
		modelParameters.ProbEspalhamentoFogoQueimaLenta <
			modelParameters.ProbEspalhamentoFogoInicial &&
		modelParameters.ProbEspalhamentoFogoInicial <
			modelParameters.ProbEspalhamentoFogoArvoreQueimando
}
