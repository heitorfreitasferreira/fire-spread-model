package model

import (
	"fmt"
	"math/rand"

	"github.com/heitorfreitasferreira/fireSpreadSimultor/pkg/automata/lattice/cell"
	"github.com/heitorfreitasferreira/fireSpreadSimultor/utils"
)

func (r *ModelRunner) PlantFireSeeds() {
	for i, row := range *r.board {
		for j, c := range row {
			if c.State == cell.FIRE {
				r.plantFireSeedFrom(i, j)
			}
		}
	}
}

func (r *ModelRunner) plantFireSeedFrom(i, j int) {
	const fireSeedCreationProbabilityTreshold = 0.0
	if rand.Float64() > fireSeedCreationProbabilityTreshold {
		return
	}

	positions := NLowestPositions(3, r.WindMatrix)
	latticeHeight := len(*r.board)
	latticeWidth := len((*r.board)[0])

	maxDistance := 3
	pos := positions[rand.Intn(len(positions))]

	iRange := rand.Intn(maxDistance)
	iDeltaUnClamped := (pos.I - 1) * iRange
	iDelta := utils.Clamp(iDeltaUnClamped, 0, (latticeHeight - 1))

	jRange := rand.Intn(maxDistance)
	jDeltaUnClamped := (pos.J - 1) * jRange
	jDelta := utils.Clamp(jDeltaUnClamped, 0, (latticeWidth - 1))

	(*r.board)[i+iDelta][j+jDelta].HasFireSeed = true

	fmt.Printf("Planting fire seed at i:%d, j:%d\n", i+iDelta, j+jDelta)
}
