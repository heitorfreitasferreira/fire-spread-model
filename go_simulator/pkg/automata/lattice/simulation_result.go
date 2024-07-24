package lattice

import (
	"fmt"
	"strings"

	"github.com/heitorfreitasferreira/fireSpreadSimultor/pkg/automata/lattice/cell"
	"github.com/heitorfreitasferreira/fireSpreadSimultor/utils"
)

type SimulationResult [][][]cell.CellState

func (result *SimulationResult) ViewLattice() {
	fmt.Printf("%s", result.ToString())
}

func (result SimulationResult) ToString() string {
	var separator string
	var sb = strings.Builder{}
	for i := 0; i < len(result[0]); i++ {
		separator += "- "
	}

	// var str string
	for _, iteration := range result {
		for _, row := range iteration {
			for _, c := range row {
				sb.WriteString(fmt.Sprintf("%d ", c))
				// str += fmt.Sprintf("%d ", c)
			}
			sb.WriteString("\n")
			// str += "\n"
		}
		// str += fmt.Sprintf("\n%s\n\n", separator)
		sb.WriteString(fmt.Sprintf("%s\n", separator))
	}
	return sb.String()
}

func (result SimulationResult) GetLastIteration() [][]cell.CellState {
	return result[len(result)-1]
}

func (result SimulationResult) Get(pos utils.Vector3D[int]) cell.CellState {
	return result[pos.K][pos.I][pos.J]
}

func (result SimulationResult) ViewLastIteration() {
	var sb = strings.Builder{}

	for _, row := range result.GetLastIteration() {
		for _, c := range row {
			sb.WriteString(fmt.Sprintf("%d ", c))
		}
		sb.WriteString("\n")
	}
	fmt.Printf("%s", sb.String())
}
