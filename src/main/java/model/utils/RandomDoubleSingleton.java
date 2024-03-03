package model.utils;

import java.util.Random;

public class RandomDoubleSingleton {
    private static RandomDoubleSingleton instance;
    private final Random random;

    private RandomDoubleSingleton(long seed) {
        random = new Random();
        random.setSeed(seed);
    }

    public static RandomDoubleSingleton getInstance(long seed) {
        if (instance == null) {
            instance = new RandomDoubleSingleton(seed);
        }
        return instance;
    }

    public double nextDouble() {
        return this.random.nextDouble();
    }
    public double nextDouble(double initial, double end) {
        return this.random.nextDouble() * (end - initial) + initial;
    }

    public int nextInt(int bound) {
        return this.random.nextInt(bound);
    }

    public double nextGaussian() {
        return this.random.nextGaussian();
    }
}
