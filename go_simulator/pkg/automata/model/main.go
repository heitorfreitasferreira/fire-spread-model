package model

import (
	"fmt"
	"math/rand"

	"github.com/heitorfreitasferreira/fireSpreadSimultor/pkg/automata/lattice/cell"
	"github.com/heitorfreitasferreira/fireSpreadSimultor/utils"
)

type Parameters struct {
	InfluenciaUmidade                   float64
	ProbEspalhamentoFogoInicial         float64
	ProbEspalhamentoFogoArvoreQueimando float64
	ProbEspalhamentoFogoQueimaLenta     float64
	InfluenciaVegetacaoCampestre        float64
	InfluenciaVegetacaoSavanica         float64
	InfluenciaVegetacaoFlorestal        float64
}

type ModelRunner struct {
	params Parameters

	windMatrix [][]float64

	humidityInfluence float64

	nextState               map[cell.CellState]cell.CellState
	maxTimeInState          map[cell.CellState]uint16
	probFireSpreadTo        map[cell.CellState]float64
	probCentralCatchingFire map[cell.CellState]float64

	probFireSeedCatchingFire map[cell.CellState]float64 //
}

func NewRunner(modelParams Parameters, windParams MatrixParams, humidity float32) ModelRunner {

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

	var probFireSeedCatchingFire = map[cell.CellState]float64{
		cell.ASH:          0,
		cell.INITIAL_FIRE: 0,
		cell.FIRE:         0,
		cell.EMBER:        0,
		cell.ROOTS:        0,
		cell.WATER:        0,

		cell.MEADOW:   0.1,
		cell.SAVANNAH: 0.2,
		cell.FOREST:   0.05,
	}
	return ModelRunner{
		params:                   modelParams,
		nextState:                nextState,
		maxTimeInState:           maxTimeInState,
		probFireSpreadTo:         probFireSpreadTo,
		probCentralCatchingFire:  probCentralCatchingFire,
		humidityInfluence:        calculateHumidityInfluence(humidity),
		windMatrix:               windParams.CreateMatrix(),
		probFireSeedCatchingFire: probFireSeedCatchingFire,
	}
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

func (r *ModelRunner) Step(neighbors [][]*cell.Cell) {
	var central *cell.Cell = neighbors[len(neighbors)/2][len(neighbors)/2]

	if central.State.IsFire() && central.IterationsInState >= r.maxTimeInState[central.State] {
		central.SetNextState(r.nextState[central.State])
		return
	}
	if central.State.IsBurnable() {
		if central.HasFireSeed && (rand.Float64() < r.probFireSeedCatchingFire[central.State]) {
			fmt.Printf("Fire seed caught fire\n")
			central.SetNextState(r.nextState[central.State])
		}
		if isCloseToFire(neighbors) {
			r.stepBurnable(neighbors)
		}
	}
}

func (r *ModelRunner) PlantFireSeeds(cells [][]*cell.Cell) {
	for i, row := range cells {
		for j, c := range row {
			if c.State == cell.FIRE {
				r.plantFireSeedFrom(i, j, cells)
			}
		}
	}
}

func (r *ModelRunner) plantFireSeedFrom(i, j int, cells [][]*cell.Cell) {
	const fireSeedCreationProbabilityTreshold = 0.1
	// Se não for criar já encerra o processamento
	if rand.Float64() > fireSeedCreationProbabilityTreshold {
		return
	}

	positions := NLowestPositions(3, r.windMatrix)
	latticeHeight := len(cells)
	latticeWidth := len((cells)[0])

	maxDistance := 3
	for _, pos := range positions {
		iRange := uint16(rand.Intn(maxDistance))
		iDeltaUnClamped := int((pos.I - 1) * iRange)
		iDelta := utils.Clamp(iDeltaUnClamped, 0, (latticeHeight - 1))

		jRange := uint16(rand.Intn(maxDistance))
		jDeltaUnClamped := int((pos.J - 1) * jRange)
		jDelta := utils.Clamp(jDeltaUnClamped, 0, (latticeWidth - 1))

		cells[i+iDelta][j+jDelta].HasFireSeed = true

		fmt.Printf("Planting fire seed at i:%d, j:%d\n", i+iDelta, j+jDelta)
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

func (r *ModelRunner) stepBurnable(neighbors [][]*cell.Cell) {
	var probMatrix [][]float64 = getProbabilities(neighbors)
	var central *cell.Cell = neighbors[len(neighbors)/2][len(neighbors)/2]

	for _, typeOfFire := range cell.FireStates() {
		for i, row := range neighbors {
			for j, c := range row {
				probability := r.probFireSpreadTo[typeOfFire] * r.probCentralCatchingFire[central.State] * r.humidityInfluence * r.windMatrix[i][j]
				if c.State == typeOfFire && probMatrix[i][j] < probability {
					central.SetNextState(r.nextState[central.State])
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

func calculateHumidityInfluence(humidity float32) float64 {
	switch {
	case humidity > 0.0 && humidity <= 0.25:
		return 1.5
	case humidity > 0.25 && humidity <= 0.5:
		return 1.0
	case humidity > 0.5 && humidity <= 0.75:
		return 0.8
	case humidity > 0.75 && humidity <= 1.0:
		return 0.6
	default:
		return 0
	}
}
