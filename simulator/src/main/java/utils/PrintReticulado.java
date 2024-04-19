package utils;

public class PrintReticulado {
    public static void printSimulation(int[][][] simulation) {
        for (int i = 0; i < simulation.length; i++) {
            for (int j = 0; j < simulation[0].length; j++) {
                for (int k = 0; k < simulation[0][0].length; k++) {
                    System.out.print(simulation[i][j][k] + " ");
                }
                System.out.println();
            }
            System.out.println();
        }
    }

    public static void printLastIterationOfSimulation(int[][][] simulation) {
        System.out.println("----------------------------");
        for (int i = 0; i < simulation[0].length; i++) {
            for (int j = 0; j < simulation[0][0].length; j++) {
                System.out.print(simulation[simulation.length - 1][i][j] + " ");
            }
            System.out.println();
        }
        System.out.println("----------------------------");
    }
}
