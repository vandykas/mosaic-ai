import java.io.File;
import java.io.FileNotFoundException;
import java.util.Locale;
import java.util.Scanner;

/**
 * Main Class untuk penyelesai Mosaic AI.
 * Kelas ini menangani input file, menginisialisasi puzzle dan konfigurasi,
 * serta mengatur proses penyelesaian menggunakan Heuristik dan Algoritma Genetika.
 *
 * @author TODO to be filled
 */
public class Main {

    /**
     * Titik masuk utama aplikasi.
     * Mengharapkan dua argumen baris perintah: jalur ke file hyperparameter dan jalur ke file input mosaic.
     *
     * @param args Argumen baris perintah. args[0] adalah file hyperparam, args[1] adalah file input.
     */
    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Penggunaan: java Main hyperparam.txt input.txt");
            return;
        }

        File fileHyperparameter = new File(args[0]);
        File fileInput = new File(args[1]);

        try {
            Scanner sc = new Scanner(fileInput);
            sc.useLocale(Locale.US);
            Mosaic mosaic = readAndMakeMosaic(sc);
            mosaic.runHeuristic();

            // Membaca hyperparameter
            sc = new Scanner(fileHyperparameter);
            GAConfig config = readAndMakeGAConfig(sc);
            sc.close();

            GA algoritmaGenetika = new GA(mosaic, config);

            if (mosaic.getUnknownCellsSize() == 0) {
                System.out.println("Diselesaikan heuristic");
                mosaic.printHeuristicSolution();
            }
            else {
                mosaic.createUnknownCellsProbability();
                algoritmaGenetika.run();
                System.out.println("Hasil heuristik single point");
                System.out.println("Banyak cell unknown: " + mosaic.getUnknownCellsSize());
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

    private static GAConfig readAndMakeGAConfig(Scanner sc) {
        int maxGeneration = sc.nextInt();
        int maxPopulationSize = sc.nextInt();
        double mutationRate = sc.nextDouble();
        double elitismRate = sc.nextDouble();
        double crossoverRate = sc.nextDouble();
        double convergenceThreshold = sc.nextDouble();
        int convergenceWindow = sc.nextInt();
        double heuristicRate = sc.nextDouble();
        double alphaStart = sc.nextDouble();
        int repetisi = sc.nextInt();
        return new GAConfig(maxPopulationSize, mutationRate, elitismRate, crossoverRate, maxGeneration,
                convergenceThreshold, convergenceWindow, heuristicRate, alphaStart, repetisi);
    }
}