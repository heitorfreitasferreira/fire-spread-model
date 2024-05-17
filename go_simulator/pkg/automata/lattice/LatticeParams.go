package lattice

import (
	"fmt"

	"github.com/heitorfreitasferreira/fireSpreadSimultor/pkg/automata/lattice/cell"
	"github.com/heitorfreitasferreira/fireSpreadSimultor/utils"
)

type LatticeParams struct {
	Height     uint8
	Width      uint8
	Humidity   float32
	Iterations uint16
	// WindDirection model.WindDirection
	InitialState cell.CellState
	FireSpots    []utils.Vector3D
}

func (p LatticeParams) ToString() string {
	return fmt.Sprintf("LatticeParams = {Height: %d, Width: %d, Humidity: %f, Iterations: %d, InitialState: %d, FireSpots: %v}",
		p.Height,
		p.Width,
		p.Humidity,
		p.Iterations,
		// p.WindDirection,
		p.InitialState,
		p.FireSpots)
}
