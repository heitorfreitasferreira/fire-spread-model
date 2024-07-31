package database

type SparkRepository interface {
	GetNextSparkTraversingWaterBarrierSeed() (int64, error)
	StoreSparkSimulationSeed(latticeID int, seed int64) error
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
