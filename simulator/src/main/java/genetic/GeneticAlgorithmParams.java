package genetic;

public interface GeneticAlgorithmParams {
    int numberOfGenerations();

    int populationSize();

    double mutationRate();

    double mutationProb();

    double crossoverRate();

    double elitismRate();

    int tournamentSize();

    double crossoverBlxAlpha();

    int numberOfSimulationsPerFitness();

    int reverseElitismN();

}
