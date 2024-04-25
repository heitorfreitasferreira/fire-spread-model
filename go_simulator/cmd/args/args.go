package args

import (
	"flag"
	"fmt"
	"log"
	"os"

	"github.com/heitorfreitasferreira/fireSpreadSimultor/pkg/automata/lattice"
	"github.com/heitorfreitasferreira/fireSpreadSimultor/pkg/automata/lattice/cell"
	"github.com/heitorfreitasferreira/fireSpreadSimultor/pkg/automata/model"
	"github.com/heitorfreitasferreira/fireSpreadSimultor/pkg/genetic"
)

type RunMode string

const (
	automata RunMode = "automata"
	evolve   RunMode = "genetic"
)

type Args struct {
	GeneticParams genetic.GeneticAlgorithmParams
	LatticeParams lattice.LatticeParams
	Mode          RunMode
	Seed          int64
	InputFile     string
	OutputFile    string
}

var (
	Mode string

	Height, Width, FireSpots, Iterations, InitialState, Seed int
	Humidity                                                 float64
	WindDirection                                            string

	//gen
	InputFile, OutputFile                                                                         string
	Generations, PopulationSize, SimulationsPerFitness, TournamentSize                            int
	MutationRate, MutationProb, CrossOverRate, ElitismRate, ReverseElitismRate, CrossoverBlxAlpha float64
	Reproduction, Selection, Crossover                                                            string

	automataCmd = flag.NewFlagSet("automata", flag.ExitOnError)
	genCmd      = flag.NewFlagSet("gen", flag.ExitOnError)
)

func setupCommonFlags() {
	for _, fs := range []*flag.FlagSet{automataCmd, genCmd} {
		fs.IntVar(&Height, "height", 32, "")
		fs.IntVar(&Width, "width", 32, "")
		fs.IntVar(&FireSpots, "fireSpots", 32, "")
		// FireSpots := []lattice.Vector2D{{I: height / 2, J: width / 2}}
		fs.Float64Var(&Humidity, "humidity", 0.5, "")
		fs.IntVar(&Iterations, "iterarions", 100, "")
		fs.StringVar(&WindDirection, "windDirection", "north", "")
		fs.IntVar(&InitialState, "initialState", 1, "")
		fs.IntVar(&Seed, "seed", 0, "")
		fs.StringVar(&InputFile, "inputFile", "oi", "")
		fs.StringVar(&OutputFile, "outputFile", "tch", "")
		fs.IntVar(&Generations, "generations", 50, "")
		fs.IntVar(&PopulationSize, "populationSize", 100, "")
		fs.IntVar(&TournamentSize, "tournamentSize", 2, "")
		fs.Float64Var(&MutationRate, "mutationRate", 0.1, "")
		fs.Float64Var(&MutationProb, "mutationProb", 0.1, "")
		fs.Float64Var(&CrossOverRate, "crossOverRate", 0.1, "")
		fs.Float64Var(&ElitismRate, "elistismRate", 0.1, "")
		fs.Float64Var(&ReverseElitismRate, "reverseElistismRate", 0.1, "")
		fs.Float64Var(&CrossoverBlxAlpha, "crossoverBlxAlpha", 0.01, "")
		fs.StringVar(&Reproduction, "reproduction", "r", "")
		fs.StringVar(&Selection, "selection", "s", "")
		fs.StringVar(&Crossover, "crossover", "c", "")
		fs.IntVar(&SimulationsPerFitness, "simulationsPerFitness", 10, "")
	}
}

func InitialStateMatrix(height, width uint8, initialState cell.CellState) [][]cell.CellState {
	initialStates := make([][]cell.CellState, height)
	for i := range initialStates {
		initialStates[i] = make([]cell.CellState, width)
		for j := range initialStates[i] {
			initialStates[i][j] = initialState
		}
	}
	return initialStates
}

func MocAltitudeMatrix(height, width uint8) [][]float32 {
	altitudeMatrix := make([][]float32, height)
	for i := range altitudeMatrix {
		altitudeMatrix[i] = make([]float32, width)
		for j := range altitudeMatrix[i] {
			altitudeMatrix[i][j] = 1
		}
	}
	return altitudeMatrix
}

func Pedrao() Args {
	setupCommonFlags()

	switch os.Args[1] {
	case "automata":
		automataCmd.Parse(os.Args[2:])
		fmt.Println("automata")
	case "gen":
		genCmd.Parse(os.Args[2:])
		fmt.Println("gen")
	default:
		log.Fatalf("[ERROR] unknown subcommand '%s', see help for more details.", os.Args[1])
	}

	return Args{
		Mode:       RunMode(Mode),
		Seed:       int64(Seed),
		InputFile:  InputFile,
		OutputFile: OutputFile,
		GeneticParams: genetic.GeneticAlgorithmParams{
			Generations:           int64(Generations),
			PopulationSize:        int64(PopulationSize),
			TournamentSize:        int64(TournamentSize),
			SimulationsPerFitness: int64(SimulationsPerFitness),
			MutationRate:          MutationRate,
			MutationProb:          MutationProb,
			CrossOverRate:         CrossOverRate,
			ElitismRate:           ElitismRate,
			ReverseElitismRate:    ReverseElitismRate,
			CrossoverBlxAlpha:     CrossoverBlxAlpha,
			Reproduction:          genetic.Reproductor(Reproduction),
			Selection:             genetic.Selection(Selection),
			Crossover:             genetic.Crossover(Crossover),
		},
		LatticeParams: lattice.LatticeParams{
			Height:        uint8(Height),
			Width:         uint8(Width),
			Humidity:      Humidity,
			Iterations:    uint16(Iterations),
			WindDirection: model.WindDirection(WindDirection),
			InitialState:  cell.CellState(InitialState),
			FireSpots:     []lattice.Vector2D{{I: uint8(Height) / 2, J: uint8(Width) / 2}},
		},
	}
}
