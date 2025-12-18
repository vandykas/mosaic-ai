import java.io.File;
import java.io.FileNotFoundException;
import java.util.Random;
import java.util.Scanner;

public class MainGA {
    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Masukkan file parameter dan input sebagai argument!");
            return;
        }

        File param = new File(args[0]);
        File puzzle = new File(args[1]);
        try {
            Scanner sc = new Scanner(param);
            int totalGeneration = sc.nextInt();
            int maxPopulationSize = sc.nextInt();
            double crossoverRate = sc.nextDouble();
            double mutationRate = sc.nextDouble();
            double elitismPct = sc.nextDouble();
            int convergence_window = sc.nextInt();
            double convergence_treshold = sc.nextDouble();

            sc = new Scanner(puzzle);
            int n = sc.nextInt();
            int m = sc.nextInt();
            Integer[][] grid = new Integer[n][m];
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < m; j++) {
                    grid[i][j] = sc.nextInt();
                }
            }

            Mosaic mosaic = new Mosaic(m, n, grid);
            doGenAlgo(mosaic, totalGeneration, maxPopulationSize, crossoverRate, mutationRate, elitismPct, convergence_window, convergence_treshold);
        }
        catch (FileNotFoundException e) {
            System.out.println("File " + args[0] + " not found!");
        }
    }

    /*
    Melakukan algoritma genetik dan menyimpan individu terbaik yang ditemukan
     */
    public static void doGenAlgo(Mosaic fireStation, int totalGeneration, int maxPopulationSize,
                                 double crossoverRate, double mutationRate, double elitismPct, int convergence_window, double convergence_treshold) {
        GA myGeneticAlgo = new GA(fireStation, totalGeneration, maxPopulationSize, crossoverRate, mutationRate, elitismPct, new Random(), convergence_window, convergence_treshold);
        Individual result = myGeneticAlgo.runGenAlgo();
        result.printResult(fireStation.getEmptyPosition());
    }
}

