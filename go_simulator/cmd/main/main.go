package main

import (
	"fmt"
	"math/rand"
	"sync"
	"time"

	"github.com/heitorfreitasferreira/fireSpreadSimultor/cmd/args"
	"github.com/heitorfreitasferreira/fireSpreadSimultor/pkg/automata/lattice"
	"github.com/heitorfreitasferreira/fireSpreadSimultor/pkg/automata/model"
)

func main() {
	startTime := time.Now()
	a := args.Pedrao()
	var wg sync.WaitGroup

	for i := 0; i < 10; i++ {

		wg.Add(1)
		go func(a args.Args) {

			rand.Seed(0)

			l := lattice.CreateLattice(a.LatticeParams)

			l.Run(model.Parameters{
				InfluenciaUmidade:                   1,
				ProbEspalhamentoFogoInicial:         0.6,
				ProbEspalhamentoFogoArvoreQueimando: 1,
				ProbEspalhamentoFogoQueimaLenta:     0.2,
				InfluenciaVegetacaoCampestre:        0.6,
				InfluenciaVegetacaoSavanica:         1,
				InfluenciaVegetacaoFlorestal:        0.8,
			})
			wg.Done()
		}(a)
	}
	wg.Wait()
	fmt.Println("Tempo total: ", time.Since(startTime))
}

/*
func NewArgs() *Args {
	mode := flag.String("mode", string(automata), "run mode")
	height := flag.Int64("height", 32, "lattice height")
	width := flag.Int64("width", 32, "lattice width")
	humidity := flag.Float64("humidity", 0.5, "humidity")
	iterations := flag.Int64("iterations", 100, "iterations")
	windDirection := flag.String("windDirection", "N", "wind direction")
	initialState := flag.Int64("initialState", int64(cell.SAVANNAH), "initial state")
	seed := flag.Int64("seed", 0, "seed")
	inputFile := flag.String("inputFile", "", "input file")
	outputFile := flag.String("outputFile", "", "output file")
	Generations := flag.Int64("Generations", 50, "generations")
	PopulationSize := flag.Int64("PopulationSize", 100, "population size")
	TournamentSize := flag.Int64("TournamentSize", 2, "tournament size")
	MutationRate := flag.Float64("MutationRate", 0.1, "mutation rate")
	MutationProb := flag.Float64("MutationProb", 0.1, "mutation probability")
	CrossOverRate := flag.Float64("CrossOverRate", 0.1, "crossover rate")
	ElitismRate := flag.Float64("ElitismRate", 0.1, "elitism rate")
	ReverseElitismRate := flag.Float64("ReverseElitismRate", 0.1, "reverse elitism rate")
	CrossoverBlxAlpha := flag.Float64("CrossoverBlxAlpha", 0.01, "crossover blx alpha")
	Reproduction := flag.String("Reproduction", "Sexual", "reproduction")
	Selection := flag.String("Selection", "RandomSelector", "selection")
	Crossover := flag.String("Crossover", "Blx", "crossover")
	SimulationsPerFitness := flag.Int64("SimulationsPerFitness", 10, "simulations per fitness")

	flag.Parse()

	return &Args{
		geneticParams: genetic.GeneticAlgorithmParams{
			Generations:           *Generations,
			PopulationSize:        *PopulationSize,
			TournamentSize:        *TournamentSize,
			MutationRate:          *MutationRate,
			MutationProb:          *MutationProb,
			CrossOverRate:         *CrossOverRate,
			ElitismRate:           *ElitismRate,
			ReverseElitismRate:    *ReverseElitismRate,
			CrossoverBlxAlpha:     *CrossoverBlxAlpha,
			Reproduction:          genetic.Reproductor(*Reproduction),
			Selection:             genetic.Selection(*Selection),
			Crossover:             genetic.Crossover(*Crossover),
			SimulationsPerFitness: *SimulationsPerFitness,
		},
		latticeParams: lattice.CreateLattice(
			uint8(*height),
			uint8(*width),
			[]lattice.Vector2D{{I: uint8(*height) / 2, J: uint8(*width) / 2}},
			*humidity,
			uint16(*iterations),
			model.WindDirection(*windDirection),
			initialStateMatrix(uint8(*height), uint8(*width), cell.CellState(*initialState)),
			mocAltitudeMatrix(uint8(*height), uint8(*width)),
		),
		mode:       RunMode(*mode),
		seed:       *seed,
		inputFile:  *inputFile,
		outputFile: *outputFile,
	}
}
*/
