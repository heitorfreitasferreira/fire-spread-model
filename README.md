# Fire Spread Model for the Brazilian Cerrado

This Java project, "Fire Spread Model," implements an enhanced stochastic model utilizing cellular automata for simulating wildfires specifically in the Brazilian Cerrado biome. Leveraging JCommander for command-line parameter configuration, it provides a flexible and powerful tool for researchers and environmentalists to model and understand the dynamics of wildfires. This simulation aims to assist in the development of more effective firefighting strategies and the planning of mitigation efforts by accurately representing the complexities of fire spread, influenced by factors such as vegetation types, wind currents, and air humidity.

## Features

- Detailed wildfire simulation based on cellular automata and stochastic rules.
- Customizable through command-line arguments for dynamic simulation setups.
- Incorporates environmental factors like vegetation, wind, and humidity.

## Requirements

- Java JDK 21 or higher.
- Maven for building and managing the project.

## Building the Project

1. Clone the repository to your local machine.
2. Navigate to the project directory.
3. Build the project using Maven:
    ```bash
    mvn package
    ```

This will compile the source code and package it into a runnable JAR file located in the `target/` directory, named `fire-spread-model-1.0-SNAPSHOT.jar`.

## Running the Simulation

After building the project, run the simulation using the following command:

```bash
java -jar target/fire-spread-model-1.0-SNAPSHOT.jar [options]
```

### Command-Line Options

The simulation supports a variety of command-line options allowing for detailed customization of the simulation process. Below are the available options along with their descriptions:

- `--mode`: Specifies the operation mode of the simulation. This determines the main behavior of the program upon execution. There are two valid options for this parameter:
  - `single-simulation`: Runs a single instance of the simulation with the specified parameters. This mode is suitable for conducting a specific simulation scenario, analyzing its outcomes based on the provided configuration. Example: `--mode single-simulation`.
  - `genetic-algorithm`: Invokes the genetic algorithm mode, which utilizes genetic algorithm techniques for optimizing or exploring simulation parameters. This mode is intended for scenarios where you wish to iteratively improve upon the simulation parameters to achieve desired outcomes or to explore the parameter space for optimal configurations. Example: `--mode genetic-algorithm`.
  - Any other value provided to `--mode` will result in an `IllegalArgumentException` indicating an invalid mode selection.

- `--input`: The path to a JSON input file that contains the simulation configuration. This file should define the initial conditions and parameters for the simulation, such as the layout of the simulated area, initial fire locations, and environmental conditions. The structure of the JSON file must match the expected input format of the simulation model. Example: `--input config.json`.

- `--output`: The file path where the simulation results will be saved. If not specified, the default path `./output.json` is used. The output file includes detailed results of the simulation, such as the progression of the fire, affected areas, and final state of the simulation environment. Example: `--output results.json`.

- `--number-of-generations`: Defines the number of generations (simulation steps) to run. Each generation represents a timestep in the simulation process. Increasing the number of generations allows for longer simulation times. Default value is 20. Example: `--number-of-generations 50`.

- `--population-size`: Sets the size of the population in the simulation. In the context of wildfire simulations, this could represent the number of distinct fire particles or units being simulated. Default value is 100. Example: `--population-size 200`.

- `--mutation-rate`: Determines the rate of mutation within the simulation. This could affect various factors such as the spread rate or behavior of the fire under different conditions. Default value is 0.1. Example: `--mutation-rate 0.05`.

- `--mutation-prob`: Specifies the probability of mutation occurring at each generation for any given unit of the population. This adds an element of randomness or variability to the simulation, reflecting the unpredictable nature of real-world wildfires. Default value is 0.1. Example: `--mutation-prob 0.2`.

- `--crossover-rate`: Defines the rate at which crossover occurs between units in the population. In wildfire simulation, this could simulate the merging or interaction of separate fire fronts. Default value is 0.9. Example: `--crossover-rate 0.8`.

- `--elitism-rate`: The proportion of the top-performing units in the population that are preserved unchanged into the next generation. This ensures that successful strategies or behaviors are maintained. Default value is 0.1. Example: `--elitism-rate 0.15`.

- `--tournament-size`: In selection processes, this sets the size of the tournament for choosing which units reproduce. This parameter impacts the selection pressure. Default value is 2. Example: `--tournament-size 3`.

- `--max-iterations`: The maximum number of iterations the simulation should run before automatically stopping. This serves as a fail-safe to prevent excessively long or infinite simulation runs. Default value is 100. Example: `--max-iterations 150`.

- `--crossover-blx-alpha`: Specifies the BLX-alpha parameter used in the crossover operation, impacting how offspring are generated from parents. This parameter can influence the diversity of the population. Default value is 0.001. Example: `--crossover-blx-alpha 0.005`.

Each option allows for the fine-tuning of the simulation to reflect different scenarios, conditions, and behaviors observed in wildfires, providing a powerful tool for research and analysis.

### Example

## Building

First, if you haven't already, build the project using Maven:

```bash
mvn package
```

## Running a single simulation

```bash
java -jar target/fire-spread-model-1.0-SNAPSHOT.jar --mode "single-simulation" --input "input.json" --output "results.json"
```

## Running the Genetic Algorithm Module

```bash
java -jar target/fire-spread-model-1.0-SNAPSHOT.jar --mode "genetic-algorithm"
```

## Running the Genetic Algorithm Module with Custom Parameters

```bash
java -jar target/fire-spread-model-1.0-SNAPSHOT.jar --mode "genetic-algorithm" --number-of-generations 50 --population-size 200 --mutation-rate 0.05 --mutation-prob 0.2 --crossover-rate 0.8 --elitism-rate 0.15 --tournament-size 3 --max-iterations 150 --crossover-blx-alpha 0.005
```

## Contributing

Contributions to the Fire Spread Model project are welcome. Contact me at @heitorfreitasferreira for any questions, suggestions or improvements.
