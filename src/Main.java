import java.io.File;
import java.io.FileNotFoundException;
import java.util.Locale;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Penggunaan: java Main hyperparam.txt input.txt");
            return;
        }

        File fileHyperparameter = new File(args[0]);
        File fileInput = new File(args[1]);

        try {
            // Membaca input puzzle
            Scanner sc = new Scanner(fileInput);
            sc.useLocale(Locale.US);
            Mosaic mosaic = readAndMakeMosaic(sc);
            mosaic.runHeuristic();

            // Membaca hyperparameter
            sc = new Scanner(fileHyperparameter);
            int maxPopulationSize = sc.nextInt();
            double mutationRate = sc.nextDouble();
            double elitismRate = sc.nextDouble();
            int maxGeneration = sc.nextInt();
            double convergenceThreshold = sc.nextDouble();
            int convergenceWindow = sc.nextInt();
            int repetisi = sc.nextInt();
            sc.close();

            GA algoritmaGenetika = new GA(
                    mosaic,
                    maxPopulationSize,
                    mutationRate,
                    elitismRate,
                    maxGeneration,
                    convergenceThreshold,
                    convergenceWindow
            );

            if (mosaic.getUnknownCellsSize() == 0) {
                System.out.println("Diselesaikan heuristic");
                mosaic.printHeuristicSolution();
            }
            else {
                algoritmaGenetika.run(repetisi);
                mosaic.printHeuristicSolution();
            }
        }
        catch (FileNotFoundException e) {
            System.out.println("File tidak ditemukan: " + e.getMessage());
        }
    }

    private static Mosaic readAndMakeMosaic(Scanner sc) {
        int ukuranGrid = sc.nextInt();
        int[][] clue = new int[ukuranGrid][ukuranGrid];
        for (int i = 0; i < ukuranGrid; i++) {
            for (int j = 0; j < ukuranGrid; j++) {
                clue[i][j] = sc.nextInt();
            }
        }
        return new Mosaic(ukuranGrid, clue);
    }
}