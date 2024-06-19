package loggers

import (
	"fmt"
	"log"

	"github.com/heitorfreitasferreira/fireSpreadSimultor/pkg/automata/lattice"
	"github.com/heitorfreitasferreira/fireSpreadSimultor/pkg/automata/lattice/cell"
	"github.com/heitorfreitasferreira/fireSpreadSimultor/utils"
)

func ViewFirstSparkPos(simulation lattice.SimulationResult, latticeParams lattice.LatticeParams) {
	fp, err := getFirstFireFromSimulation(simulation, latticeParams)
	if err != nil {
		log.Print(err)
		return
	}
	log.Printf("%d: {i: %d, j: %d}\n", 0, fp.I, fp.J)
}

func getFirstFireFromSimulation(simulation lattice.SimulationResult, latticeParams lattice.LatticeParams) (utils.Vector3D[int], error) {
	for k := 0; k < latticeParams.Iterations; k++ {
		for j := latticeParams.Width/2 - 1; j >= 0; j-- {
			for i := 0; i < latticeParams.Width; i++ {
				currPos := utils.Vector3D[int]{I: i, J: j, K: k}
				if simulation.Get(currPos) == cell.INITIAL_FIRE {
					return currPos, nil
				}
			}
		}
	}
	return utils.Vector3D[int]{}, fmt.Errorf("no fire found in simulation")
}
