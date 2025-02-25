package analyses

import (
	"testing"

	"github.com/heitorfreitasferreira/fireSpreadSimultor/pkg/automata/lattice"
	"github.com/heitorfreitasferreira/fireSpreadSimultor/pkg/automata/lattice/cell"
	"github.com/heitorfreitasferreira/fireSpreadSimultor/pkg/automata/model"
)

const size = 16
const iters = 10
const firstIterationFirePosition = 2

func getGoldenSinRes() lattice.SimulationResult {
	simResult := make([][][]cell.CellState, iters)
	for i := range iters {
		simResult[i] = make([][]cell.CellState, size)
		for j := range size {
			simResult[i][j] = make([]cell.CellState, size)
			for k := range size {
				simResult[i][j][k] = cell.WATER
			}
		}
	}

	for i := range size {
		for j := range size {
			if j != size/2 {
				simResult[iters-1][i][j] = cell.FIRE
			}
		}
	}

	simResult[firstIterationFirePosition][0][0] = cell.FIRE
	return simResult
}

// func TestTotalAreaBurned(t *testing.T) {

// 	in := RiverAnalysisInput{
// 		WindDirection:    model.N,
// 		Radius:           1,
// 		SimulationResult: getGoldenSinRes(),
// 	}
// 	var areaExpected float64 = (size * (size / 2)) / (size * (size))

// 	if cmp.Compare(in.totalAreaBurned(), areaExpected) != 0 {
// 		t.Errorf("Expected %f, got %f", areaExpected, in.totalAreaBurned())
// 	}

// }
//
//	func TestAreaWestOfRiver(t *testing.T) {
//		in := RiverAnalysisInput{
//			WindDirection:    model.N,
//			Radius:           1,
//			SimulationResult: getGoldenSinRes(),
//		}
//		if cmp.Compare(in.totalAreaBurnedWestOfTheRiver(), areaExpected) != 0 {
//			t.Errorf("Expected %f, got %f", areaExpected, in.totalAreaBurnedWestOfTheRiver())
//		}
//	}
func TestFirstPositionFireWestOfTheRiver(t *testing.T) {
	in := RiverAnalysisInput{
		WindDirection:    model.N,
		Radius:           1,
		SimulationResult: getGoldenSinRes(),
	}

	out := in.firstFirePosition()
	if out.I <= 0 {
		t.Errorf("Expected iteration to be %d, got %d", 0, out.I)
	}
	if out.J < 0 {
		t.Errorf("Expected height position %d, got %d", 0, out.J)
	}
	if out.K < 0 {
		t.Errorf("Expected width position %d, got %d", 0, out.K)
	}

	if out.I != firstIterationFirePosition {
		t.Errorf("Expected %d, got %d", firstIterationFirePosition, out.I)
	}
}
