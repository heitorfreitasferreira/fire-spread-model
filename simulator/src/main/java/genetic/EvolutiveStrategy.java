package genetic;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import genetic.reproductors.Reproductor;
import lombok.extern.java.Log;
import model.estados.Estados;
import model.modelos.Heitorzera2;
import model.modelos.ModelParameters;
import model.reticulado.Reticulado;
import model.reticulado.ReticuladoFactory;
import model.reticulado.ReticuladoParameters;
import model.terreno.GeradorLateral;
import model.utils.ProgressBarSingleton;
import model.utils.RandomDoubleSingleton;
import model.utils.Triple;
import model.utils.Tuple;
import model.vento.DirecoesVento;

@Log
public class EvolutiveStrategy {
    public static final Double ZERO_FITNESS = 0.0;
    public static final double INVALID_FITNESS = -1.0;
    int[][][] objectiveSimulation;
    List<Tuple<ModelParameters, Double>> population;
    Statistics statistics;
    private final RandomDoubleSingleton randomGenerator;
    private final int ALTURA;
    private final int QNT_ITERACOES;
    private final int LARGURA;
    private final Reproductor reproductor;

    private List<Triple<Integer, Integer, Integer>> cellWithFire;
    private ProgressBarSingleton pb;

    private final GeneticAlgorithmParams params;

    public EvolutiveStrategy(
            int[][][] objectiveSimulation,
            GeneticAlgorithmParams params,
            ReticuladoParameters reticuladoParameters,
            Reproductor reproductor) {

        this.statistics = new Statistics();
        this.ALTURA = objectiveSimulation[0].length;
        this.LARGURA = objectiveSimulation[0][0].length;
        this.objectiveSimulation = objectiveSimulation;
        this.randomGenerator = RandomDoubleSingleton.getInstance();
        this.QNT_ITERACOES = reticuladoParameters.QNT_ITERACOES();
        this.params = params;
        this.reproductor = reproductor;
        population = new ArrayList<>(params.populationSize());
        this.startPopulation(params.populationSize());
        this.pb = ProgressBarSingleton.getInstance(params.numberOfGenerations() * params.populationSize() * 4);

        this.cellWithFire = new ArrayList<>();
    }

    public void evolve() {
        calculateFitness();
        for (int i = 0; i < params.numberOfGenerations(); i++) {
            stepOneGeneration();
            statistics.updateStatistics(population);
        }
        statistics.logToFile(params);
    }

    private void stepOneGeneration() {
        population.addAll(reproductor.reproduzir(population));
        calculateFitness();
        selectByTournamentRemovingNBestFromPoll(5);
    }

    private void selectByTournamentRemovingNBestFromPoll(int n) {
        population.sort((a, b) -> b.getSecond().compareTo(a.getSecond()));
        // Removing n best individuals
        for (int i = 0; i < n; i++) {
            population.remove(0);
        }
        selectByTournament();
    }

    private void selectByTournament() {
        List<Tuple<ModelParameters, Double>> newPopulation = new ArrayList<>(params.populationSize());
        while (newPopulation.size() < params.populationSize()) {
            pb.step();
            List<Tuple<ModelParameters, Double>> tournament = new ArrayList<>();
            for (int j = 0; j < params.tournamentSize(); j++) {
                tournament.add(population.get(randomGenerator.nextInt(params.populationSize())));
            }
            tournament.sort((a, b) -> b.getSecond().compareTo(a.getSecond()));
            var bestIndividual = tournament.get(0);
            newPopulation.add(bestIndividual);
        }
        population = newPopulation;
    }

    private void calculateFitness() {
        population.parallelStream().forEach(individual -> {
            pb.step();
            if (individual.getSecond() != INVALID_FITNESS)
                return;

            ModelParameters modelParameters = individual.getFirst();
            if (!isGenotypeValid(modelParameters)) {
                individual.setSecond(ZERO_FITNESS); // Penalize invalid individuals
                return;
            }
            int[][][][] simulations = runManySimulations(params.numberOfSimulationsPerFitness());
            int[][][] simulationMode = getModeOfSimulations(simulations);
            Double fitness = compareOutputs(simulationMode);
            individual.setSecond(fitness);
        });
    }

