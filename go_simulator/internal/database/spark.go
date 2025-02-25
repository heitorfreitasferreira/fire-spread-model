package database

import (
	"encoding/json"
	"fmt"
	"strings"

	"github.com/heitorfreitasferreira/fireSpreadSimultor/internal/analyses"
	"github.com/heitorfreitasferreira/fireSpreadSimultor/pkg/automata/lattice"
	"github.com/heitorfreitasferreira/fireSpreadSimultor/pkg/automata/model"
)

type SparkRepository interface {
	GetNextSparkTraversingWaterBarrierSeed() (int64, error)
	StoreSparkSimulationSeed(latticeID int, seed int64) error
	GetSimulationsByWindParams(directions []model.WindDirection, radius []int) ([]analyses.RiverAnalysisInput, error)
}

func (s *service) GetSimulationsByWindParams(directions []model.WindDirection, radius []int) ([]analyses.RiverAnalysisInput, error) {
	tx, err := s.db.Begin()
	if err != nil {
		return nil, err
	}
	defer tx.Rollback()

	// Construir partes da consulta para os arrays
	dirPlaceholders := make([]string, len(directions))
	radPlaceholders := make([]string, len(radius))

	args := make([]interface{}, 0, len(directions)+len(radius))
	for i, dir := range directions {
		dirPlaceholders[i] = fmt.Sprintf("$%d", i+1)
		args = append(args, dir)
	}
	for i, rad := range radius {
		radPlaceholders[i] = fmt.Sprintf("$%d", len(directions)+i+1)
		args = append(args, rad)
	}

	query := fmt.Sprintf(
		`SELECT lattice.result, wind.direction, wind.radius FROM lattice JOIN wind ON lattice.wind_id = wind.id WHERE wind.direction IN (%s) AND wind.radius IN (%s)`,
		strings.Join(dirPlaceholders, ", "),
		strings.Join(radPlaceholders, ", "),
	)

	stmt, err := tx.Prepare(query)
	if err != nil {
		return nil, err
	}
	defer stmt.Close()

	rows, err := stmt.Query(args...)
	if err != nil {
		return nil, err
	}
	defer rows.Close()

	results := make([]analyses.RiverAnalysisInput, 0)
	for rows.Next() {
		var (
			resultData    []byte
			windDirection model.WindDirection
			radius        int
		)

		err = rows.Scan(&resultData, &windDirection, &radius)
		if err != nil {
			return nil, err
		}

		var simulationResult lattice.SimulationResult
		err = json.Unmarshal(resultData, &simulationResult)
		if err != nil {
			return nil, err
		}

		res := analyses.RiverAnalysisInput{
			SimulationResult: simulationResult,
			WindDirection:    windDirection,
			Radius:           radius,
		}

		results = append(results, res)
	}

	return results, nil
}

func (s *service) GetNextSparkTraversingWaterBarrierSeed() (int64, error) {
	tx, err := s.db.Begin()
	if err != nil {
		return 0, err
	}
	defer tx.Rollback()

	stmt, err := tx.Prepare(`SELECT seed FROM spark_simulation_seed WHERE seed IS NOT NULL ORDER BY seed DESC LIMIT 1`)
	if err != nil {
		return 0, err
	}
	defer stmt.Close()

	var seed int64
	err = stmt.QueryRow().Scan(&seed)
	if err != nil {
		return 0, err
	}

	return seed + 1, nil
}

func (s *service) StoreSparkSimulationSeed(latticeID int, seed int64) error {
	tx, err := s.db.Begin()
	if err != nil {
		return err
	}

	stmt, err := tx.Prepare(`INSERT INTO spark_simulation_seed (lattice_id, seed) VALUES ($1, $2)`)
	if err != nil {
		tx.Rollback()
		return err
	}
	defer stmt.Close()

	_, err = stmt.Exec(latticeID, seed)
	if err != nil {
		tx.Rollback()
		return err
	}

	return tx.Commit()
}
