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

func (state *CellState) GetName() string {
	switch *state {
	case ASH:
		return "ash"
	case INITIAL_FIRE:
		return "initial-fire"
	case FIRE:
		return "fire"
	case EMBER:
		return "ember"
	case MEADOW:
		return "meadow"
	case SAVANNAH:
		return "savannah"
	case FOREST:
		return "forest"
	case WATER:
		return "water"
	case ROOTS:
		return "roots"
	default:
		return ""
	}
}

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
