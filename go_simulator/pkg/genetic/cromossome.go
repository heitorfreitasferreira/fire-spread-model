package genetic

import (
	"strconv"

	"github.com/heitorfreitasferreira/fireSpreadSimultor/pkg/automata/model"
)

type Cromossome []float64

func (cromossome *Cromossome) ToString(separator string) string {
	str := ""
	for i, value := range *cromossome {
		str += strconv.FormatFloat(value, 'f', -1, 64)
		if i < len(*cromossome)-1 {
			str += separator
		}
	}
	return str
}

func (cromossome *Cromossome) toModelParams() (model.Parameters, error) {
	if len(*cromossome) != 7 {
		return model.Parameters{}, &InvalidCromossomeError{}
	}
	return model.Parameters{
		InfluenciaUmidade:                   (*cromossome)[0],
		ProbEspalhamentoFogoInicial:         (*cromossome)[1],
		ProbEspalhamentoFogoArvoreQueimando: (*cromossome)[2],
		ProbEspalhamentoFogoQueimaLenta:     (*cromossome)[3],
		InfluenciaVegetacaoCampestre:        (*cromossome)[4],
		InfluenciaVegetacaoSavanica:         (*cromossome)[5],
		InfluenciaVegetacaoFlorestal:        (*cromossome)[6],
	}, nil
}

func fromModelParams(params model.Parameters) Cromossome {
	return Cromossome{
		params.InfluenciaUmidade,
		params.ProbEspalhamentoFogoInicial,
		params.ProbEspalhamentoFogoArvoreQueimando,
		params.ProbEspalhamentoFogoQueimaLenta,
		params.InfluenciaVegetacaoCampestre,
		params.InfluenciaVegetacaoSavanica,
		params.InfluenciaVegetacaoFlorestal,
	}
}

// func modelParamsToArray(params model.Parameters) []float64 {
// 	return []float64{
// 		params.InfluenciaUmidade,
// 		params.ProbEspalhamentoFogoInicial,
// 		params.ProbEspalhamentoFogoArvoreQueimando,
// 		params.ProbEspalhamentoFogoQueimaLenta,
// 		params.InfluenciaVegetacaoCampestre,
// 		params.InfluenciaVegetacaoSavanica,
// 		params.InfluenciaVegetacaoFlorestal,
// 	}
// }

func (c *Cromossome) isValid() bool {
	correctAmout := len(*c) == 7

	anyOutOfRange := false
	for _, value := range *c {
		if value < 0 || value > 1 {
			anyOutOfRange = true
			break
		}
	}
	mParams, err := c.toModelParams()
	if err != nil {
		return false
	}
	return correctAmout && !anyOutOfRange && mParams.AreValuesInOrder()
}

type InvalidCromossomeError struct{}

func (e *InvalidCromossomeError) Error() string {
	return "Invalid cromossome length"
}
