package genetic

import "math/rand"

type SelectionType string

const (
	TournamentSelection SelectionType = "tournament"
	RouletteSelection   SelectionType = "roulette"
)

type selector interface {
	killWeak(*Population)
}

func getSelector(geneticParams GeneticAlgorithmParams) selector {
	switch geneticParams.Selection {
	case RouletteSelection:
		return RouletteSelector{
			PopulationSize: geneticParams.PopulationSize,
		}
	default:
		return TournamentSelector{
			K:              geneticParams.TournamentSize,
			PopulationSize: geneticParams.PopulationSize,
		}
	}
}

type TournamentSelector struct {
	K              uint16
	PopulationSize uint16
}

func (ts TournamentSelector) killWeak(population *Population) {

	selected := make(Population, ts.PopulationSize)
	avaliablesIndexes := make([]int, len(*population)) // 0 (default) is avaliable
	for i := range selected {
		selectedIdx := tournamentSelection(population, ts.K)
		for avaliablesIndexes[selectedIdx] != 0 { // Sem reposição
			selectedIdx = tournamentSelection(population, ts.K)
		}
		selected[i] = (*population)[tournamentSelection(population, ts.K)]
		avaliablesIndexes[selectedIdx] = 1
	}
	*population = selected
}

func tournamentSelection(population *Population, k uint16) int {
	best := -1

	selectedIndexes := make([]int, k)
	for i := uint16(0); i < k; i++ {
		selectedIndexes[i] = rand.Intn(len(*population))
	}
	for _, idx := range selectedIndexes {
		if best == -1 || (*population)[idx].Fenotype > (*population)[best].Fenotype {
			best = idx
		}
	}

	return best
}

type RouletteSelector struct {
	PopulationSize uint16
}

func (selector RouletteSelector) killWeak(population *Population) {
	// TODO: ver com o Didiney se esse gepetéco está certo

	sourviors := make(Population, selector.PopulationSize)
	roulette := make([]float64, len(*population))
	roulette[0] = (*population)[0].Fenotype
	for i := 1; i < len(*population); i++ {
		roulette[i] = roulette[i-1] + (*population)[i].Fenotype
	}
	for i := 0; i < int(selector.PopulationSize); i++ {
		r := rand.Float64() * roulette[len(roulette)-1]
		for j := 0; j < len(roulette); j++ {
			if r < roulette[j] {
				sourviors[i] = (*population)[j]
				break
			}
		}
	}
	*population = sourviors
}
