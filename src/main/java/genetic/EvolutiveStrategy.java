package genetic;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import lombok.extern.java.Log;
import model.estados.Estados;
import model.modelos.Heitorzera2;
import model.modelos.ModelParameters;
import model.reticulado.Reticulado;
import model.reticulado.ReticuladoFactory;
import model.reticulado.ReticuladoParameters;
import model.terreno.GeradorLateral;
import model.utils.RandomDoubleSingleton;
import model.utils.Tuple;
import model.vento.DirecoesVento;

@Log
public class EvolutiveStrategy {
    public static final Double ZERO_FITNESS = 0.0;
    int[][][] objectiveSimulation;
    List<Tuple<ModelParameters, Double>> population;
    Statistics statistics;
    final int POPULATION_SIZE;
    private final RandomDoubleSingleton randomGenerator;
    private final double INVALID_FITNESS = -1.0;
    private final double MUTATION_RATE;
    private final double MUTATION_PROB;
    private final int geracoes;
    private final int TOURNAMENT_K;
    private final int ALTURA;
    private final int QNT_ITERACOES;
    private final int LARGURA;
    private final double CROSSOVER_RATE;
    private final double blxAlpha;

    private final GeneticAlgorithmParams params;

    public EvolutiveStrategy(int[][][] objectiveSimulation, GeneticAlgorithmParams params,
            ReticuladoParameters reticuladoParameters) {
        this.POPULATION_SIZE = params.populationSize();
        this.MUTATION_RATE = params.mutationRate();
        this.TOURNAMENT_K = params.tournamentSize();
        this.ALTURA = objectiveSimulation[0].length;
        this.LARGURA = objectiveSimulation[0][0].length;
        this.geracoes = params.numberOfGenerations();
        this.CROSSOVER_RATE = params.crossoverRate();
        this.objectiveSimulation = objectiveSimulation;
        this.randomGenerator = RandomDoubleSingleton.getInstance();
        this.QNT_ITERACOES = reticuladoParameters.QNT_ITERACOES();
        this.blxAlpha = params.crossoverBlxAlpha();
        this.MUTATION_PROB = params.mutationProb();
        this.params = params;

        population = new ArrayList<>(POPULATION_SIZE);

        this.statistics = new Statistics();

        this.startPopulation(POPULATION_SIZE);
    }

    public void evolve() {
        long start = System.currentTimeMillis();
        calculateFitness();
        for (int i = 0; i < geracoes; i++) {
            log.info("Evolving generation ... " + (float) i * 100 / geracoes + "%");
            stepOneGeneration();
            statistics.updateStatistics(population);
        }
        long end = System.currentTimeMillis();
        System.out.println("Evolution finished in " + (end - start) / 1000 + " seconds.");
        statistics.logToFile(params);
    }

    private void stepOneGeneration() {
        reproduceSexually();
        calculateFitness();
        selectByTournament();
    }

    private void selectByTournament() {
        List<Tuple<ModelParameters, Double>> newPopulation = new ArrayList<>(POPULATION_SIZE);
        for (int i = 0; i < POPULATION_SIZE; i++) {
            List<Tuple<ModelParameters, Double>> tournament = new ArrayList<>();
            for (int j = 0; j < TOURNAMENT_K; j++) {
                tournament.add(population.get(randomGenerator.nextInt(POPULATION_SIZE)));
            }
            tournament.sort((a, b) -> b.getSecond().compareTo(a.getSecond()));
            var bestIndividual = tournament.get(0);

            newPopulation.add(bestIndividual);
        }
        population = newPopulation;
    }

    private void reproduceSexually() {
        for (int i = 0; i < POPULATION_SIZE; i++) {
            ModelParameters parent1 = population.get(randomGenerator.nextInt(POPULATION_SIZE)).getFirst();
            ModelParameters parent2 = population.get(randomGenerator.nextInt(POPULATION_SIZE)).getFirst();
            population.add(new Tuple<>(reproduce(parent1, parent2), INVALID_FITNESS));
        }
    }

