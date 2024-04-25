package cell

type Position struct {
	I         uint8
	J         uint8
	Iteration uint8
}
type Cell struct {
	State             CellState
	NextState         CellState
	InitialState      CellState
	IterationsInState uint16
	Altitude          float32
}

func (cell *Cell) Update() {
	if cell.NextState != NONE {
		cell.IterationsInState = 0
		cell.State = cell.NextState
		cell.NextState = NONE
	} else {
		cell.IterationsInState++
	}
}

func (cell *Cell) SetNextState(state CellState) {
	cell.NextState = state
}

func CreateCell(state CellState, i, j uint8, altitude float32) *Cell {
	return &Cell{
		State:             state,
		NextState:         NONE,
		InitialState:      state,
		IterationsInState: 0,
		Altitude:          altitude,
	}
}
