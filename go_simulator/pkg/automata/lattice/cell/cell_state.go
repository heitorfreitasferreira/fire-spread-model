package cell

type CellState uint8

const (
	ASH CellState = iota
	INITIAL_FIRE
	FIRE
	EMBER
	MEADOW
	SAVANNAH
	FOREST
	WATER
	ROOTS
	NONE
)

func (state *CellState) IsBurnable() bool {
	return *state == MEADOW || *state == SAVANNAH || *state == FOREST || *state == ROOTS
}

func (state *CellState) IsFire() bool {
	return *state == INITIAL_FIRE || *state == FIRE || *state == EMBER
}

func FireStates() []CellState {
	return []CellState{INITIAL_FIRE, FIRE, EMBER}
}

func BurnableStates() []CellState {
	return []CellState{MEADOW, SAVANNAH, FOREST, ROOTS}
}

func AllStates() []CellState {
	return []CellState{ASH, INITIAL_FIRE, FIRE, EMBER, MEADOW, SAVANNAH, FOREST, WATER, ROOTS}
}