    private int[][][][] runManySimulations(int numberOfSimulations) {
        int[][][][] simulations = new int[numberOfSimulations][QNT_ITERACOES][ALTURA][LARGURA];
        for (int i = 0; i < numberOfSimulations; i++) {
            Reticulado reticulado = getReticulado();
            ModelParameters modelParameters = population.get(i).getFirst();
            reticulado.setModelo(new Heitorzera2(modelParameters));
            simulations[i] = getReticulado().setModelo(new Heitorzera2(population.get(i).getFirst())).run();
            ;
        }
        return simulations;
    }

    private int getMaxIndex(int[] array) {
        int maxIndex = 0;
        for (int i = 1; i < array.length; i++) {
            if (array[i] > array[maxIndex]) {
                maxIndex = i;
            }
        }
        return maxIndex;
    }

    private int[][][] getModeOfSimulations(int[][][][] simulations) {
        int[][][] mode = new int[QNT_ITERACOES][ALTURA][LARGURA];
        for (int i = 0; i < QNT_ITERACOES; i++) {
            for (int j = 0; j < ALTURA; j++) {
                for (int k = 0; k < LARGURA; k++) {
                    int[] count = new int[Estados.values().length];
                    for (int l = 0; l < simulations.length; l++) {
                        count[simulations[l][i][j][k]]++;
                    }
                    mode[i][j][k] = simulations[getMaxIndex(count)][i][j][k];
                }
            }
        }
        return mode;
    }

    private void startPopulation(int size) {
        for (int i = 0; i < size; i++) {
            ModelParameters modelParameters = new ModelParameters(
                    randomGenerator.nextDouble(),
                    randomGenerator.nextDouble(),
                    randomGenerator.nextDouble(),
                    randomGenerator.nextDouble(),
                    randomGenerator.nextDouble(),
                    randomGenerator.nextDouble(),
                    randomGenerator.nextDouble());
            population.add(new Tuple<>(modelParameters, INVALID_FITNESS));
        }
    }

    private Double compareOutputs(int[][][] simulation) {
        double fitness = cellWithFire.stream()
                .filter(cell -> Estados.isFogo(simulation[cell.getThird()][cell.getFirst()][cell.getSecond()]))
                .count();
        return fitness / cellWithFire.size();
    }

    private boolean isGenotypeBetween0_1(ModelParameters modelParameters) {
        Field[] fields = modelParameters.getClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            try {
                if ((double) field.get(modelParameters) < 0.0 || (double) field.get(modelParameters) > 1.0) {
                    return false;
                }
            } catch (IllegalAccessException e) {
                log.severe("Error while accessing field in ModelParameters");
            }
        }
        return true;
    }

    private boolean isGenotypeValid(ModelParameters modelParameters) {
        return isGenotypeBetween0_1(modelParameters) && areValuesInOrder(modelParameters);
    }

    private boolean areValuesInOrder(ModelParameters modelParameters) {
        return true;
        // return modelParameters.influenciaVegetacaoCampestre() <
        // modelParameters.influenciaVegetacaoFlorestal() &&
        // modelParameters.influenciaVegetacaoFlorestal() <
        // modelParameters.influenciaVegetacaoSavanica()
        // &&
        // modelParameters.probEspalhamentoFogoQueimaLenta() <
        // modelParameters.probEspalhamentoFogoInicial() &&
        // modelParameters.probEspalhamentoFogoInicial() <
        // modelParameters.probEspalhamentoFogoArvoreQueimando();
    }

    private Reticulado getReticulado() {
        return new Reticulado(new ReticuladoParameters(
                List.of(new Tuple<>(ALTURA / 2, LARGURA / 2)),
                ALTURA,
                LARGURA,
                0.5, // Tanto faz pois o que importa é o ModelParameters
                DirecoesVento.N,
                ReticuladoFactory.getMatrizEstadosDeEstadoInicial(Estados.SAVANICA, ALTURA, LARGURA),
                new GeradorLateral(),
                QNT_ITERACOES));
    }
}
