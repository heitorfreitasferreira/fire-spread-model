package cmd

import (
	"os"

	"github.com/spf13/cobra"
)

var rootCmd = &cobra.Command{
	Use:   "Fire Spread Simulator",
	Short: "Fire Spread Simulator is a tool to simulate the spread of fire in a lattice",
	Long:  `Fire Spread Simulator is a tool to simulate the spread of fire in a lattice, it also has a genetic algorithm to optimize the parameters of the simulation`,
}

func Execute() {
	err := rootCmd.Execute()
	if err != nil {
		os.Exit(1)
	}
}

func init() {
	rootCmd.AddCommand(singleSimulationCmd)

	singleSimulationCmd.Flags().String("config", "./input.json", "Path to the config file")
	singleSimulationCmd.Flags().Int64("seed", -1, "Seed for random number generation")
	singleSimulationCmd.Flags().Bool("persist", false, "Persist the simulation in the database")

	rootCmd.AddCommand(riverCmd)

	riverCmd.Flags().String("config", "./input.json", "Path to the config file")
	riverCmd.Flags().Int("simulations", 100, "Number of simulations to run")

	rootCmd.AddCommand(queryByWindCmd)

	queryByWindCmd.Flags().StringSliceP("direction", "d", []string{"N", "NE", "E", "SE", "S", "SW", "W", "NW"}, "Wind direction. Can be N, NE, E, SE, S, SW, W, NW. Default is all directions.")
	queryByWindCmd.Flags().IntSlice("radius", []int{1, 2, 3, 4, 5}, "Spark radius. Default is [1,5].")
}
