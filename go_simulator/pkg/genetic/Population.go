package genetic

import (
	"github.com/heitorfreitasferreira/fireSpreadSimultor/pkg/automata/lattice"
	"github.com/heitorfreitasferreira/fireSpreadSimultor/pkg/automata/model"
)

type Individual struct {
	Genotype Cromossome
	Fenotype float64
}

func individualCreator(
	params GeneticAlgorithmParams,
	latticeParams lattice.LatticeParams,
	goal lattice.SimulationResult,
) func(cromossome Cromossome) *Individual {

	fitnessCalculator := GetFitnessCalculator(goal)

	return func(cromossome Cromossome) *Individual {
		err, modelParam := cromossome.toModelParams()
		if err != nil || !cromossome.isValid() {
			return &Individual{
				Genotype: cromossome,
				Fenotype: 0,
			}
		}
		results := lattice.CreateAndRunLatticesParallel(latticeParams, model.Parameters(modelParam), params.SimulationsPerFitness)
		fenotype := fitnessCalculator(&results)
		return &Individual{
			Genotype: cromossome,
			Fenotype: fenotype,
		}
	}
}

type Population []*Individual

func (a Population) Len() int           { return len(a) }
func (a Population) Swap(i, j int)      { a[i], a[j] = a[j], a[i] }
func (a Population) Less(i, j int) bool { return a[i].Fenotype < a[j].Fenotype }

func (pop *Population) getStatistics() (
	best float64,
	average float64,
	worst float64,
	standardDeviation float64,
	parametersOfBest Cromossome,
	parametersOfWorst Cromossome) {

	best = -1
	worst = 2
	average = 0
	cromossomeLen := len((*pop)[0].Genotype)
	parametersOfBest = make(Cromossome, cromossomeLen)
	parametersOfWorst = make(Cromossome, cromossomeLen)
	for _, individual := range *pop {
		average += individual.Fenotype
		if individual.Fenotype > best {
			best = individual.Fenotype
			parametersOfBest = individual.Genotype
		}
		if individual.Fenotype < worst {
			worst = individual.Fenotype
			parametersOfWorst = individual.Genotype
		}
	}
	average /= float64(len(*pop))
	for _, individual := range *pop {
		standardDeviation += (individual.Fenotype - average) * (individual.Fenotype - average)
	}
	standardDeviation /= float64(len(*pop))

	return
}
