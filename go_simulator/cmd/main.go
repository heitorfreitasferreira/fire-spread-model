package main

import (
	"encoding/json"
	"flag"
	"fmt"
	"log"
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

type outputType string

const (
	singleJson  outputType = "single-json"
	folderOfTxt outputType = "multiple-txt"
)

type args struct {
	GeneticParams genetic.GeneticAlgorithmParams
	LatticeParams lattice.LatticeParams
	WindParams    model.MatrixParams

	Mode       runMode
	OutputType outputType

	Seed       int64
	InputFile  string
	OutputFile string
}

func main() {
	var filepath string
	flag.StringVar(&filepath, "config", "./input.json", "Path to the config file")
	flag.Parse()

	fileargs := getArgsFromFile(filepath)

	switch fileargs.Mode {
	case automata:
		acClaudiney(fileargs)
	case evolve:
		ag(fileargs)
	}
}

func acClaudiney(fileargs args) {
	startTime := time.Now()

	modelParams := model.Parameters{
		InfluenciaUmidade:                   1,
		ProbEspalhamentoFogoInicial:         0.6,
		ProbEspalhamentoFogoArvoreQueimando: 1,
		ProbEspalhamentoFogoQueimaLenta:     0.2,
		InfluenciaVegetacaoCampestre:        0.6,
		InfluenciaVegetacaoSavanica:         1,
		InfluenciaVegetacaoFlorestal:        0.8,
	}

	l := lattice.CreateLattice(fileargs.LatticeParams, fileargs.WindParams, modelParams)

	simulation := l.Run()
	if fileargs.OutputType == folderOfTxt {
		loggers.SalveSimulationInManyFilesInFolder(simulation, fileargs.LatticeParams)
	}
	fmt.Println("Tempo total: ", time.Since(startTime))
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
