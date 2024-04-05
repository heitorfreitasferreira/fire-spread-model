package genetic.reproductors;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import genetic.EvolutiveStrategy;
import genetic.GeneticAlgorithmParams;
import lombok.extern.java.Log;
import model.modelos.ModelParameters;
import model.utils.RandomDoubleSingleton;
import model.utils.Tuple;

@Log
public class ReprodutorSexuado implements Reproductor {
    private GeneticAlgorithmParams params;
    private RandomDoubleSingleton randomGenerator;

    public ReprodutorSexuado(GeneticAlgorithmParams params) {
        this.params = params;
        this.randomGenerator = RandomDoubleSingleton.getInstance();
    }

    public List<Tuple<ModelParameters, Double>> reproduzir(List<Tuple<ModelParameters, Double>> pais) {
        List<Tuple<ModelParameters, Double>> filhos = new ArrayList<>();
        for (int i = 0; i < params.populationSize(); i++) {
            ModelParameters parent1 = pais.get(randomGenerator.nextInt(params.populationSize())).getFirst();
            ModelParameters parent2 = pais.get(randomGenerator.nextInt(params.populationSize())).getFirst();
            filhos.add(new Tuple<>(reproduce(parent1, parent2), EvolutiveStrategy.INVALID_FITNESS));
        }
        return filhos;
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
        boolean shouldMutate = randomGenerator.nextDouble() < params.mutationProb();
        if (!shouldMutate)
            return;
        int alleleToMutate = randomGenerator.nextInt(childValues.length);
        childValues[alleleToMutate] = mutateAllele(childValues[alleleToMutate]);
    }

    private double mutateAllele(double allele) {
        return allele + randomGenerator.nextDouble(-params.mutationRate(), params.mutationRate());
    }

    private double crossOverBlxAlpha(double a, double b) {
        double min = Math.min(a, b);
        double max = Math.max(a, b);
        double I = max - min;
        double beta = randomGenerator.nextDouble(-params.crossoverBlxAlpha(), 1 + params.crossoverBlxAlpha());
        return a + I * beta;
    }

}
