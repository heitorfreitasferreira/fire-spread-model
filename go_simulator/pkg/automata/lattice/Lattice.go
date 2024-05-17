package lattice

import (
	"github.com/heitorfreitasferreira/fireSpreadSimultor/pkg/automata/lattice/cell"
	"github.com/heitorfreitasferreira/fireSpreadSimultor/pkg/automata/model"
	"github.com/heitorfreitasferreira/fireSpreadSimultor/utils"
)

type Lattice struct {
	Height        uint8
	Width         uint8
	Humidity      float32
	Iterations    uint16
	WindDirection model.WindDirection
	Cells         [][]*cell.Cell
	InitialState  cell.CellState
	fireStarters  [][]utils.Vector2D

	modelRunner model.ModelRunner
}

var watterCell = cell.CreateCell(cell.WATER, 0, 0, 0)

func CreateLattice(p LatticeParams, windParams model.MatrixParams, modelParams model.Parameters) Lattice {

	altitudeMatrix := make([][]float32, p.Height)
	for i := range altitudeMatrix {
		altitudeMatrix[i] = make([]float32, p.Width)
		for j := range altitudeMatrix[i] {
			altitudeMatrix[i][j] = 1
		}
	}

	initialStates := make([][]cell.CellState, p.Height)
	for i := range initialStates {
		initialStates[i] = make([]cell.CellState, p.Width)
		for j := range initialStates[i] {
			initialStates[i][j] = p.InitialState
		}
	}

	fires := make([][]utils.Vector2D, p.Iterations)
	for i := 0; i < int(p.Iterations); i++ {
		fires[i] = make([]utils.Vector2D, 0)
	}
	for _, spot := range p.FireSpots {
		fires[spot.K] = append(fires[spot.K], utils.Vector2D{
			I: spot.I,
			J: spot.J,
		})
	}

	return Lattice{
		Height:       p.Height,
		Width:        p.Width,
		Humidity:     p.Humidity,
		Iterations:   p.Iterations,
		fireStarters: fires,
		modelRunner:  model.NewRunner(modelParams, windParams, p.Humidity),
		Cells: createCellMatrix(
			p.Height,
			p.Width,
			initialStates,
			altitudeMatrix,
			fires[0]),
	}
}

func (lattice *Lattice) Run() SimulationResult {
	returnable := make([][][]cell.CellState, lattice.Iterations)

	// windMatrix := model.MatrixParams{
	// 	Direction: lattice.WindDirection,
	// 	Coef:      1,
	// 	MultBase:  0.16,
	// 	Decai:     0.03,
	// }.CreateMatrix()

	for i := uint16(0); i < lattice.Iterations; i++ {
		returnable[i] = lattice.runOneIteration(i)
	}
	return returnable
}

func (lattice *Lattice) runOneIteration(iteration uint16) [][]cell.CellState {
	for _, spot := range lattice.fireStarters[iteration] {
		lattice.Cells[spot.I][spot.J].State = cell.INITIAL_FIRE
		lattice.Cells[spot.I][spot.J].NextState = cell.INITIAL_FIRE
		lattice.Cells[spot.I][spot.J].IterationsInState = 0
	}

	for i := int16(0); i < int16(lattice.Height); i++ {
		for j := int16(0); j < int16(lattice.Width); j++ {

			neighborhood := lattice.determineNeighborhood(i, j)
			lattice.modelRunner.Step(neighborhood)
		}
	}
	lattice.updateAllCells()
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

func (lattice *Lattice) determineNeighborhood(i, j int16) [][]*cell.Cell {
	neighborhoodSize := 3
	halfSize := int16(neighborhoodSize / 2)
	neighborhood := make([][]*cell.Cell, neighborhoodSize)
	for k := range neighborhood {
		neighborhood[k] = make([]*cell.Cell, neighborhoodSize)
	}
	for di := -halfSize; di <= halfSize; di++ {
		for dj := -halfSize; dj <= halfSize; dj++ {
			neighborI := i + di
			neighborJ := j + dj

			if neighborI < 0 || neighborI >= int16(lattice.Height) || neighborJ < 0 || neighborJ >= int16(lattice.Width) {
				neighborhood[di+halfSize][dj+halfSize] = watterCell
				continue
			}
			neighborhood[di+halfSize][dj+halfSize] = lattice.Cells[neighborI][neighborJ]
		}
	}
	return neighborhood
}

func createCellMatrix(
	height uint8,
	width uint8,
	initialStates [][]cell.CellState,
	altitudeMatrix [][]float32,
	fireSpots []utils.Vector2D) [][]*cell.Cell {
	cells := make([][]*cell.Cell, height)
	for i := range cells {
		cells[i] = make([]*cell.Cell, width)
		for j := range cells[i] {
			cells[i][j] = cell.CreateCell(initialStates[i][j], uint8(i), uint8(j), altitudeMatrix[i][j])
		}
	}

	for _, fireSpot := range fireSpots {
		cells[fireSpot.I][fireSpot.J].State = cell.INITIAL_FIRE
	}

	return cells
}
