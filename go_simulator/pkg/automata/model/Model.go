package model

import (
	"math/rand"
	"sync"

	"github.com/heitorfreitasferreira/fireSpreadSimultor/pkg/automata/lattice/cell"
)

var mu sync.RWMutex

type Parameters struct {
	InfluenciaUmidade                   float64
	ProbEspalhamentoFogoInicial         float64
	ProbEspalhamentoFogoArvoreQueimando float64
	ProbEspalhamentoFogoQueimaLenta     float64
	InfluenciaVegetacaoCampestre        float64
	InfluenciaVegetacaoSavanica         float64
	InfluenciaVegetacaoFlorestal        float64
}

var nextState = map[cell.CellState]cell.CellState{
	cell.ASH:          cell.ASH,
	cell.INITIAL_FIRE: cell.FIRE,
	cell.FIRE:         cell.EMBER,
	cell.EMBER:        cell.ASH,
	cell.MEADOW:       cell.INITIAL_FIRE,
	cell.SAVANNAH:     cell.INITIAL_FIRE,
	cell.FOREST:       cell.INITIAL_FIRE,
	cell.ROOTS:        cell.EMBER,
	cell.WATER:        cell.WATER,
}

var maxTimeInState = map[cell.CellState]uint16{
	cell.INITIAL_FIRE: 3,
	cell.FIRE:         3,
	cell.EMBER:        10,
	cell.ASH:          ^uint16(0),
	cell.MEADOW:       ^uint16(0),
	cell.SAVANNAH:     ^uint16(0),
	cell.FOREST:       ^uint16(0),
	cell.ROOTS:        ^uint16(0),
	cell.WATER:        ^uint16(0),
}

var probFireSpreadTo = map[cell.CellState]float64{
	cell.ASH:          0,
	cell.INITIAL_FIRE: 0,
	cell.FIRE:         0,
	cell.EMBER:        0,
	cell.MEADOW:       0,
	cell.SAVANNAH:     0,
	cell.FOREST:       0,
	cell.ROOTS:        0,
	cell.WATER:        0,
}

var probCentralCatchingFire = map[cell.CellState]float64{
	cell.ASH:          0,
	cell.INITIAL_FIRE: 0,
	cell.FIRE:         0,
	cell.EMBER:        0,
	cell.MEADOW:       0,
	cell.SAVANNAH:     0,
	cell.FOREST:       0,
	cell.ROOTS:        0,
	cell.WATER:        0,
}

func Step(neighbors [][]*cell.Cell, params Parameters, windMatrix [][]float64) {
	mu.Lock()
	probCentralCatchingFire[cell.MEADOW] = params.InfluenciaVegetacaoCampestre
	probCentralCatchingFire[cell.SAVANNAH] = params.InfluenciaVegetacaoSavanica
	probCentralCatchingFire[cell.FOREST] = params.InfluenciaVegetacaoFlorestal

	probFireSpreadTo[cell.EMBER] = params.ProbEspalhamentoFogoQueimaLenta
	probFireSpreadTo[cell.FIRE] = params.ProbEspalhamentoFogoArvoreQueimando
	probFireSpreadTo[cell.INITIAL_FIRE] = params.ProbEspalhamentoFogoInicial
	mu.Unlock()
	var central *cell.Cell = neighbors[len(neighbors)/2][len(neighbors)/2]

	if central.State.IsFire() && central.IterationsInState >= maxTimeInState[central.State] {
		central.SetNextState(nextState[central.State])
		return
	}
	if central.State.IsBurnable() && isCloseToFire(neighbors) {
		stepBurnable(neighbors, params, windMatrix)
	}
}

func isCloseToFire(neighbors [][]*cell.Cell) bool {
	for _, row := range neighbors {
		for _, c := range row {
			if c.State.IsFire() {
				return true
			}
		}
	}
	return false
}

func stepBurnable(neighbors [][]*cell.Cell, params Parameters, windMatrix [][]float64) {
	var probMatrix [][]float64 = getProbabilities(neighbors)
	var central *cell.Cell = neighbors[len(neighbors)/2][len(neighbors)/2]

	for _, typeOfFire := range cell.FireStates() {
		for i, row := range neighbors {
			for j, c := range row {
				mu.RLock()
				probability := probFireSpreadTo[typeOfFire] * probCentralCatchingFire[central.State] * params.InfluenciaUmidade * windMatrix[i][j]
				mu.RUnlock()
				if c.State == typeOfFire && probMatrix[i][j] < probability {
					central.SetNextState(typeOfFire)
					return
				}

			}
		}

	}
}

func getProbabilities(neighbors [][]*cell.Cell) [][]float64 {
	var numRows, numCols int = len(neighbors), len(neighbors[0])
	probabilities := make([][]float64, len(neighbors))

	for i := 0; i < numRows; i++ {
		probabilities[i] = make([]float64, len(neighbors[i]))
		for j := 0; j < numCols; j++ {
			probabilities[i][j] = rand.Float64()
		}
	}

	return probabilities
}
