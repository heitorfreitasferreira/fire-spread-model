package loggers

import (
	"encoding/json"
	"os"

	"github.com/heitorfreitasferreira/fireSpreadSimultor/pkg/automata/lattice"
)

func SaveSimulationInSingleJsonFile(simulation lattice.SimulationResult, latticeParams lattice.LatticeParams, outputFile string) {
	byteResult, err := json.Marshal(simulation)
	// var matrix [][][]int = make([][][]int, latticeParams.Iterations)
	// for k := 0; k < latticeParams.Iterations; k++ {
	// 	matrix[k] = make([][]int, latticeParams.Height)
	// 	for j := 0; j < latticeParams.Height; j++ {
	// 		matrix[k][j] = make([]int, latticeParams.Width)
	// 		for i := 0; i < latticeParams.Width; i++ {
	// 			matrix[k][j][i] = int(simulation[k][j][i])
	// 		}
	// 	}
	// }
	if err != nil {
		panic(err)
	}
	file, err := os.Create(outputFile)
	if err != nil {
		panic(err)
	}
	defer file.Close()
	_, err = file.Write(byteResult)
	if err != nil {
		panic(err)
	}
}
