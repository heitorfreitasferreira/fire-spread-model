# Fire spread simulator and optimizer

## Usage

### Build

```make build```

Caso queira executar já com a configuração padrão, execute:

```make run```

Isso irá executar o programa com a configuração padrão que está no arquivo `input.json`, no mesmo diretório que este README.md.

### Execution

Execute the binary (should be at ./bin/fire-spread-simulator if you builded with make build) with the argument `--config` and the path to the configuration file.

```json
{
    "Mode": "genetic", // "automata" | "genetic"
    "Seed": 0, // Random number generator seed
    "InputFile": "./input.json", // Path to this file
    "OutputType": "single-json", // "single-json" | "multiple-txt" | "csv"
    "OutputFile": "./output.json", // if "single-json" | "csv" file, if "multiple-txt" folder,
    "GeneticParams": {
        "Generations": 10,
        "PopulationSize": 100,
        "TournamentSize": 2,
        "SimulationsPerFitness": 10,
        "MutationRate": 0.1,
        "MutationProb": 0.1,
        "CrossOverRate": 0.1,
        "ReverseElitismRate": 0.1,
        "CrossoverBlxAlpha": 0.01,
        "Selection": "TournamentSelection", // "tournament" | "roulette"
        "MatingPoolSelection": "standard", //"standard" (Elitism) | "roulette" | "tournament"
        "Crossover": "MeanCrossover", //"one-point" | "mean" | "arithmetic" | "blx-alpha"
        "Mutation": "mutationTypeValue" // "standard" | "uniform" | "creep"
    },
    "LatticeParams": {
        "Height": 64,
        "Width": 64,
        "Humidity": 0.5,
        "Iterations": 100,
        "WindDirection": "N", // "N" | "NE" | "E" | "SE" | "S" | "SW" | "W" | "NW"
        "InitialState": 4, //[0...9], "ASH" "INITIAL_FIRE" "FIRE" "EMBER" "MEADOW" "SAVANNAH" "FOREST" "WATER" "ROOTS" "NONE"
        "FireSpots": [
            {
                "I": 32, // lINE
                "J": 32 // COLUMN
            }
        ]
    }
}
```
