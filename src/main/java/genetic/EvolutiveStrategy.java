package genetic;

import lombok.extern.java.Log;
import model.modelos.Heitorzera2;
import model.modelos.ModelParameters;
import model.reticulado.Reticulado;
import model.utils.RandomDoubleSingleton;
import model.utils.Tuple;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

@Log
public class EvolutiveStrategy {
    int[][][] objectiveSimulation;
    Reticulado reticulado;
    List<Tuple<ModelParameters, Double>> population;
    Statistics statistics;
    int POPULATION_SIZE;
    private final RandomDoubleSingleton randomGenerator;
    private static final double INVALID_FITNESS = -1.0;
    private static final double MUTATION_RATE = 0.05;
    private static final int TOURNAMENT_K = 2;

    public EvolutiveStrategy(Reticulado reticulado, int[][][] objectiveSimulation){
        this.POPULATION_SIZE = 10;

        this.objectiveSimulation = objectiveSimulation;
        this.reticulado = reticulado;
        this.randomGenerator = RandomDoubleSingleton.getInstance(0);

        population = new ArrayList<>(POPULATION_SIZE);

        this.statistics = new Statistics();

        this.startPopulation(POPULATION_SIZE);
    }

    public void evolve(int geracoes){
        calculateFitness();
        for(int i = 0; i < geracoes; i++){
            evolve();
            statistics.updateStatistics(population);
        }
        statistics.logToFile();
    }

    private void evolve(){
        reproduceAsexually();
        calculateFitness();
        selectByTournament(TOURNAMENT_K);
    }

    private void selectByTournament(int k) {
        List<Tuple<ModelParameters, Double>> newPopulation = new ArrayList<>(POPULATION_SIZE);
        for (int i = 0; i < POPULATION_SIZE; i++) {
            List<Tuple<ModelParameters, Double>> tournament = new ArrayList<>(k);
            for (int j = 0; j < k; j++) {
                tournament.add(population.get(randomGenerator.nextInt(POPULATION_SIZE)));
            }
            tournament.sort((a, b) -> b.getSecond().compareTo(a.getSecond()));
            var individual = tournament.get(0);

            newPopulation.add(individual);
        }
        population = newPopulation;
    }

    private void reproduceAsexually() {
        for (int i = 0; i < POPULATION_SIZE; i++) {
            ModelParameters modelParameters = population.get(i).getFirst();
            ModelParameters newModelParameters = new ModelParameters(
                    modelParameters.influenciaUmidade() + randomGenerator.nextDouble(-MUTATION_RATE, MUTATION_RATE),
                    modelParameters.probEspalhamentoFogoInicial() + randomGenerator.nextDouble(-MUTATION_RATE, MUTATION_RATE),
                    modelParameters.probEspalhamentoFogoArvoreQueimando() + randomGenerator.nextDouble(-MUTATION_RATE, MUTATION_RATE),
                    modelParameters.probEspalhamentoFogoQueimaLenta() + randomGenerator.nextDouble(-MUTATION_RATE, MUTATION_RATE),
                    modelParameters.influenciaVegetacaoCampestre() + randomGenerator.nextDouble(-MUTATION_RATE, MUTATION_RATE),
                    modelParameters.influenciaVegetacaoSavanica() + randomGenerator.nextDouble(-MUTATION_RATE, MUTATION_RATE),
                    modelParameters.influenciaVegetacaoFlorestal() + randomGenerator.nextDouble(-MUTATION_RATE, MUTATION_RATE)
            );
            population.add(new Tuple<>(newModelParameters, INVALID_FITNESS));
        }
    }

    private void calculateFitness() {
        for (Tuple<ModelParameters, Double> individual : population) {
            if (individual.getSecond() != INVALID_FITNESS) continue;

            ModelParameters modelParameters = individual.getFirst();
            if (!isGenotypeValid(modelParameters)) {
                individual.setSecond(0.0);// Penalize invalid individuals
                continue;
            }

            reticulado.setModelo(new Heitorzera2(modelParameters));
            int[][][] simulation = reticulado.run();
            Double fitness = compareOutputs(simulation);
            individual.setSecond(fitness);
        }
    }

    private void startPopulation(int size){
        for(int i = 0; i < size; i++){
            ModelParameters modelParameters = new ModelParameters(
                    randomGenerator.nextDouble(),
                    randomGenerator.nextDouble(),
                    randomGenerator.nextDouble(),
                    randomGenerator.nextDouble(),
                    randomGenerator.nextDouble(),
                    randomGenerator.nextDouble(),
                    randomGenerator.nextDouble()
            );
            population.add(new Tuple<>(modelParameters, INVALID_FITNESS));
        }
    }

    private Double compareOutputs(int[][][] simulation){
        float fitness = 0;
        float numberOfCells = (float)reticulado.getAltura() * reticulado.getLargura() * Reticulado.QNT_ITERACOES;
        for(int i = 0; i < reticulado.getAltura(); i++){
            for(int j = 0; j < reticulado.getLargura(); j++){
                for(int k = 0; k < Reticulado.QNT_ITERACOES; k++){
                    if(simulation[k][i][j] == objectiveSimulation[k][i][j]){
                        fitness++;
                    }
                }
            }
        }
        return (double) (fitness / numberOfCells);
    }

    private boolean isGenotypeValid(ModelParameters modelParameters){
        Field[] fields = modelParameters.getClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            try {
                if ((double)field.get(modelParameters) < 0.0 || (double)field.get(modelParameters) > 1.0) {
                    return false;
                }
            } catch (IllegalAccessException e) {
                log.severe("Error while accessing field in ModelParameters");
            }
        }
        return true;
    }
}
