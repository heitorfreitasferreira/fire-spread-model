package main

import (
	"fmt"
	"time"

	"github.com/heitorfreitasferreira/fireSpreadSimultor/pkg/automata/lattice"
	"github.com/heitorfreitasferreira/fireSpreadSimultor/pkg/automata/lattice/cell"
	"github.com/heitorfreitasferreira/fireSpreadSimultor/pkg/automata/model"
	"github.com/heitorfreitasferreira/fireSpreadSimultor/pkg/genetic"
	"github.com/heitorfreitasferreira/fireSpreadSimultor/utils"
)

func main() {

	var latticeParams = lattice.LatticeParams{
		Height:        64,
		Width:         64,
		Humidity:      0.5,
		Iterations:    50,
		WindDirection: model.N,
		InitialState:  cell.SAVANNAH,
		FireSpots:     []utils.Vector2D{{I: 32, J: 32}},
	}

	var modelParams model.Parameters = model.Parameters{
		InfluenciaUmidade:                   1,
		ProbEspalhamentoFogoInicial:         0.6,
		ProbEspalhamentoFogoArvoreQueimando: 1,
		ProbEspalhamentoFogoQueimaLenta:     0.2,
		InfluenciaVegetacaoCampestre:        0.6,
		InfluenciaVegetacaoSavanica:         1,
		InfluenciaVegetacaoFlorestal:        0.8,
	}

	var geneticParams genetic.GeneticAlgorithmParams = genetic.GeneticAlgorithmParams{
		Generations:           100,
		PopulationSize:        10,
		TournamentSize:        2,
		SimulationsPerFitness: 10,
		MutationRate:          0.1,
		MutationProb:          0.1,
		CrossOverRate:         0.8,
		ReverseElitismRate:    0.1,
		CrossoverBlxAlpha:     0.5,
		Selection:             genetic.TournamentSelection,
		ParentSelection:       genetic.ElitismMatingPoolSelection,
		Crossover:             genetic.MeanCrossover,
		MutationType:          genetic.CreepMutation,
	}

	startTime := time.Now()
	var goal lattice.SimulationResult = lattice.CreateAndRunLatticesParallel(latticeParams, modelParams, 1)[0]
	genetic.Evolve(geneticParams, latticeParams, goal)
	fmt.Println("Tempo total: ", time.Since(startTime))

}
