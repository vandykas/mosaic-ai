import java.util.List;
import java.util.Random;

/**
 * Mengimplementasikan berbagai strategi crossover untuk Algoritma Genetika.
 * Digunakan untuk menggabungkan dua kromosom parent untuk menghasilkan keturunan.
 * <p>
 * <strong>Catatan:</strong> Beberapa metode crossover (One-Point, Two-Point, Uniform)
 * tersedia untuk menentukan mana yang menghasilkan konvergensi terbaik.
 *
 * @author Marco, Vandyka
 */
public class CrossoverStrategy {
    /** Generator random acak untuk mencari titik potong */
    private final Random random;

    /** Puzzle mosaic untuk pembuatan individu baru */
    private final Mosaic mosaic;

    /**
     * Membangun Strategi Crossover baru.
     *
     * @param random Generator angka acak.
     * @param mosaic Instance puzzle (konteks untuk operasi crossover).
     */
    public CrossoverStrategy(Random random, Mosaic mosaic) {
        this.random = random;
        this.mosaic = mosaic;
    }

    public Individu[] rowBasedCrossover(boolean[][] kromosom1, boolean[][] kromosom2) {
        boolean[][] child1 = new boolean[kromosom1.length][kromosom1.length];
        boolean[][] child2 = new boolean[kromosom2.length][kromosom2.length];

        int chromosomeLength = kromosom1.length;
        int crossoverPoint = random.nextInt(chromosomeLength);

        for (int i = 0; i < chromosomeLength; i++) {
            for (int j = 0; j < chromosomeLength; j++) {
                if (i < crossoverPoint) {
                    child1[i][j] = kromosom1[i][j];
                    child2[i][j] = kromosom2[i][j];
                }
                else {
                    child1[i][j] = kromosom2[i][j];
                    child2[i][j] = kromosom1[i][j];
                }
            }
        }
        return new Individu[]{new Individu(random, mosaic, child1), new Individu(random, mosaic, child2)};
    }

    public Individu[] colBasedCrossover(boolean[][] kromosom1, boolean[][] kromosom2) {
        boolean[][] child1 = new boolean[kromosom1.length][kromosom1.length];
        boolean[][] child2 = new boolean[kromosom2.length][kromosom2.length];

        int chromosomeLength = kromosom1.length;
        int crossoverPoint = random.nextInt(chromosomeLength);

        for (int i = 0; i < chromosomeLength; i++) {
            for (int j = 0; j < chromosomeLength; j++) {
                if (j < crossoverPoint) {
                    child1[i][j] = kromosom1[i][j];
                    child2[i][j] = kromosom2[i][j];
                }
                else {
                    child1[i][j] = kromosom2[i][j];
                    child2[i][j] = kromosom1[i][j];
                }
            }
        }
        return new Individu[]{new Individu(random, mosaic, child1), new Individu(random, mosaic, child2)};
    }

    public Individu[] rowAndColBasedCrossover(boolean[][] kromosom1, boolean[][] kromosom2) {
        boolean[][] child1 = new boolean[kromosom1.length][kromosom1.length];
        boolean[][] child2 = new boolean[kromosom2.length][kromosom2.length];

        int chromosomeLength = kromosom1.length;
        int rowCrossoverPoint = random.nextInt(chromosomeLength);
        int colCrossoverPoint = random.nextInt(chromosomeLength);

        for (int i = 0; i < chromosomeLength; i++) {
            for (int j = 0; j < chromosomeLength; j++) {
                if (i <= rowCrossoverPoint && j <= colCrossoverPoint ||
                        i > rowCrossoverPoint && j > colCrossoverPoint) {
                    child1[i][j] = kromosom1[i][j];
                    child2[i][j] = kromosom2[i][j];
                }
                else {
                    child1[i][j] = kromosom2[i][j];
                    child2[i][j] = kromosom1[i][j];
                }
            }
        }
        return new Individu[]{new Individu(random, mosaic, child1), new Individu(random, mosaic, child2)};
    }

    public Individu[] subGridBasedCrossover(boolean[][] kromosom1, boolean[][] kromosom2) {
        boolean[][] child1 = new boolean[kromosom1.length][kromosom1.length];
        boolean[][] child2 = new boolean[kromosom2.length][kromosom2.length];

        int chromosomeLength = kromosom1.length;
        int r1 = random.nextInt(chromosomeLength);
        int r2 = random.nextInt(r1, chromosomeLength);
        int c1 = random.nextInt(chromosomeLength);
        int c2 = random.nextInt(c1, chromosomeLength);

        for (int i = 0; i < chromosomeLength; i++) {
            for (int j = 0; j < chromosomeLength; j++) {
                if (i >= r1 && i <= r2 && j >= c1 && j <= c2) {
                    child1[i][j] = kromosom2[i][j];
                    child2[i][j] = kromosom1[i][j];
                }
                else {
                    child1[i][j] = kromosom1[i][j];
                    child2[i][j] = kromosom2[i][j];
                }
            }
        }
        return new Individu[]{new Individu(random, mosaic, child1), new Individu(random, mosaic, child2)};
    }

    public Individu[] uniformCrossover(boolean[][] kromosom1, boolean[][] kromosom2, List<Cell> unknownCells) {
        boolean[][] child1 = new boolean[kromosom1.length][kromosom1.length];
        boolean[][] child2 = new boolean[kromosom2.length][kromosom2.length];

        int chromosomeLength = kromosom1.length;
        for (Cell cell : unknownCells) {
            int i = cell.row();
            int j = cell.col();
            if (random.nextDouble() < 0.5) {
                child1[i][j] = kromosom1[i][j];
                child2[i][j] = kromosom2[i][j];
            }
            else {
                child1[i][j] = kromosom2[i][j];
                child2[i][j] = kromosom1[i][j];
            }
        }
        return new Individu[]{new Individu(random, mosaic, child1), new Individu(random, mosaic, child2)};
    }
}
