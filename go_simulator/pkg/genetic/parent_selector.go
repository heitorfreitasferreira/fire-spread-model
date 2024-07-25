package genetic

import (
	"math/rand"
	"sort"
)

type MatingPoolSelectionType string

const (
	ElitismMatingPoolSelection                      MatingPoolSelectionType = "standard"
	TournamentMatingPoolSelectionWithoutRepositiong MatingPoolSelectionType = "tournament"
	RouletteMatingPoolSelection                     MatingPoolSelectionType = "roulette"
)

type matingPoolSelector interface {
	matingPool(Population) Population // Pais que irão cruzar na prox geração
}

func getMatingPoolSelector(geneticParams GeneticAlgorithmParams) matingPoolSelector {
	switch geneticParams.ParentSelection {
	default:
		return ElitismParentSelector{
			matingPoolSize: int(geneticParams.PopulationSize / 10), // 10% dos melhores
		}
	}
}

type ElitismParentSelector struct {
	matingPoolSize int
}

func (s ElitismParentSelector) matingPool(population Population) Population {
	sort.Sort(population)
	return population[:s.matingPoolSize]
}

type TournamentMatingPoolSelectorWithoutReposition struct {
	k              uint16
	matingPoolSize int
}

func (s TournamentMatingPoolSelectorWithoutReposition) matingPool(
	population Population) Population {
	selected := make(Population, s.matingPoolSize)
	avaliablesIndexes := make([]int, len(population)) // 0 (default) is avaliable
	for i := range selected {
		selectedIdx := tournamentSelection(&population, s.k)
		for avaliablesIndexes[selectedIdx] != 0 { // Sem reposição
			selectedIdx = tournamentSelection(&population, s.k)
		}
		selected[i] = population[selectedIdx]
		avaliablesIndexes[selectedIdx] = 1
	}
	return selected
}

type RouletteMatingPoolSelector struct {
	matingPoolSize int
}

func (s RouletteMatingPoolSelector) matingPool(population Population) Population {
	selected := make(Population, s.matingPoolSize)
	roulette := make([]float64, len(population))
	roulette[0] = population[0].Fenotype
	for i := 1; i < len(population); i++ {
		roulette[i] = roulette[i-1] + population[i].Fenotype
	}
	for i := 0; i < int(s.matingPoolSize); i++ {
		r := rand.Float64() * roulette[len(roulette)-1]
		for j := 0; j < len(roulette); j++ {
			if r < roulette[j] {
				selected[i] = population[j]
				break
			}
		}
	}
	return selected
}
