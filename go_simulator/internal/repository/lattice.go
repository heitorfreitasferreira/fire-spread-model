package repository

import (
	"github.com/heitorfreitasferreira/fireSpreadSimultor/pkg/automata/lattice"
	"github.com/heitorfreitasferreira/fireSpreadSimultor/pkg/automata/model"
)

type LatticeRepository interface {
	StoreSimulation(latticeParams lattice.LatticeParams, windParams model.WindParams, modelParams model.Parameters, windMatrix model.WindMatrix, result lattice.SimulationResult) error
}
