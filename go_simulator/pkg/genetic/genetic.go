package genetic

import "fmt"

type GeneticAlgorithmParams struct {
	Generations           uint16
	PopulationSize        uint16
	TournamentSize        uint16
	SimulationsPerFitness uint16
	MutationRate          float64
	MutationProb          float64
	CrossOverRate         float64
	ReverseElitismRate    float64
	CrossoverBlxAlpha     float64
	Selection             SelectionType
	ParentSelection       MatingPoolSelectionType
	Crossover             CrossoverType
	MutationType          MutationType
}

func NewGeneticAlggorithmParams(
	generations,
	populationSize,
	tournamentSize,
	simulationsPerFitness uint16,
	mutationRate,
	mutationProb,
	crossOverRate,
	reverseElitismRate,
	crossoverBlxAlpha float64,
	selection SelectionType,
	parentSelection MatingPoolSelectionType,
	crossover CrossoverType,
	mutationType MutationType,
) GeneticAlgorithmParams {
	return GeneticAlgorithmParams{
		Generations:           generations,
		PopulationSize:        populationSize,
		TournamentSize:        tournamentSize,
		SimulationsPerFitness: simulationsPerFitness,
		MutationRate:          mutationRate,
		MutationProb:          mutationProb,
		CrossOverRate:         crossOverRate,
		ReverseElitismRate:    reverseElitismRate,
		CrossoverBlxAlpha:     crossoverBlxAlpha,
		Selection:             selection,
		ParentSelection:       parentSelection,
		Crossover:             crossover,
		MutationType:          mutationType,
	}
}

func (g GeneticAlgorithmParams) ToString() string {
	var shape string = "GeneticAlgorithmParams = {Generations: %d, PopulationSize: %d, TournamentSize: %d, SimulationsPerFitness: %d, MutationRate: %f, MutationProb: %f, CrossOverRate: %f, ReverseElitismRate: %f, CrossoverBlxAlpha: %f, Selection: %s, ParentSelection: %s, Crossover: %s, MutationType: %s}"
	return fmt.Sprintf(shape,
		g.Generations,
		g.PopulationSize,
		g.TournamentSize,
		g.SimulationsPerFitness,
		g.MutationRate,
		g.MutationProb,
		g.CrossOverRate,
		g.ReverseElitismRate,
		g.CrossoverBlxAlpha,
		g.Selection,
		g.ParentSelection,
		g.Crossover,
		g.MutationType)
}
