package lattice

import (
	"github.com/heitorfreitasferreira/fireSpreadSimultor/pkg/automata/lattice/cell"
	"github.com/heitorfreitasferreira/fireSpreadSimultor/pkg/automata/model"
	"github.com/heitorfreitasferreira/fireSpreadSimultor/utils"
)

type Lattice struct {
	Height        int
	Width         int
	Humidity      float32
	Iterations    int
	WindDirection model.WindDirection
	Cells         [][]*cell.Cell
	InitialState  cell.CellState
	fireStarters  [][]utils.Vector2D[int]

	modelRunner model.ModelRunner
}

func CreateLattice(p LatticeParams, windParams model.MatrixParams, modelParams model.Parameters) Lattice {
	altitudeMatrix := make([][]float32, p.Height)
	for i := range altitudeMatrix {
		altitudeMatrix[i] = make([]float32, p.Width)
		for j := range altitudeMatrix[i] {
			altitudeMatrix[i][j] = 1
		}
	}

	fires := make([][]utils.Vector2D[int], p.Iterations)
	for i := 0; i < int(p.Iterations); i++ {
		fires[i] = make([]utils.Vector2D[int], 0)
	}

	for _, spot := range p.FireSpots {
		fires[spot.K] = append(fires[spot.K], utils.Vector2D[int]{
			I: spot.I,
			J: spot.J,
		})
	}

	if len(p.InitialState) != int(p.Height) || len(p.InitialState[0]) != int(p.Width) {
		panic("Initial state matrix does not match the lattice dimensions")
	}
	board := createCellMatrix(
		p.Height,
		p.Width,
		p.InitialState,
		altitudeMatrix,
		fires[0])
	return Lattice{
		Height:       p.Height,
		Width:        p.Width,
		Humidity:     p.Humidity,
		Iterations:   p.Iterations,
		fireStarters: fires,
		modelRunner:  model.NewRunner(modelParams, windParams, p.Humidity, &board),
		Cells:        board,
	}
}

func (lattice *Lattice) Run() SimulationResult {
	returnable := make([][][]cell.CellState, lattice.Iterations)

	for i := 0; i < lattice.Iterations; i++ {
		returnable[i] = lattice.runOneIteration(i)
	}

	return returnable
}

func (lattice *Lattice) runOneIteration(iteration int) [][]cell.CellState {
	for _, spot := range lattice.fireStarters[iteration] {
		lattice.Cells[spot.I][spot.J].State = cell.INITIAL_FIRE
		lattice.Cells[spot.I][spot.J].NextState = cell.INITIAL_FIRE
		lattice.Cells[spot.I][spot.J].IterationsInState = 0
	}

	for i := 0; i < lattice.Height; i++ {
		for j := 0; j < lattice.Width; j++ {
			lattice.modelRunner.Step(i, j)
		}
	}
	lattice.updateAllCells()

	lattice.modelRunner.PlantFireSeeds()

	return lattice.getCellsStates()
}

func (lattice *Lattice) updateAllCells() {
	for _, row := range lattice.Cells {
		for _, c := range row {
			c.Update()
		}
	}
}

func (lattice *Lattice) getCellsStates() [][]cell.CellState {
	states := make([][]cell.CellState, lattice.Height)
	cellMatrix := lattice.Cells

	for i := range cellMatrix {
		states[i] = make([]cell.CellState, lattice.Width)
		for j := range cellMatrix[i] {
			states[i][j] = cellMatrix[i][j].State
		}
	}
	return states
}

func createCellMatrix(
	height int,
	width int,
	initialStates [][]cell.CellState,
	altitudeMatrix [][]float32,
	fireSpots []utils.Vector2D[int]) [][]*cell.Cell {
	cells := make([][]*cell.Cell, height)
	for i := range cells {
		cells[i] = make([]*cell.Cell, width)
		for j := range cells[i] {
			cells[i][j] = cell.CreateCell(initialStates[i][j], altitudeMatrix[i][j])
		}
	}

	for _, fireSpot := range fireSpots {
		cells[fireSpot.I][fireSpot.J].State = cell.INITIAL_FIRE
	}

	return cells
}
