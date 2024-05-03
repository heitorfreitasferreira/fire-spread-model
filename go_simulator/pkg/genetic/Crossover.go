package genetic

import "math/rand"

type CrossoverType string

const (
	OnePointCrossover CrossoverType = "one-point"
	MeanCrossover     CrossoverType = "mean"
	Arithmetic        CrossoverType = "arithmetic"
	BlxAlpha          CrossoverType = "blx-alpha"
)

type CrossoverStrategy interface {
	crossover(Population) []Cromossome
}

func getCrossover(geneticParams GeneticAlgorithmParams) CrossoverStrategy {
	switch geneticParams.Crossover {
	case MeanCrossover:
		return meanCrossover{}
	case Arithmetic:
		return arithmeticCrossover{}
	default:
		return onePointCrossover{
			crossOverRate: geneticParams.CrossOverRate,
		}
	}
}

type onePointCrossover struct {
	crossOverRate float64
}

func (c onePointCrossover) crossover(population Population) []Cromossome {
	var newPopulation []Cromossome = make([]Cromossome, len(population))
	cromossomeLen := len((*population[0]).Genotype)

	for i := 0; i < len(population); i += 2 {
		parent1 := population[i]
		parent2 := population[i+1]

		if rand.Float64() < c.crossOverRate { // Irá cruzar
			crossoverPoint := rand.Intn(cromossomeLen)

			child1 := make(Cromossome, cromossomeLen)
			child2 := make(Cromossome, cromossomeLen)

			for j := 0; j < crossoverPoint; j++ {
				child1[j] = parent1.Genotype[j]
				child2[j] = parent2.Genotype[j]
			}
			for j := crossoverPoint; j < cromossomeLen; j++ {
				child1[j] = parent2.Genotype[j]
				child2[j] = parent1.Genotype[j]
			}
			newPopulation[i] = child1
			newPopulation[i+1] = child2
		} else {
			newPopulation[i] = parent1.Genotype
			newPopulation[i+1] = parent2.Genotype
		}
	}
	return newPopulation
}

type meanCrossover struct {
}

func (c meanCrossover) crossover(population Population) []Cromossome {

	var newPopulation []Cromossome
	cromossomeLen := len((*population[0]).Genotype)

	for i := 0; i < len(population)-1; i += 2 {
		parent1 := population[i]
		parent2 := population[i+1]

		child := make(Cromossome, cromossomeLen)

		for j := 0; j < cromossomeLen; j++ {
			child[j] = (parent1.Genotype[j] + parent2.Genotype[j]) / 2
		}
		newPopulation = append(newPopulation, child)
	}
	return newPopulation
}

type arithmeticCrossover struct{}

func (c arithmeticCrossover) crossover(population Population) []Cromossome {

	var newPopulation []Cromossome = make([]Cromossome, int(len(population)/2))
	cromossomeLen := len((*population[0]).Genotype)

	for i := 0; i < len(population); i += 2 {
		parent1 := population[i]
		parent2 := population[i+1]

		child1 := make(Cromossome, cromossomeLen)
		child2 := make(Cromossome, cromossomeLen)
		r := rand.Float64()
		for j := 0; j < cromossomeLen; j++ {
			child1[j] = r*parent1.Genotype[j] + (1-r)*parent2.Genotype[j]
			child2[j] = r*parent2.Genotype[j] + (1-r)*parent1.Genotype[j]
		}
		newPopulation[i] = child1

	}
	return newPopulation
}

type blxAlphaCrossover struct {
	alpha float64
}
