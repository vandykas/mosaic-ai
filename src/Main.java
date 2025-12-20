import java.io.*;
import java.util.*;

/**
 * Kelas utama untuk menjalankan algoritma genetika pada puzzle Mosaic
 */
public class Main {
    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Penggunaan: java Main hyperparam.txt input.txt");
            return;
        }

        File fileHyperparameter = new File(args[0]);
        File fileInput = new File(args[1]);

        try {
            // Membaca hyperparameter
            Scanner sc = new Scanner(fileHyperparameter);
            sc.useLocale(Locale.US);
            int populasi = sc.nextInt();
            double mutation_rate = sc.nextDouble();
            double elitism_rate = sc.nextDouble();
            int max_generation = sc.nextInt();
            double convergence_treshold = sc.nextDouble();
            int convergence_window = sc.nextInt();
            int repetisi = sc.nextInt();
            sc.close();

            // Membaca input puzzle
            sc = new Scanner(fileInput);
            
            int ukuranGrid = sc.nextInt();
            int[][] clue = new int[ukuranGrid][ukuranGrid];
            sc.useLocale(Locale.US);
            for (int i = 0; i < ukuranGrid; i++) {
                for (int j = 0; j < ukuranGrid; j++) {
                    clue[i][j] = sc.nextInt();
                }
            }
            sc.close();

            // Membuat objek Mosaic
            Mosaic mosaic = new Mosaic(ukuranGrid, clue);
            
            // Menjalankan GA untuk setiap repetisi
            for (int r = 0; r < repetisi; r++) {
                System.out.println("Repetisi ke-" + (r + 1));
                
                // Membuat objek GA dengan seed sesuai repetisi
                GA algoritmaGenetika = new GA(
                    mosaic,
                    populasi,
                    mutation_rate,
                    elitism_rate,
                    max_generation,
                    convergence_treshold,
                    convergence_window,
                    r // r ini jadi seed
                );
                
                // Menjalankan algoritma genetika
                algoritmaGenetika.jalankan();
                
                // Mendapatkan solusi terbaik
                Individu solusiTerbaik = algoritmaGenetika.getIndividuTerbaik();
                System.out.println("Fitness terbaik: " + solusiTerbaik.getFitness());
            }

        } catch (FileNotFoundException e) {
            System.out.println("File tidak ditemukan: " + e.getMessage());
        }
    }
}