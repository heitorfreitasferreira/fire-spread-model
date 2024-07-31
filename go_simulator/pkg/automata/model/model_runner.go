package model

import (
	"fmt"
	"math/rand"

	"github.com/heitorfreitasferreira/fireSpreadSimultor/pkg/automata/lattice/cell"
)

type ModelRunner struct {
	Parameters

	WindMatrix

	*rand.Rand

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

func NewRunner(modelParams Parameters, windParams WindParams, humidity float32, board *[][]*cell.Cell, numberGenerator *rand.Rand) ModelRunner {

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
		Parameters:     modelParams,
		nextState:      nextState,
		maxTimeInState: maxTimeInState,

		probFireSpreadTo:         probFireSpreadTo,
		probCentralCatchingFire:  probCentralCatchingFire,
		probFireSeedCatchingFire: probFireSeedCatchingFire,

		humidityInfluence: calculateHumidityInfluence(humidity),
		WindMatrix:        windParams.CreateMatrix(),

		board:    board,
		deltaPos: positionsToLook(windParams.Radius),
		radius:   windParams.Radius,
		Rand:     numberGenerator,
	}
}
func (r *ModelRunner) Step(i, j int) {
	var central *cell.Cell = (*r.board)[i][j]

	if central.State.IsFire() && central.IterationsInState >= r.maxTimeInState[central.State] {
		central.SetNextState(r.nextState[central.State])
		return
	}
	if central.State.IsBurnable() {
		if central.HasFireSeed && (r.Rand.Float64() < r.probFireSeedCatchingFire[central.State]) {
			fmt.Printf("Fire seed caught fire\n")
			central.SetNextState(r.nextState[central.State])
		}
		r.stepBurnable(i, j)
	}
}

func (r *ModelRunner) stepBurnable(i, j int) {
	central := (*r.board)[i][j]

	for _, tuple := range r.deltaPos {
		neighborState := r.getStateByRelativePosition(i, tuple[0], j, tuple[1])
		if !neighborState.IsFire() {
			continue
		}

		probability := r.probFireSpreadTo[neighborState] * r.probCentralCatchingFire[central.State] * r.humidityInfluence * r.WindMatrix.GetByRelativeNeighborhoodPosition(tuple[0], tuple[1])
		if r.Rand.Float64() < probability {
			central.SetNextState(r.nextState[central.State])
			return
		}
	}
}
