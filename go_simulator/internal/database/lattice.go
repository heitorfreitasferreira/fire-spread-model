package database

import (
	"encoding/json"

	"github.com/heitorfreitasferreira/fireSpreadSimultor/pkg/automata/lattice"
	"github.com/heitorfreitasferreira/fireSpreadSimultor/pkg/automata/model"
)

type LatticeRepository interface {
	StoreSimulation(latticeParams lattice.LatticeParams, windParams model.WindParams, modelParams model.Parameters, windMatrix model.WindMatrix, result lattice.SimulationResult) (int, error)
}

func (s *service) StoreSimulation(latticeParams lattice.LatticeParams, windParams model.WindParams, modelParams model.Parameters, windMatrix model.WindMatrix, result lattice.SimulationResult) (int, error) {
	tx, err := s.db.Begin()
	if err != nil {
		return 0, err
	}

	// Prepare the statement for inserting into wind table
	stmt, err := tx.Prepare(`INSERT INTO wind (coef, mult_base, decai, direction, radius, result) VALUES ($1, $2, $3, $4, $5, $6) RETURNING id`)
	if err != nil {
		tx.Rollback()
		return 0, err
	}
	defer stmt.Close()

	// Serialize windMatrix to JSON
	windMatrixJSON, err := json.Marshal(windMatrix)
	if err != nil {
		tx.Rollback()
		return 0, err
	}

	var windID int
	err = stmt.QueryRow(windParams.Coef, windParams.MultBase, windParams.Decai, windParams.Direction, windParams.Radius, windMatrixJSON).Scan(&windID)
	if err != nil {
		tx.Rollback()
		return 0, err
	}
	//
	stmt, err = tx.Prepare(`INSERT INTO model_params (influencia_umidade, prob_espalhamento_fogo_inicial, prob_espalhamento_fogo_arvore_queimando, prob_espalhamento_fogo_queima_lenta, influencia_vegetacao_campestre, influencia_vegetacao_savanica, influencia_vegetacao_florestal) VALUES ($1, $2, $3, $4, $5, $6, $7) RETURNING id`)

	if err != nil {
		tx.Rollback()
		return 0, err
	}
	defer stmt.Close()
	var modelParamID int
	err = stmt.QueryRow(modelParams.InfluenciaUmidade, modelParams.ProbEspalhamentoFogoInicial, modelParams.ProbEspalhamentoFogoArvoreQueimando, modelParams.ProbEspalhamentoFogoQueimaLenta, modelParams.InfluenciaVegetacaoCampestre, modelParams.InfluenciaVegetacaoSavanica, modelParams.InfluenciaVegetacaoFlorestal).Scan(&modelParamID)
	if err != nil {
		tx.Rollback()
		return 0, err
	}
	//
	stmt, err = tx.Prepare(`INSERT INTO lattice (height, width, humidity, iterations, initial_state, fire_spots, result, wind_id, model_params_id)VALUES ($1, $2, $3, $4, $5, $6, $7, $8, $9) RETURNING id`)
	if err != nil {
		tx.Rollback()
		return 0, err
	}
	defer stmt.Close()

	initialStateJSON, err := json.Marshal(latticeParams.InitialState)
	if err != nil {
		tx.Rollback()
		return 0, err
	}
	fireSpotsJSON, err := json.Marshal(latticeParams.FireSpots)
	if err != nil {
		tx.Rollback()
		return 0, err
	}

	resultJSON, err := json.Marshal(result)
	if err != nil {
		tx.Rollback()
		return 0, err
	}

	var id int
	err = stmt.QueryRow(latticeParams.Height, latticeParams.Width, latticeParams.Humidity, latticeParams.Iterations, initialStateJSON, fireSpotsJSON, resultJSON, windID, modelParamID).Scan(&id)
	if err != nil {
		tx.Rollback()
		return 0, err
	}
	err = tx.Commit()
	if err != nil {
		return 0, err
	}
	return id, nil
}
