CREATE TABLE
    IF NOT EXISTS spark_simulation_seed (
        lattice_id INTEGER NOT NULL,
        seed BIGINT NOT NULL,
        FOREIGN KEY (lattice_id) REFERENCES lattice (id),
        CONSTRAINT unique_simulation_seed UNIQUE (lattice_id, seed)
    );