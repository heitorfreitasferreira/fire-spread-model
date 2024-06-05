package genetic

import "math/rand"

type MutationType string

const (
	StandardMutation MutationType = "standard"
	UniformMutation  MutationType = "uniform"
	CreepMutation    MutationType = "creep" // gene +=  rand(-mutationRate, mutationRate)
)

type MutationStrategy interface {
	mutate([]Cromossome)
}

func getMutator(geneticParams GeneticAlgorithmParams) MutationStrategy {
	switch geneticParams.MutationType {
	default:
		return CreepMutator{
			mutationProb: geneticParams.MutationProb,
			mutationRate: geneticParams.MutationRate,
		}
	}
}

type CreepMutator struct {
	mutationRate float64
	mutationProb float64
}

func (m CreepMutator) mutate(population []Cromossome) {
	for _, cromossome := range population {
		if rand.Float64() < m.mutationProb {
			rndAlelIdx := rand.Intn(len(cromossome))
			mutationFactor := (rand.Float64() * 2 * m.mutationRate) - m.mutationRate
			cromossome[rndAlelIdx] += mutationFactor
		}
	}
}

type UniformMutator struct {
	mutationRate float64
}

func (m UniformMutator) mutate(population []Cromossome) {
	for _, cromossome := range population {
		for i := range cromossome {
			if rand.Float64() < m.mutationRate {
				cromossome[i] = rand.Float64()
			}
		}
	}
}
