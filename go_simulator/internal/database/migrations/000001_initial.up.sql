CREATE TABLE
    IF NOT EXISTS wind (
        id INTEGER PRIMARY KEY AUTOINCREMENT,
        coef REAL NOT NULL,
        mult_base REAL NOT NULL,
        decai REAL NOT NULL,
        direction TEXT NOT NULL,
        radius REAL NOT NULL,
        result TEXT NOT NULL
    );

CREATE TABLE
    IF NOT EXISTS lattice (
        id INTEGER PRIMARY KEY AUTOINCREMENT,
        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        height INT NOT NULL,
        width INT NOT NULL,
        humidity REAL NOT NULL,
        iterations INT NOT NULL,
        initial_state TEXT NOT NULL,
        fire_spots TEXT NOT NULL,
        result TEXT NOT NULL,
        wind_id INT NOT NULL,
        model_params_id INT NOT NULL,
        FOREIGN KEY (wind_id) REFERENCES wind (id),
        FOREIGN KEY (model_params_id) REFERENCES model_params (id)
    );

CREATE TABLE
    IF NOT EXISTS model_params (
        id INTEGER PRIMARY KEY AUTOINCREMENT,
        influencia_umidade FLOAT NOT NULL,
        prob_espalhamento_fogo_inicial FLOAT NOT NULL,
        prob_espalhamento_fogo_arvore_queimando FLOAT NOT NULL,
        prob_espalhamento_fogo_queima_lenta FLOAT NOT NULL,
        influencia_vegetacao_campestre FLOAT NOT NULL,
        influencia_vegetacao_savanica FLOAT NOT NULL,
        influencia_vegetacao_florestal FLOAT NOT NULL
    );