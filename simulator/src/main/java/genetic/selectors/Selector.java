package genetic.selectors;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import genetic.GeneticAlgorithmParams;
import model.modelos.ModelParameters;
import utils.Tuple;

public interface Selector {
    List<Tuple<ModelParameters, Double>> select(List<Tuple<ModelParameters, Double>> population);

    public enum Type {
        TOURNAMENT,
        ROULETTE,
        ELITISM,
        RANDOM,
        REVERSE_ELITISM_WITH_TOURNAMENT;

        public static List<Type> getAllExceptRandom() {
            List<Type> allTypes = new ArrayList<>(Arrays.asList(values()));
            allTypes.remove(RANDOM);
            return allTypes;
        }
    }

    static Selector getSelector(Type type, GeneticAlgorithmParams params) {
        return switch (type) {
            case TOURNAMENT -> new TournamentSelector(params);
            case ROULETTE -> new RouletteSelector(params);
            case ELITISM -> new ElitismSelector(params);
            case RANDOM -> new RandomSelector(params);
            case REVERSE_ELITISM_WITH_TOURNAMENT -> new ReverseElitismWithTournamentSelector(params);
            default -> new ElitismSelector(params);
        };
    }
}
