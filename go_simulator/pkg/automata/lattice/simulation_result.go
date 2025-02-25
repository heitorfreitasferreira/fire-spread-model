package lattice

import (
	"fmt"
	"strings"
	"time"

	"github.com/heitorfreitasferreira/fireSpreadSimultor/pkg/automata/lattice/cell"
	"github.com/heitorfreitasferreira/fireSpreadSimultor/utils"
)

type SimulationResult [][][]cell.CellState

func (result SimulationResult) ViewLattice() {
	// fmt.Printf("%s", result.ToString())
	// var separator string
	// for i := 0; i < len(result[0]); i++ {
	// 	separator += "- "
	// }

	for i, iteration := range result {
		if i%10 != 0 {
			continue
		}

		var str string
		for _, row := range iteration {
			for _, c := range row {
				str += c.ToColoredString()
			}
			str += "\n"
		}
		fmt.Printf("\033[H\033[2J%s\n", str)
		// Wait 0.1 seconds
		time.Sleep(50 * time.Millisecond)
	}
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
				sb.WriteString(c.ToColoredString())
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
