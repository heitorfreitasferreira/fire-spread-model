package loggers

import (
	"fmt"
	"log"
	"os"
	"time"

	"github.com/heitorfreitasferreira/fireSpreadSimultor/pkg/automata/lattice"
	"github.com/heitorfreitasferreira/fireSpreadSimultor/pkg/automata/lattice/cell"
)

func SalveSimulationInManyFilesInFolder(simulation lattice.SimulationResult, latticeParams lattice.LatticeParams) {
	baseFolder := time.Now().Format("2006-01-02-15-04-05")

	err := os.MkdirAll(baseFolder, 0755)
	if err != nil {
		log.Fatalf("Erro ao criar o diretório: %v", err)
	}

	for i, iteration := range simulation {
		saveLatticeInTxtFile(iteration, latticeParams, baseFolder, i)
	}
}

func saveLatticeInTxtFile(lattice [][]cell.CellState, latticeParams lattice.LatticeParams, baseFolder string, iteration int) {
	fileName := fmt.Sprintf("%s/%d.txt", baseFolder, iteration)
	file, err := os.Create(fileName)
	if err != nil {
		log.Fatal(err)
	}
	defer file.Close()

	fmt.Fprintf(file, "%d %d\n%.3f %.3f\n", latticeParams.Height, latticeParams.Width, float32(latticeParams.Height)*0.256, float32(latticeParams.Width)*0.256)

	for _, row := range lattice {
		for _, cellState := range row {
			fmt.Fprintf(file, "%d ", cellState)
		}
		fmt.Fprintln(file)
	}
}
