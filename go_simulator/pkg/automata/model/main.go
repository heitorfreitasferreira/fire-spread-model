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

	windMatrix WindMatrix

	humidityInfluence float64

	nextState               map[cell.CellState]cell.CellState
	maxTimeInState          map[cell.CellState]uint16
	probFireSpreadTo        map[cell.CellState]float64
	probCentralCatchingFire map[cell.CellState]float64

	probFireSeedCatchingFire map[cell.CellState]float64 //

	deltaPos [][]int

	radius int

	board *[][]*cell.Cell
}

func NewRunner(modelParams Parameters, windParams MatrixParams, humidity float32, board *[][]*cell.Cell) ModelRunner {

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
		cell.INITIAL_FIRE: 0.6,
		cell.FIRE:         1.0,
		cell.EMBER:        0.2,
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
		cell.MEADOW:       0.6,
		cell.SAVANNAH:     1.0,
		cell.FOREST:       0.8,
		cell.ROOTS:        0.1,
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
		params:         modelParams,
		nextState:      nextState,
		maxTimeInState: maxTimeInState,

		probFireSpreadTo:         probFireSpreadTo,
		probCentralCatchingFire:  probCentralCatchingFire,
		probFireSeedCatchingFire: probFireSeedCatchingFire,

		humidityInfluence: calculateHumidityInfluence(humidity),
		windMatrix:        windParams.CreateMatrix(),

		board:    board,
		deltaPos: positionsToLook(windParams.Radius),
		radius:   windParams.Radius,
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

func (r *ModelRunner) Step(i, j int) {
	var central *cell.Cell = (*r.board)[i][j]

	if central.State.IsFire() && central.IterationsInState >= r.maxTimeInState[central.State] {
		central.SetNextState(r.nextState[central.State])
		return
	}
	if central.State.IsBurnable() {
		if central.HasFireSeed && (rand.Float64() < r.probFireSeedCatchingFire[central.State]) {
			fmt.Printf("Fire seed caught fire\n")
			central.SetNextState(r.nextState[central.State])
		}
		r.stepBurnable(i, j)
	}
}

func (r *ModelRunner) PlantFireSeeds() {
	for i, row := range *r.board {
		for j, c := range row {
			if c.State == cell.FIRE {
				r.plantFireSeedFrom(i, j)
			}
		}
	}
}

func (r *ModelRunner) plantFireSeedFrom(i, j int) {
	const fireSeedCreationProbabilityTreshold = 0.0
	if rand.Float64() > fireSeedCreationProbabilityTreshold {
		return
	}

	positions := NLowestPositions(3, r.windMatrix)
	latticeHeight := len(*r.board)
	latticeWidth := len((*r.board)[0])

	maxDistance := 3
	pos := positions[rand.Intn(len(positions))]

	iRange := uint16(rand.Intn(maxDistance))
	iDeltaUnClamped := int((pos.I - 1) * iRange)
	iDelta := utils.Clamp(iDeltaUnClamped, 0, (latticeHeight - 1))

	jRange := uint16(rand.Intn(maxDistance))
	jDeltaUnClamped := int((pos.J - 1) * jRange)
	jDelta := utils.Clamp(jDeltaUnClamped, 0, (latticeWidth - 1))

	(*r.board)[i+iDelta][j+jDelta].HasFireSeed = true

	fmt.Printf("Planting fire seed at i:%d, j:%d\n", i+iDelta, j+jDelta)
}

func (r *ModelRunner) stepBurnable(i, j int) {
	central := (*r.board)[i][j]

	for _, tuple := range r.deltaPos {
		neighborState := r.getStateByRelativePosition(i, tuple[0], j, tuple[1])
		if !neighborState.IsFire() {
			continue
		}

		probability := r.probFireSpreadTo[neighborState] * r.probCentralCatchingFire[central.State] * r.humidityInfluence * r.windMatrix.GetByRelativeNeighborhoodPosition(tuple[0], tuple[1])
		if rand.Float64() < probability {
			central.SetNextState(r.nextState[central.State])
			return
		}
	}
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

func (r *ModelRunner) getStateByRelativePosition(currI, deltaI, currJ, deltaJ int) cell.CellState {
	i := currI + deltaI
	j := currJ + deltaJ
	if i < 0 || i >= len(*r.board) || j < 0 || j >= len((*r.board)[0]) {
		return cell.WATER
	}
	return (*r.board)[i][j].State
}

func positionsToLook(radius int) [][]int {
	positions := make([][]int, 0)
	for i := -radius; i <= radius; i++ {
		for j := -radius; j <= radius; j++ {
			if i == 0 && j == 0 {
				continue
			}
			positions = append(positions, []int{i, j})
		}
	}
	return positions
}
