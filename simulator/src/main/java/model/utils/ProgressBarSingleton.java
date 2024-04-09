package model.utils;

import me.tongfei.progressbar.ProgressBar;

public class ProgressBarSingleton {
    private static ProgressBarSingleton instance = null;
    private ProgressBar progressBar;

    private ProgressBarSingleton(int max) {
        this.progressBar = new ProgressBar("Progress", max);
    }

    public static ProgressBarSingleton getInstance(int max) {
        if (instance == null) {
            instance = new ProgressBarSingleton(max);
        }
        return instance;
    }

    public void step() {
        if (instance == null) {
            return;
        }
        progressBar.step();
    }

    public void close() {
        progressBar.close();
    }

    public void reset() {
        progressBar.reset();
    }

    public void stepBy(int step) {
        progressBar.stepBy(step);
    }

    public void setMax(int max) {
        progressBar.maxHint(max);
    }

    public void setExtraMessage(String message) {
        progressBar.setExtraMessage(message);
    }

}
