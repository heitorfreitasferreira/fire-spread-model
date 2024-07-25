package cmd

import (
	"log"
	"math/rand"

	"github.com/heitorfreitasferreira/fireSpreadSimultor/internal/database"
	"github.com/heitorfreitasferreira/fireSpreadSimultor/internal/loggers"
	"github.com/heitorfreitasferreira/fireSpreadSimultor/pkg/automata/lattice"
	"github.com/heitorfreitasferreira/fireSpreadSimultor/pkg/automata/model"
	"github.com/heitorfreitasferreira/fireSpreadSimultor/pkg/genetic"
	"github.com/spf13/cobra"
)

type args struct {
	genetic.GeneticAlgorithmParams `json:"GeneticAlgorithmParams"`
	lattice.LatticeParams          `json:"LatticeParams"`
	model.WindParams               `json:"WindParams"`

	OutputType loggers.OutputType

	OutputFile string
}

var singleSimulationCmd = &cobra.Command{
	Use:   "run",
	Short: "Run a single simulation",
	Long:  `Run a single simulation`,
	Run: func(cmd *cobra.Command, args []string) {
		filepath, _ := cmd.Flags().GetString("config")
		seed, _ := cmd.Flags().GetInt64("seed")
		persist, _ := cmd.Flags().GetBool("persist")
		file := getArgsFromFile(filepath)
		var randGenerator *rand.Rand
		if seed == -1 {
			randGenerator = &rand.Rand{}
		} else {
			randGenerator = rand.New(rand.NewSource(seed))
		}
		modelParams := model.Parameters{
			InfluenciaUmidade:                   1,
			ProbEspalhamentoFogoInicial:         0.6,
			ProbEspalhamentoFogoArvoreQueimando: 1,
			ProbEspalhamentoFogoQueimaLenta:     0.2,
			InfluenciaVegetacaoCampestre:        0.6,
			InfluenciaVegetacaoSavanica:         1,
			InfluenciaVegetacaoFlorestal:        0.8,
		}
		runner := lattice.CreateLattice(file.LatticeParams, file.WindParams, modelParams, randGenerator)
		simulation := runner.Run()

		if persist {
			db := database.New()

			err := db.StoreSimulation(file.LatticeParams, file.WindParams, model.DefaultParams, runner.ModelRunner.WindMatrix, simulation)
			if err != nil {
				log.Fatal(err)
			}
		} else {
			switch file.OutputType {
			case loggers.FolderOfTxt:
				loggers.SalveSimulationInManyFilesInFolder(simulation, file.LatticeParams)
			case loggers.ViewSparkData:
				loggers.ViewFirstSparkPos(simulation, file.LatticeParams)
			case loggers.SingleJson:
				loggers.SaveSimulationInSingleJsonFile(simulation, file.LatticeParams, file.OutputFile)
			case loggers.ViewInConsole:
				simulation.ViewLattice()
			case loggers.ViewInConsoleEndOfLattice:
				simulation.ViewLastIteration()
			}
		}
	},
}
