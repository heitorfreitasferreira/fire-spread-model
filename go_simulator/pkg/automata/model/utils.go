package model

import "github.com/heitorfreitasferreira/fireSpreadSimultor/pkg/automata/lattice/cell"

func calculateHumidityInfluence(humidity float32) float64 {
	switch {
	case humidity > 0.0 && humidity <= 0.25:
		return 1.5
	case humidity > 0.25 && humidity <= 0.5:
		return 1.0
	case humidity > 0.5 && humidity <= 0.75:
		return 0.8
	case humidity > 0.75 && humidity <= 1.0:
		return 0.6
	default:
		return 0
	}
}
func (r *ModelRunner) getStateByRelativePosition(currI, deltaI, currJ, deltaJ int) cell.CellState {
	i := currI + deltaI
	j := currJ + deltaJ
	if i < 0 || i >= len(*r.board) || j < 0 || j >= len((*r.board)[0]) {
		return cell.WATER
	}
	return (*r.board)[i][j].State
}

func positionsToLook(radius int) [][]int {
	positions := make([][]int, 0)
	for i := -radius; i <= radius; i++ {
		for j := -radius; j <= radius; j++ {
			if i == 0 && j == 0 {
				continue
			}
			positions = append(positions, []int{i, j})
		}
	}
	return positions
}
