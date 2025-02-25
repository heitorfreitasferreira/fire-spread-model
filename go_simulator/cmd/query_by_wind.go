package cmd

import (
	"fmt"
	"log"

	"github.com/heitorfreitasferreira/fireSpreadSimultor/internal/analyses"
	"github.com/heitorfreitasferreira/fireSpreadSimultor/internal/database"
	"github.com/heitorfreitasferreira/fireSpreadSimultor/pkg/automata/model"
	"github.com/spf13/cobra"
)

var queryByWindCmd = &cobra.Command{
	Use:   "query_by_wind",
	Short: "Query the database for simulations by wind_parameters",
	Long:  `Query the database for simulations by wind_parameters like spark radius and wind direction`,
	Run: func(cmd *cobra.Command, args []string) {
		directionsStr, _ := cmd.Flags().GetStringSlice("direction")
		radius, _ := cmd.Flags().GetIntSlice("radius")

		db := database.New()

		directions := make([]model.WindDirection, 0)
		for _, directionStr := range directionsStr {
			directions = append(directions, model.WindDirection(directionStr))
		}

		results, err := db.GetSimulationsByWindParams(directions, radius)
		if err != nil {
			log.Fatal(err)
		}

		fmt.Print(analyses.NewRiverAnalysis(results).ToCsv(','))

		if err != nil {
			log.Fatal(err)
		}

	},
}
