package genetic

import (
	"github.com/heitorfreitasferreira/fireSpreadSimultor/pkg/automata/lattice"
	"github.com/heitorfreitasferreira/fireSpreadSimultor/pkg/automata/lattice/cell"
	"github.com/heitorfreitasferreira/fireSpreadSimultor/utils"
)

func GetFitnessCalculator(goal lattice.SimulationResult) func(simulations *[]lattice.SimulationResult) float64 {
	var fireMaks []utils.Vector3D = make([]utils.Vector3D, 0)
	for i := uint16(0); i < uint16(len(goal)); i++ {
		for j := uint16(0); j < uint16(len(goal[i])); j++ {
			for k := uint16(0); k < uint16(len(goal[i][j])); k++ {
				if goal[i][j][k].IsFire() {
					fireMaks = append(fireMaks, utils.Vector3D{I: i, J: j, K: k})
				}
			}
		}
	}

	return func(simulations *[]lattice.SimulationResult) float64 {
		var totalFitness uint16 = 0
		actualSimulation := getModeOfSimulations(*simulations)
		for _, fireMak := range fireMaks {
			if actualSimulation[fireMak.I][fireMak.J][fireMak.K].IsFire() {
				totalFitness++
			}
		}
		return float64(totalFitness) / float64(len(fireMaks))
	}
}

func getModeOfSimulations(simulations []lattice.SimulationResult) lattice.SimulationResult {

	numberOfIterations := len(simulations[0])
	height := len(simulations[0][0])
	width := len(simulations[0][0][0])

	mode := make(lattice.SimulationResult, numberOfIterations)
	for i := range mode {
		mode[i] = make([][]cell.CellState, height)
		for j := range mode[i] {
			mode[i][j] = make([]cell.CellState, width)
		}
	}

	for i := 0; i < numberOfIterations; i++ {
		for j := 0; j < height; j++ {
			for k := 0; k < width; k++ {
				count := make([]int, len(cell.AllStates()))
				for l := range simulations {
					count[simulations[l][i][j][k]]++
				}
				mode[i][j][k] = simulations[getMaxIndex(count)][i][j][k]
			}
		}
	}
	return mode
}

func getMaxIndex(array []int) int {
	maxIndex := 0
	for i := 1; i < len(array); i++ {
		if array[i] > array[maxIndex] {
			maxIndex = i
		}
	}
	return maxIndex
}
