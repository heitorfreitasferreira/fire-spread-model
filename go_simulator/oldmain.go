package main

import (
	"encoding/json"
	"flag"
	"fmt"
	"log"
	"math/rand"
	"os"
	"time"

	"github.com/heitorfreitasferreira/fireSpreadSimultor/internal/loggers"
	"github.com/heitorfreitasferreira/fireSpreadSimultor/pkg/automata/lattice"
	"github.com/heitorfreitasferreira/fireSpreadSimultor/pkg/automata/model"
	"github.com/heitorfreitasferreira/fireSpreadSimultor/pkg/genetic"
)

type runMode string

const (
	automata runMode = "automata"
	evolve   runMode = "genetic"
)

type args struct {
	GeneticParams genetic.GeneticAlgorithmParams
	lattice.LatticeParams
	model.WindParams

	Mode       runMode
	OutputType loggers.OutputType

	Seed       int64
	InputFile  string
	OutputFile string
}

func oldmain() {
	var filepath string
	var seed int64
	flag.StringVar(&filepath, "config", "./input.json", "Path to the config file")
	flag.Int64Var(&seed, "seed", -1, "Seed for random number generation")

	if seed != -1 {
		rand.Seed(seed)
	}
	flag.Parse()

	fileargs := getArgsFromFile(filepath)

	switch fileargs.Mode {
	case automata:
		ac(fileargs)
	case evolve:
		ag(fileargs)
	}
}

func ac(fileargs args) {
	modelParams := model.Parameters{
		InfluenciaUmidade:                   1,
		ProbEspalhamentoFogoInicial:         0.6,
		ProbEspalhamentoFogoArvoreQueimando: 1,
		ProbEspalhamentoFogoQueimaLenta:     0.2,
		InfluenciaVegetacaoCampestre:        0.6,
		InfluenciaVegetacaoSavanica:         1,
		InfluenciaVegetacaoFlorestal:        0.8,
	}

	l := lattice.CreateLattice(fileargs.LatticeParams, fileargs.WindParams, modelParams, &rand.Rand{})

	simulation := l.Run()
	switch fileargs.OutputType {
	case loggers.FolderOfTxt:
		loggers.SalveSimulationInManyFilesInFolder(simulation, fileargs.LatticeParams)
	case loggers.ViewSparkData:
		loggers.ViewFirstSparkPos(simulation, fileargs.LatticeParams)
	case loggers.SingleJson:
		loggers.SaveSimulationInSingleJsonFile(simulation, fileargs.LatticeParams, fileargs.OutputFile)
	case loggers.ViewInConsole:
		simulation.ViewLattice()
	case loggers.ViewInConsoleEndOfLattice:
		simulation.ViewLastIteration()
	}
}

func ag(fileargs args) {

	var modelParams model.Parameters = model.Parameters{
		InfluenciaUmidade:                   1,
		ProbEspalhamentoFogoInicial:         0.6,
		ProbEspalhamentoFogoArvoreQueimando: 1,
		ProbEspalhamentoFogoQueimaLenta:     0.2,
		InfluenciaVegetacaoCampestre:        0.6,
		InfluenciaVegetacaoSavanica:         1,
		InfluenciaVegetacaoFlorestal:        0.8,
	}

	startTime := time.Now()
	var goal lattice.SimulationResult = lattice.CreateAndRunLatticesParallel(fileargs.LatticeParams, modelParams, fileargs.WindParams, 1)[0]
	genetic.Evolve(fileargs.GeneticParams, fileargs.LatticeParams, fileargs.WindParams, goal)
	fmt.Println("Tempo total: ", time.Since(startTime))
}

func getArgsFromFile(relativePath string) args {
	file, err := os.Open(relativePath)
	if err != nil {
		log.Fatalf("Erro ao abrir o arquivo: %v", err)
	}
	defer file.Close()

	args := args{}

	decoder := json.NewDecoder(file)
	err = decoder.Decode(&args)
	if err != nil {
		log.Fatalf("Erro ao decodificar o arquivo JSON: %v", err)
	}
	return args
}
