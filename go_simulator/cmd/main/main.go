package main

import (
	"fmt"
	"time"

	"github.com/heitorfreitasferreira/fireSpreadSimultor/pkg/automata/lattice"
	"github.com/heitorfreitasferreira/fireSpreadSimultor/pkg/automata/lattice/cell"
	"github.com/heitorfreitasferreira/fireSpreadSimultor/pkg/automata/model"
)

func main() {

	var latticeParams = lattice.LatticeParams{
		Height:        64,
		Width:         64,
		Humidity:      0.5,
		Iterations:    50,
		WindDirection: model.N,
		InitialState:  cell.SAVANNAH,
		FireSpots:     []lattice.Vector2D{{I: 32, J: 32}},
	}

	var modelParams model.Parameters = model.Parameters{
		InfluenciaUmidade:                   1,
		ProbEspalhamentoFogoInicial:         0.6,
		ProbEspalhamentoFogoArvoreQueimando: 1,
		ProbEspalhamentoFogoQueimaLenta:     0.2,
		InfluenciaVegetacaoCampestre:        0.6,
		InfluenciaVegetacaoSavanica:         1,
		InfluenciaVegetacaoFlorestal:        0.8,
	}

	startTime := time.Now()
	lattice.CreateAndRunLatticesParallel(latticeParams, modelParams, 500)
	fmt.Println("Tempo total: ", time.Since(startTime))
}
