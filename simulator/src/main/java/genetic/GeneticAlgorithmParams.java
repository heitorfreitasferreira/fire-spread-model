package genetic;

import genetic.reproductors.Reproductor;
import genetic.selectors.Selector;

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

    double reverseElitismPercentage();

    Selector.Type selectorType();

    Reproductor.Type reproductorType();

}
