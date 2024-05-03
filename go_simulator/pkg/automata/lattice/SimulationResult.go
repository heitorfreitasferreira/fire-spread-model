package lattice

import (
	"fmt"

	"github.com/heitorfreitasferreira/fireSpreadSimultor/pkg/automata/lattice/cell"
)

type SimulationResult [][][]cell.CellState

func (result *SimulationResult) ViewLattice() {
	var separator string
	for i := 0; i < len((*result)[0]); i++ {
		separator += "- "
	}

	for _, iteration := range *result {
		for _, row := range iteration {
			for _, c := range row {
				fmt.Printf("%d ", c)
			}
			fmt.Println()
		}
		fmt.Printf("\n%s\n\n", separator)
	}
}
