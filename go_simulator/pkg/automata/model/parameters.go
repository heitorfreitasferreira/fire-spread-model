package model

import "math/rand"

// Este arquivo contém a definição de um struct que armazena os parâmetros do modelo de propagação de fogo. Eles são os mesmos parametros que estão sendo utilizados nos modelos genéticos que tentam otimizar o modelo de propagação de fogo, logo cuidado ao alterar a struct.
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
	params := Parameters{}
	params.InfluenciaUmidade = rand.Float64()
	params.ProbEspalhamentoFogoInicial = rand.Float64()
	params.ProbEspalhamentoFogoArvoreQueimando = rand.Float64()
	params.ProbEspalhamentoFogoQueimaLenta = rand.Float64()
	params.InfluenciaVegetacaoCampestre = rand.Float64()
	params.InfluenciaVegetacaoSavanica = rand.Float64()
	params.InfluenciaVegetacaoFlorestal = rand.Float64()
	return params
}

func (params *Parameters) AreValuesInOrder() bool {
	return params.InfluenciaVegetacaoCampestre <
		params.InfluenciaVegetacaoFlorestal &&
		params.InfluenciaVegetacaoFlorestal <
			params.InfluenciaVegetacaoSavanica &&
		params.ProbEspalhamentoFogoQueimaLenta <
			params.ProbEspalhamentoFogoInicial &&
		params.ProbEspalhamentoFogoInicial <
			params.ProbEspalhamentoFogoArvoreQueimando
}
