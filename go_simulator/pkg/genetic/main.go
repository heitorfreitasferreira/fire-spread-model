package genetic

import (
	"fmt"

	"github.com/heitorfreitasferreira/fireSpreadSimultor/pkg/automata/lattice"
	"github.com/heitorfreitasferreira/fireSpreadSimultor/pkg/automata/model"
)

type evolver struct {
	population Population

	selector       selector
	parentSelector matingPoolSelector
	crossover      CrossoverStrategy
	mutator        MutationStrategy
	newIndividual  func(Cromossome) *Individual
}

func Evolve(geneticParams GeneticAlgorithmParams, latticeParams lattice.LatticeParams, goal lattice.SimulationResult) {

	newIndividual := individualCreator(geneticParams, latticeParams, goal)
	selector := getSelector(geneticParams)
	parentSelector := getMatingPoolSelector(geneticParams)
	crossover := getCrossover(geneticParams)
	mutator := getMutator(geneticParams)
	statistic := newEvolutionData(geneticParams, latticeParams)

	population := make(Population, geneticParams.PopulationSize)
	actualParams := model.Parameters{
		InfluenciaUmidade:                   1,
		ProbEspalhamentoFogoInicial:         0.6,
		ProbEspalhamentoFogoArvoreQueimando: 1,
		ProbEspalhamentoFogoQueimaLenta:     0.2,
		InfluenciaVegetacaoCampestre:        0.6,
		InfluenciaVegetacaoSavanica:         1,
		InfluenciaVegetacaoFlorestal:        0.8,
	}
	cromossome := fromModelParams(actualParams)
	for i := range population {
		population[i] = newIndividual(cromossome)
	}
	evolver := evolver{
		population:     population,
		selector:       selector,
		parentSelector: parentSelector,
		crossover:      crossover,
		mutator:        mutator,
	}
	for i := 0; i < int(geneticParams.Generations); i++ {
		evolver.stepOneGeneration()

		statistic.addData(evolver.population, i)
	}

	fmt.Print(statistic.toString())
	statistic.saveToFile()
}

func (e *evolver) stepOneGeneration() {
	matingPool := e.parentSelector.matingPool(e.population)

	childernCromossome := e.crossover.crossover(matingPool)
	e.mutator.mutate(childernCromossome)

	childern := make(Population, len(childernCromossome))

	for i, cromossome := range childernCromossome {
		childern[i] = e.newIndividual(cromossome)
	}
	e.population = append(e.population, childern...)
	e.selector.killWeak(e.population)

}
