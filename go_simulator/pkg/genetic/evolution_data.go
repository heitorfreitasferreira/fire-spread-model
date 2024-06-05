package genetic

import (
	"fmt"
	"os"
	"strings"

	"github.com/heitorfreitasferreira/fireSpreadSimultor/pkg/automata/lattice"
)

type EvolutionData struct {
	bestFitness       []float64
	averageFitness    []float64
	worstFitness      []float64
	standardDeviation []float64
	parametesOfBest   []Cromossome
	parametersOfWorst []Cromossome
	GeneticParams     GeneticAlgorithmParams
	LatticeParams     lattice.LatticeParams
}

func newEvolutionData(geneticParams GeneticAlgorithmParams, latticeParams lattice.LatticeParams) *EvolutionData {
	return &EvolutionData{
		bestFitness:       make([]float64, geneticParams.Generations),
		averageFitness:    make([]float64, geneticParams.Generations),
		worstFitness:      make([]float64, geneticParams.Generations),
		standardDeviation: make([]float64, geneticParams.Generations),
		parametesOfBest:   make([]Cromossome, geneticParams.Generations),
		parametersOfWorst: make([]Cromossome, geneticParams.Generations),
		GeneticParams:     geneticParams,
		LatticeParams:     latticeParams,
	}
}

func (e *EvolutionData) addData(pop Population, generation int) {
	best, average, worst, standardDeviation, parametersOfBest, parametersOfWorst := pop.getStatistics()

	e.bestFitness[generation] = best
	e.averageFitness[generation] = average
	e.worstFitness[generation] = worst
	e.standardDeviation[generation] = standardDeviation
	e.parametesOfBest[generation] = parametersOfBest
	e.parametersOfWorst[generation] = parametersOfWorst
}

func (e *EvolutionData) toString() string {
	var builder strings.Builder
	builder.WriteString("# " + e.GeneticParams.ToString() + "\n")
	builder.WriteString("# " + e.LatticeParams.ToString() + "\n")
	builder.WriteString("Best Fitness,Average Fitness,Worst Fitness,Standard Deviation,Best Parameters\n")
	for i := 0; i < len(e.bestFitness); i++ {
		builder.WriteString(fmt.Sprintf("%f,%f,%f,%f,%s\n",
			e.bestFitness[i],
			e.averageFitness[i],
			e.worstFitness[i],
			e.standardDeviation[i],
			e.parametesOfBest[i].ToString("; ")))
	}

	return builder.String()
}

func (e *EvolutionData) saveToFile() {
	file, err := os.Create("evolution_data.csv")
	if err != nil {
		fmt.Println("Error creating file:", err)
		return
	}
	defer file.Close()

	data := e.toString()
	_, err = file.WriteString(data)
	if err != nil {
		fmt.Println("Error writing to file:", err)
		return
	}

	fmt.Println("Evolution data saved to evolution_data.csv")
}