    private ModelParameters reproduce(ModelParameters parent1, ModelParameters parent2) {
        Field[] p1Fields = parent1.getClass().getDeclaredFields();
        Field[] p2Fields = parent2.getClass().getDeclaredFields();
        double[] childValues = new double[p1Fields.length];
        for (int i = 0; i < p1Fields.length; i++) {
            p1Fields[i].setAccessible(true);
            p2Fields[i].setAccessible(true);
            try {
                double allele = this.crossOverBlxAlpha((double) p1Fields[i].get(parent1),
                        (double) p2Fields[i].get(parent2));
                childValues[i] = allele;
            } catch (IllegalAccessException e) {
                log.severe("Error while accessing field in ModelParameters");
                return new ModelParameters(0, 0, 0, 0, 0, 0, 0);
            }
        }
        mutateChromosome(childValues);
        return new ModelParameters(childValues);
    }

    private void mutateChromosome(double[] childValues) {
        boolean shouldMutate = randomGenerator.nextDouble() < MUTATION_PROB;
        if (!shouldMutate)
            return;
        int alleleToMutate = randomGenerator.nextInt(childValues.length);
        childValues[alleleToMutate] = mutateAllele(childValues[alleleToMutate]);
    }

    private double mutateAllele(double allele) {
        return allele + randomGenerator.nextDouble(-MUTATION_RATE, MUTATION_RATE);
    }

    private double crossOverBlxAlpha(double a, double b) {
        double min = Math.min(a, b);
        double max = Math.max(a, b);
        double I = max - min;
        double beta = randomGenerator.nextDouble(-blxAlpha, 1 + blxAlpha);
        return a + I * beta;
    }

    private void reproduceAsexually() {
        for (int i = 0; i < POPULATION_SIZE; i++) {
            ModelParameters modelParameters = population.get(i).getFirst();
            ModelParameters newModelParameters = new ModelParameters(
                    modelParameters.influenciaUmidade() + randomGenerator.nextDouble(-MUTATION_RATE, MUTATION_RATE),
                    modelParameters.probEspalhamentoFogoInicial()
                            + randomGenerator.nextDouble(-MUTATION_RATE, MUTATION_RATE),
                    modelParameters.probEspalhamentoFogoArvoreQueimando()
                            + randomGenerator.nextDouble(-MUTATION_RATE, MUTATION_RATE),
                    modelParameters.probEspalhamentoFogoQueimaLenta()
                            + randomGenerator.nextDouble(-MUTATION_RATE, MUTATION_RATE),
                    modelParameters.influenciaVegetacaoCampestre()
                            + randomGenerator.nextDouble(-MUTATION_RATE, MUTATION_RATE),
                    modelParameters.influenciaVegetacaoSavanica()
                            + randomGenerator.nextDouble(-MUTATION_RATE, MUTATION_RATE),
                    modelParameters.influenciaVegetacaoFlorestal()
                            + randomGenerator.nextDouble(-MUTATION_RATE, MUTATION_RATE));
            population.add(new Tuple<>(newModelParameters, INVALID_FITNESS));
        }
    }

    private void calculateFitness() {
        population.parallelStream().forEach(individual -> {
            if (individual.getSecond() != INVALID_FITNESS)
                return;

            ModelParameters modelParameters = individual.getFirst();
            if (!isGenotypeValid(modelParameters)) {
                individual.setSecond(ZERO_FITNESS); // Penalize invalid individuals
                return;
            }
            Reticulado reticulado = getReticulado();
            reticulado.setModelo(new Heitorzera2(modelParameters));
            int[][][] simulation = reticulado.run();
            Double fitness = compareOutputs(simulation);
            individual.setSecond(fitness);
        });
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
        float fitness = 0;
        var reticulado = getReticulado();
        float numberOfCells = (float) reticulado.getAltura() * reticulado.getLargura() * QNT_ITERACOES;
        for (int i = 0; i < reticulado.getAltura(); i++) {
            for (int j = 0; j < reticulado.getLargura(); j++) {
                for (int k = 0; k < QNT_ITERACOES; k++) {
                    if (simulation[k][i][j] == objectiveSimulation[k][i][j]) {
                        fitness++;
                    }
                }
            }
        }
        return (double) (fitness / numberOfCells);
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
        return modelParameters.influenciaVegetacaoCampestre() < modelParameters.influenciaVegetacaoFlorestal() &&
                modelParameters.influenciaVegetacaoFlorestal() < modelParameters.influenciaVegetacaoSavanica()
                &&
                modelParameters.probEspalhamentoFogoQueimaLenta() < modelParameters.probEspalhamentoFogoInicial() &&
                modelParameters.probEspalhamentoFogoInicial() < modelParameters.probEspalhamentoFogoArvoreQueimando();
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
