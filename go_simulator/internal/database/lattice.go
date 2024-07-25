package database

import (
	"encoding/json"

	"github.com/heitorfreitasferreira/fireSpreadSimultor/pkg/automata/lattice"
	"github.com/heitorfreitasferreira/fireSpreadSimultor/pkg/automata/model"
)

func (s *service) StoreSimulation(latticeParams lattice.LatticeParams, windParams model.WindParams, modelParams model.Parameters, windMatrix model.WindMatrix, result lattice.SimulationResult) error {
	tx, err := s.db.Begin()
	if err != nil {
		return err
	}

	// Prepare the statement for inserting into wind table
	stmt, err := tx.Prepare(`INSERT INTO wind (coef, mult_base, decai, direction, radius, result) VALUES ($1, $2, $3, $4, $5, $6) RETURNING id`)
	if err != nil {
		tx.Rollback()
		return err
	}
	defer stmt.Close()

	// Serialize windMatrix to JSON
	windMatrixJSON, err := json.Marshal(windMatrix)
	if err != nil {
		tx.Rollback()
		return err
	}

	var windID int
	err = stmt.QueryRow(windParams.Coef, windParams.MultBase, windParams.Decai, windParams.Direction, windParams.Radius, windMatrixJSON).Scan(&windID)
	if err != nil {
		tx.Rollback()
		return err
	}
	//
	stmt, err = tx.Prepare(`INSERT INTO model_params (influencia_umidade, prob_espalhamento_fogo_inicial, prob_espalhamento_fogo_arvore_queimando, prob_espalhamento_fogo_queima_lenta, influencia_vegetacao_campestre, influencia_vegetacao_savanica, influencia_vegetacao_florestal) VALUES ($1, $2, $3, $4, $5, $6, $7) RETURNING id`)

	if err != nil {
		tx.Rollback()
		return err
	}
	defer stmt.Close()
	var modelParamID int
	err = stmt.QueryRow(modelParams.InfluenciaUmidade, modelParams.ProbEspalhamentoFogoInicial, modelParams.ProbEspalhamentoFogoArvoreQueimando, modelParams.ProbEspalhamentoFogoQueimaLenta, modelParams.InfluenciaVegetacaoCampestre, modelParams.InfluenciaVegetacaoSavanica, modelParams.InfluenciaVegetacaoFlorestal).Scan(&modelParamID)
	if err != nil {
		tx.Rollback()
		return err
	}
	//
	stmt, err = tx.Prepare(`INSERT INTO lattice (height, width, humidity, iterations, initial_state, fire_spots, result, wind_id, model_params_id)VALUES ($1, $2, $3, $4, $5, $6, $7, $8, $9)`)
	if err != nil {
		tx.Rollback()
		return err
	}
	defer stmt.Close()

	initialStateJSON, err := json.Marshal(latticeParams.InitialState)
	if err != nil {
		tx.Rollback()
		return err
	}
	fireSpotsJSON, err := json.Marshal(latticeParams.FireSpots)
	if err != nil {
		tx.Rollback()
		return err
	}

	resultJSON, err := json.Marshal(result)
	if err != nil {
		tx.Rollback()
		return err
	}

	_, err = stmt.Exec(latticeParams.Height, latticeParams.Width, latticeParams.Humidity, latticeParams.Iterations, initialStateJSON, fireSpotsJSON, resultJSON, windID, modelParamID)
	if err != nil {
		tx.Rollback()
		return err
	}
	return tx.Commit()
}
