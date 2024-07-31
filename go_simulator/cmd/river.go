package cmd

import (
	"log"
	"math/rand"

	"github.com/heitorfreitasferreira/fireSpreadSimultor/internal/database"
	"github.com/heitorfreitasferreira/fireSpreadSimultor/pkg/automata/lattice"
	"github.com/heitorfreitasferreira/fireSpreadSimultor/pkg/automata/model"
	"github.com/spf13/cobra"
)

type simResult struct {
	lattice.SimulationResult
	model.WindParams
	model.WindMatrix
	seed int64
}

var riverCmd = &cobra.Command{
	Use:   "river",
	Short: "Simulate spark traversing a fire barrier like water",
	Long:  "Run simulations with some deterministic parameters and seed to create data for experiment about spark traversing a fire barrier like water",
	Run: func(cmd *cobra.Command, args []string) {
		numberOfSimulations, _ := cmd.Flags().GetInt("simulations")
		filepath, _ := cmd.Flags().GetString("config")
		file := getArgsFromFile(filepath)
		db := database.New()

		nextSeed, err := db.GetNextSparkTraversingWaterBarrierSeed()

		resultChan := make(chan simResult)
		for simulationSeedDelta := range numberOfSimulations {
			go func(seed int64) {
				for _, windParam := range windParamsForSimulation() {

					runner := lattice.CreateLattice(file.LatticeParams,
						windParam,
						model.DefaultParams,
						rand.New(rand.NewSource(seed)),
					)

					resultChan <- simResult{
						SimulationResult: runner.Run(),
						WindParams:       windParam,
						WindMatrix:       runner.ModelRunner.WindMatrix,
						seed:             seed,
					}
				}
				var currentPercentage float64 = float64((int(seed)-int(nextSeed))*100) / float64(numberOfSimulations)
				log.Printf("Seed %d done, [PROGRESS %.2f]", seed, currentPercentage)
			}(nextSeed + int64(simulationSeedDelta))
		}

		for i := 0; i < numberOfSimulations*len(windParamsForSimulation()); i++ {
			saveable := <-resultChan

			sim_id, err := db.StoreSimulation(file.LatticeParams, saveable.WindParams, model.DefaultParams, saveable.WindMatrix, saveable.SimulationResult)
			if err != nil {
				log.Fatalf("Error storing simulation: %v", err)
			}
			err = db.StoreSparkSimulationSeed(sim_id, saveable.seed)
			if err != nil {
				log.Fatalf("Error storing seed: %v", err)
			}
		}
		if err != nil {
			log.Fatal(err)
		}
	},
}

func createSimulationResults(file config, numberOfSimulations int, nextSeed int64) chan simResult {
	out := make(chan simResult)
	for seed := nextSeed; seed < nextSeed+int64(numberOfSimulations); seed++ {
		go func(seed int64) {
			for _, windParam := range windParamsForSimulation() {
				runner := lattice.CreateLattice(file.LatticeParams, windParam, model.DefaultParams, rand.New(rand.NewSource(seed)))
				out <- simResult{
					SimulationResult: runner.Run(),
					WindParams:       windParam,
					WindMatrix:       runner.ModelRunner.WindMatrix,
					seed:             seed,
				}
			}
			close(out)
		}(seed)
	}
	return out
}

func storeSimulation(resultChan chan simResult, db database.Service, file config) {
	for {
		saveable := <-resultChan
		sim_id, err := db.StoreSimulation(file.LatticeParams, saveable.WindParams, model.DefaultParams, saveable.WindMatrix, saveable.SimulationResult)
		if err != nil {
			log.Fatalf("Error storing simulation: %v", err)
		}
		err = db.StoreSparkSimulationSeed(sim_id, saveable.seed)
		if err != nil {
			log.Fatalf("Error storing seed: %v", err)
		}
	}
}
func windParamsForSimulation() []model.WindParams {
	dirs := [...]model.WindDirection{
		model.N,
		model.NE,
		model.E,
		model.SE,
		model.S,
		model.SW,
		model.W,
		model.NW,
	}
	maxRadius := 5
	var windParams []model.WindParams
	for radius := 1; radius <= maxRadius; radius++ {
		for _, dir := range dirs {
			coef := 0.1
			multBase := 0.5
			decai := 0.5
			windParams = append(windParams, model.WindParams{Coef: coef, MultBase: multBase, Decai: decai, Direction: dir, Radius: radius})
		}
	}
	return windParams
}
