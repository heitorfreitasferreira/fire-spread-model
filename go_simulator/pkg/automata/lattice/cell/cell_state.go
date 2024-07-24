package cell

import "encoding/json"

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

// Método MarshalJSON para CellState
func (c CellState) MarshalJSON() ([]byte, error) {
	return json.Marshal(uint8(c))
}

// Método UnmarshalJSON para CellState (para referência futura)
func (c *CellState) UnmarshalJSON(data []byte) error {
	var value uint8
	if err := json.Unmarshal(data, &value); err != nil {
		return err
	}
	*c = CellState(value)
	return nil
}
