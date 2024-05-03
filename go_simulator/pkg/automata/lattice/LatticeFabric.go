package lattice

import (
	"github.com/heitorfreitasferreira/fireSpreadSimultor/pkg/automata/model"
)

func CreateAndRunLatticesParallel(
	params LatticeParams,
	modelParams model.Parameters,
	numberOfSimulations uint16) []SimulationResult {
	var lattices []Lattice = make([]Lattice, numberOfSimulations)
	for i := uint16(0); i < numberOfSimulations; i++ {
		lattices[i] = CreateLattice(params)
	}

	var results []SimulationResult = make([]SimulationResult, numberOfSimulations)
	resultChan := make(chan SimulationResult)

	for i := uint16(0); i < numberOfSimulations; i++ {
		go func(i uint16) {
			resultChan <- lattices[i].Run(modelParams)
		}(i)
	}

	for i := uint16(0); i < numberOfSimulations; i++ {
		results[i] = <-resultChan
	}

	return results
}
