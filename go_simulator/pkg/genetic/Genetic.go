package genetic

type Selection string

const (
	Tournament               Selection = "tournament"
	Roulette                 Selection = "roulette"
	Elitism                  Selection = "elitism"
	ReverseElitismRoulette   Selection = "reverse-elitism-roulette"
	ReverseElitismTournament Selection = "reverse-elitism-tournament"
	RandomSelector           Selection = "random-selector"
	Random                   Selection = "random"
)

type Reproductor string

const (
	Sexual  Reproductor = "sexual"
	Asexual Reproductor = "asexual"
)

type Crossover string

const (
	Blx Crossover = "blx"
)

type GeneticAlgorithmParams struct {
	Generations           int64
	PopulationSize        int64
	TournamentSize        int64
	SimulationsPerFitness int64
	MutationRate          float64
	MutationProb          float64
	CrossOverRate         float64
	ElitismRate           float64
	ReverseElitismRate    float64
	CrossoverBlxAlpha     float64
	Reproduction          Reproductor
	Selection             Selection
	Crossover             Crossover
}
