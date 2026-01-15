import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Merepresentasikan satu individu (kandidat solusi) dalam populasi.
 * Setiap individu memiliki kromosom (array boolean) yang merepresentasikan
 * status (Hitam/Putih) dari sel-sel yang tidak diketahui pada grid Mosaic.
 *
 * @author Marco, Vandyka
 */
public class Individu implements Comparable<Individu> {
    /** Generator random digunakan untuk pembuatan kromosom */
    private final Random random;
    /** Objek mosaic yang berisi segala hal berkaitan puzzle Mosaic */
    private final Mosaic mosaic;
    /** Encoding kromosom individu dalam bitstring 2D */
    private final boolean[][] kromosom;
    /** Nilai fitness individu */
    private double fitness;

    /**
     * Membangun individu dengan kromosom tertentu.
     *
     * @param random Generator angka acak.
     * @param mosaic Instance puzzle.
     * @param kromosom Array boolean yang merepresentasikan solusi.
     */
    public Individu(Random random, Mosaic mosaic, boolean[][] kromosom) {
        this.random = random;
        this.mosaic = mosaic;
        this.kromosom = new boolean[kromosom.length][];
        for (int i = 0; i < kromosom.length; i++) {
            this.kromosom[i] = Arrays.copyOf(kromosom[i], kromosom[i].length);
        }
    }

    /**
     * Membangun individu baru dengan kromosom kosong (untuk diinisialisasi nantinya).
     *
     * @param random Generator angka acak.
     * @param mosaic Instance puzzle.
     */
    public Individu(Random random, Mosaic mosaic) {
        this.random = random;
        this.mosaic = mosaic;
        this.kromosom = new boolean[mosaic.getUkuran()][mosaic.getUkuran()];
    }

    /**
     * Membandingkan nilai fitness dua individu.
     * Individu dengan nilai fitness lebih besar akan ditempatkan di awal.
     */
    @Override
    public int compareTo(Individu o) {
        return Double.compare(o.fitness, this.fitness);
    }

    public boolean[][] getKromosom() {
        return this.kromosom;
    }

    public void calculateFitness() {
        this.fitness = mosaic.fitnessFunction(kromosom);
    }

    public void calculateFitnessWithDiversity(double alpha, double[] average) {
        this.fitness = mosaic.fitnessFunctionWithDiversity(kromosom, average, alpha);
    }

    public double getFitness() {
        return fitness;
    }

    /**
     * Inisialisasi kromosom untuk setiap cell yang tidak diketahui warnanya.
     * Setiap cell punya peluang 50% menjadi hitam atau tetap putih.
     */
    public void initKromosom() {
        for (Cell cell : mosaic.getUnknownCells()) {
            kromosom[cell.row()][cell.col()] = random.nextDouble() < 0.5;
        }
    }

    /**
     * Inisialisasi kromosom dengan hasil heuristik deterministik.
     */
    public void initKromosomWithHeuristic() {
        CellState[][] partialSolution = mosaic.getPartialSolution();
        for (int i = 0; i < partialSolution.length; i++) {
            for (int j = 0; j < partialSolution.length; j++) {
                kromosom[i][j] = partialSolution[i][j] == CellState.BLACK;
            }
        }
    }

    /**
     * Inisialisasi kromosom dengan heuristik peluang. Setiap cell memiliki
     * peluang berbeda agar bisa menjadi hitam.
     */
    public void initKromosomWithProbability() {
        List<Cell> unknownCells = mosaic.getUnknownCells();
        for (int i = 0; i < unknownCells.size(); i++) {
            Cell cell = unknownCells.get(i);
            kromosom[cell.row()][cell.col()] = random.nextDouble() < mosaic.getUnknownCellsProb(i);
        }
    }

    /**
     * Untuk cell yang tidak diketahui warnanya, memiliki peluang kecil
     * untuk bermutasi sehingga menambah eksplorasi.
     *
     * @param mutationRate Peluang mutasi terjadi
     */
    public void mutasi(double mutationRate) {
        for (Cell cell : mosaic.getUnknownCells()) {
            if (random.nextDouble() < mutationRate) {
                kromosom[cell.row()][cell.col()] = !kromosom[cell.row()][cell.col()];
            }
        }
    }

    /**
     * Crossover dengan teknik membelah kromosom secara horizontal.
     *
     * @param pasangan Pasangan individu untuk disilangkan
     * @return Array berisi 2 individu hasil persilangan
     */
    public Individu[] rowBasedCrossover(Individu pasangan) {
        CrossoverStrategy crossoverStrategy = new CrossoverStrategy(random, mosaic);
        return crossoverStrategy.rowBasedCrossover(this.kromosom, pasangan.getKromosom());
    }

    /**
     * Crossover dengan teknik membelah kromosom secara vertikal.
     *
     * @param pasangan Pasangan individu untuk disilangkan
     * @return Array berisi 2 individu hasil persilangan
     */
    public Individu[] colBasedCrossover(Individu pasangan) {
        CrossoverStrategy crossoverStrategy = new CrossoverStrategy(random, mosaic);
        return crossoverStrategy.colBasedCrossover(this.kromosom, pasangan.getKromosom());
    }

    /**
     * Crossover dengan teknik gabungan row dan column.
     *
     * @param pasangan Pasangan individu untuk disilangkan
     * @return Array berisi 2 individu hasil persilangan
     */
    public Individu[] rowAndColBasedCrossover(Individu pasangan) {
        CrossoverStrategy crossoverStrategy = new CrossoverStrategy(random, mosaic);
        return crossoverStrategy.rowAndColBasedCrossover(this.kromosom, pasangan.getKromosom());
    }

    /**
     * Crossover dengan teknik membuat sub grid. Area luar sub grid di isi kromosom
     * parent pertama dan isi sub grid di isi kromosom parent kedua/
     *
     * @param pasangan Pasangan individu untuk disilangkan
     * @return Array berisi 2 individu hasil persilangan
     */
    public Individu[] subGridBasedCrossover(Individu pasangan) {
        CrossoverStrategy crossoverStrategy = new CrossoverStrategy(random, mosaic);
        return crossoverStrategy.subGridBasedCrossover(this.kromosom, pasangan.getKromosom());
    }

    /**
     * Crossover dengan teknik uniform dimana setiap cell memiliki peluang sama untuk
     * berganti warna.
     *
     * @param pasangan Pasangan individu untuk disilangkan
     * @return Array berisi 2 individu hasil persilangan
     */
    public Individu[] uniformCrossover(Individu pasangan) {
        CrossoverStrategy crossoverStrategy = new CrossoverStrategy(random, mosaic);
        return crossoverStrategy.uniformCrossover(this.kromosom, pasangan.getKromosom(), mosaic.getUnknownCells());
    }
}