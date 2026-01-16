import java.util.ArrayList;
import java.util.Collections;

import java.util.List;
import java.util.Random;

/**
 * Mengelola populasi individu untuk Algoritma Genetika.
 * Menangani inisialisasi, elitisme, pembaruan probabilitas, dan pengurutan berdasarkan fitness.
 *
 * @author Marco, Vandyka
 */
public class Populasi {
    /** Objek yang menyimpan konteks puzzle mosaic */
    private final Mosaic mosaic;
    /** Generator angka random */
    private final Random random;
    /** Maksimal individu dalam sebuah populasi */
    private final int maxPopulationSize;
    /** List individu dalam populasi */
    private final List<Individu> population;
    /** Rata-rate cell hitam dalam populasi */
    private final double[] average;

    /**
     * Membangun Populasi baru.
     *
     * @param maxPopulationSize Jumlah maksimum individu yang diizinkan.
     * @param mosaic Instance puzzle.
     * @param random Generator angka acak.
     */
    public Populasi(int maxPopulationSize, Mosaic mosaic, Random random) {
        this.mosaic = mosaic;
        this.random = random;
        this.maxPopulationSize = maxPopulationSize;
        this.population = new ArrayList<>();
        this.average = new double[mosaic.getUnknownCellsSize()];
    }

    public int getPopulationSize() {
        return population.size();
    }

    public Individu getIndividuTerbaik() {
        return population.get(0);
    }

    /**
     * Inisialisasi populasi dengan sebagiannya menggunakan heuristik dan sisanya tidak.
     * Hanya sebagian yang menggunakan heuristik untuk mencegah terlalu banyak eksploitasi
     * pada awal populasi.
     *
     * @param heuristicRate Persentase populasi yang menggunakan heuristik peluang
     */
    public void initPopulasi(double heuristicRate) {
        int individuWithHeuristic = (int) (heuristicRate * maxPopulationSize);
        for (int i = 0; i < individuWithHeuristic; i++) {
            Individu individu = new Individu(random, mosaic);
            individu.initKromosomWithHeuristic();
            individu.initKromosomWithProbability();
            addIndividu(individu);
        }

        for (int i = 0; i < maxPopulationSize - individuWithHeuristic; i++) {
            Individu individu = new Individu(random, mosaic);
            individu.initKromosomWithHeuristic();
            individu.initKromosom();
            addIndividu(individu);
        }
    }

    /**
     * Individu terkuat akan bertahan dan dibawa ke populasi selanjutnya.
     *
     * @param elitismRate Persentase elitism akan dibawa ke generasi selanjutnya
     * @return Populasi yang sudah diisi sebagian oleh elitism
     */
    public Populasi initPopulasiWithElitism(double elitismRate) {
        Populasi nextPop = new Populasi(maxPopulationSize, mosaic, random);
        int elitismCount = (int) (maxPopulationSize * elitismRate);
        for (int i = 0; i < elitismCount; i++) {
            nextPop.addIndividu(new Individu(
                    random, mosaic, population.get(i).getKromosom()
            ));
        }
        return nextPop;
    }

    /**
     * Mengisi rata-rata sebuah gene hitam berdasarkan populasi dan akan digunakan untuk
     * menghitung diversity sebuah kromosom.
     */
    public void fillAverage() {
        List<Cell> unknownCells = mosaic.getUnknownCells();
        for (Individu individu : population) {
            boolean[][] kromosom = individu.getKromosom();
            for (int i = 0; i < unknownCells.size(); i++) {
                Cell cell = unknownCells.get(i);
                average[i] += kromosom[cell.row()][cell.col()] ? 1 : 0;
            }
        }

        for (int i = 0; i < average.length; i++) {
            average[i] /= maxPopulationSize;
        }
    }

    public void addIndividu(Individu individu) {
        this.population.add(individu);
    }

    /**
     * Menghitung semua fitness individu dalam populasi dengan fungsi fitness
     * tanpa mempertimbangkan diversity
     */
    public void calculatePopulationFitness() {
        for (Individu individu : population) {
            individu.calculateFitness();
        }
    }

    /**
     * Menghitung semua fitness individu dalam populasi dengan fungsi fitness
     * yang mempertimbangkan diversity.
     *
     * @param alpha Bobot pertimbangan nilai diversity
     */
    public void calculatePopulationFitnessWithDiversity(double alpha) {
        for (Individu individu : population) {
            individu.calculateFitnessWithDiversity(alpha, average);
        }
    }

    /**
     * Menghitung rata-rata fitness sebuah populasi untuk pengecekan konvergensi.
     *
     * @return Rata-rata fitness populasi
     */
    public double hitungFitnessRataRata() {
        double total = 0;
        for (Individu individu : population) {
            total += individu.getFitness();
        }
        return total / maxPopulationSize;
    }

    /**
     * Mengurutkan individu berdasarkan nilai fitness dari terbesar ke
     * terkecil.
     */
    public void sortPopulation() {
        Collections.sort(population);
    }

    /**
     * Memilih satu individu dari populasi menggunakan strategi seleksi roulette wheel.
     *
     * <p>Proses seleksi dilakukan oleh {@link SelectionStrategy} berdasarkan nilai fitness
     * masing-masing individu.</p>
     *
     * @return Individu terpilih dari populasi
     */
    public Individu seleksiRoulette() {
        SelectionStrategy selectionStrategy = new SelectionStrategy(random, population);
        return selectionStrategy.seleksiRoulette();
    }

    /**
     * Memilih satu individu dari populasi menggunakan strategi seleksi berbasis peringkat (rank).
     *
     * <p>Individu dengan peringkat lebih tinggi memiliki peluang seleksi yang lebih besar.
     * Detail perhitungan dilakukan pada {@link SelectionStrategy}.</p>
     *
     * @return Individu terpilih dari populasi
     */
    public Individu seleksiRank() {
        SelectionStrategy selectionStrategy = new SelectionStrategy(random, population);
        return selectionStrategy.seleksiRank();
    }

    /**
     * Memilih satu individu dari populasi menggunakan strategi seleksi turnamen.
     *
     * <p>Sejumlah individu dipilih secara acak untuk mengikuti turnamen, kemudian individu
     * terbaik dari turnamen tersebut dipilih sebagai hasil seleksi.</p>
     *
     * @param ukuranTurnamen Jumlah individu yang diikutkan dalam satu turnamen
     * @return Individu terpilih dari populasi
     */
    public Individu seleksiTournament(int ukuranTurnamen) {
        SelectionStrategy selectionStrategy = new SelectionStrategy(random, population);
        return selectionStrategy.seleksiTournament(ukuranTurnamen);
    }
}