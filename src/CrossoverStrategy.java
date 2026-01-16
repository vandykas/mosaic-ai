import java.util.List;
import java.util.Random;

/**
 * Mengimplementasikan berbagai strategi crossover untuk Algoritma Genetika.
 * Digunakan untuk menggabungkan dua kromosom parent untuk menghasilkan keturunan.
 * <p>
 * <strong>Catatan:</strong> Beberapa metode crossover (One-Point, Two-Point, Uniform)
 * tersedia untuk menentukan mana yang menghasilkan konvergensi terbaik.
 *
 * @author Vandyka
 */
public class CrossoverStrategy {
    /** Generator random acak untuk mencari titik potong */
    private final Random random;

    /** Puzzle mosaic untuk pembuatan individu baru */
    private final Mosaic mosaic;

    /**
     * Membangun Strategi Crossover baru.
     *
     * @param random Generator angka acak
     * @param mosaic Instance puzzle yang berisi informasi berkaitan dengan puzzle Mosaic
     */
    public CrossoverStrategy(Random random, Mosaic mosaic) {
        this.random = random;
        this.mosaic = mosaic;
    }

    /**
     * Strategi crossover dengan cara memotong board secara horizontal di titik acak.
     *
     * <p>Semua baris sebelum titik potong diwariskan dari parent pertama, sedangkan baris
     * sisanya dari parent kedua</p>
     *
     * <p><b>Contoh</b></p>
     * Parent 1 (p1) dan parent 2 (p2) di board 5x5 dengan indeks perpotongan 2:
     * <pre>
     * Anak 1:              Anak 2:
     * p1 p1 p1 p1 p1       p2 p2 p2 p2 p2
     * p1 p1 p1 p1 p1       p2 p2 p2 p2 p2
     * p2 p2 p2 p2 p2       p1 p1 p1 p1 p1
     * p2 p2 p2 p2 p2       p1 p1 p1 p1 p1
     * p2 p2 p2 p2 p2       p1 p1 p1 p1 p1
     * </pre>
     *
     * @param kromosom1 Kromosom parent 1
     * @param kromosom2 Kromosom parent 2
     * @return Array berisi 2 individu hasil persilangan
     */
    public Individu[] rowBasedCrossover(boolean[][] kromosom1, boolean[][] kromosom2) {
        int chromosomeLength = kromosom1.length;
        boolean[][] child1 = new boolean[chromosomeLength][chromosomeLength];
        boolean[][] child2 = new boolean[chromosomeLength][chromosomeLength];

        int crossoverPoint = random.nextInt(chromosomeLength);

        /*
         * Untuk setiap baris, jika baris sekarang di atas titik crossover:
         * - Child 1 mengambil gene dari parent 1
         * - Child 2 mengambil gene dari parent 2
         * jika baris di bawah atau sama dengan titik crossover:
         * - Child 1 mengambil gene dari parent 2
         * - Child 2 mengambil gene dari parent 1
         */
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

    /**
     * Strategi crossover dengan cara memotong board secara vertikal di titik acak.
     *
     * <p>Semua kolom sebelum titik potong diwariskan dari parent pertama, sedangkan kolom
     * sisanya dari parent kedua</p>
     *
     * <p><b>Contoh</b></p>
     * Parent 1 (p1) dan parent 2 (p2) di board 5x5 dengan indeks perpotongan 3:
     * <pre>
     * Anak 1:              Anak 2:
     * p1 p1 p1 p2 p2       p2 p2 p2 p1 p1
     * p1 p1 p1 p2 p2       p2 p2 p2 p1 p1
     * p1 p1 p1 p2 p2       p2 p2 p2 p1 p1
     * p1 p1 p1 p2 p2       p2 p2 p2 p1 p1
     * p1 p1 p1 p2 p2       p2 p2 p2 p1 p1
     * </pre>
     *
     * @param kromosom1 Kromosom parent 1
     * @param kromosom2 Kromosom parent 2
     * @return Array berisi 2 individu hasil persilangan
     */
    public Individu[] colBasedCrossover(boolean[][] kromosom1, boolean[][] kromosom2) {
        int chromosomeLength = kromosom1.length;
        boolean[][] child1 = new boolean[chromosomeLength][chromosomeLength];
        boolean[][] child2 = new boolean[chromosomeLength][chromosomeLength];

        int crossoverPoint = random.nextInt(chromosomeLength);

        /*
         * Untuk setiap kolom, jika kolom sekarang di kiri titik crossover:
         * - Child 1 mengambil gene dari parent 1
         * - Child 2 mengambil gene dari parent 2
         * jika kolom di kanan atau sama dengan titik crossover:
         * - Child 1 mengambil gene dari parent 2
         * - Child 2 mengambil gene dari parent 1
         */
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

    /**
     * Strategi crossover dengan cara memotong board secara vertikal dan horizontal di titik acak
     * sehingga membentuk seperti koordinat kartesius dimana memiliki 4 kuadran dengan penomoran
     * kuadran 1 di kanan atas dan berputar counter clockwise.
     *
     * <p>Kuadran 2 dan 4 diwariskan dari parent pertama lalu kuadran 1 dan 3 diwariskan
     * dari parent kedua</p>
     *
     * <p><b>Contoh</b></p>
     * Parent 1 (p1) dan parent 2 (p2) di board 5x5 dengan indeks perpotongan row 3 dan col 3:
     * <pre>
     * Anak 1:              Anak 2:
     * p1 p1 p1 p2 p2       p2 p2 p2 p1 p1
     * p1 p1 p1 p2 p2       p2 p2 p2 p1 p1
     * p1 p1 p1 p2 p2       p2 p2 p2 p1 p1
     * p2 p2 p2 p1 p1       p1 p1 p1 p2 p2
     * p2 p2 p2 p1 p1       p1 p1 p1 p2 p2
     * </pre>
     *
     * @param kromosom1 Kromosom parent 1
     * @param kromosom2 Kromosom parent 2
     * @return Array berisi 2 individu hasil persilangan
     */
    public Individu[] rowAndColBasedCrossover(boolean[][] kromosom1, boolean[][] kromosom2) {
        int chromosomeLength = kromosom1.length;
        boolean[][] child1 = new boolean[chromosomeLength][chromosomeLength];
        boolean[][] child2 = new boolean[chromosomeLength][chromosomeLength];

        int rowCrossoverPoint = random.nextInt(chromosomeLength);
        int colCrossoverPoint = random.nextInt(chromosomeLength);

        /*
         * Untuk setiap baris dan kolom, jika masuk ke kuadran 2 atau 3:
         * - Child 1 mengambil gene dari parent 2
         * - Child 2 mengambil gene dari parent 1
         * jika masuk kuadran 1 atau 4:
         * - Child 1 mengambil gene dari parent 1
         * - Child 2 mengambil gene dari parent 2
         */
        for (int i = 0; i < chromosomeLength; i++) {
            for (int j = 0; j < chromosomeLength; j++) {
                if (i < rowCrossoverPoint && j >= colCrossoverPoint ||
                        i >= rowCrossoverPoint && j < colCrossoverPoint) {
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

    /**
     * Strategi crossover dengan cara membuat sub grid baru dalam board.
     *
     * <p>Semua gene di luar sub grid mewarisi gene parent pertama dan gene dalam
     * sub grid mewarisi gene parent kedua</p>
     *
     * <p><b>Contoh</b></p>
     * Parent 1 (p1) dan parent 2 (p2) di board 5x5 dengan r1 = 1, c1 = 1, r2 = 3, c2 = 2:
     * <pre>
     * Anak 1:              Anak 2:
     * p1 p1 p1 p1 p1       p2 p2 p2 p2 p2
     * p1 p2 p2 p1 p1       p2 p1 p1 p2 p2
     * p1 p2 p2 p1 p1       p2 p1 p1 p2 p2
     * p1 p2 p2 p1 p1       p2 p1 p1 p2 p2
     * p1 p1 p1 p1 p1       p2 p2 p2 p2 p2
     * </pre>
     *
     * @param kromosom1 Kromosom parent 1
     * @param kromosom2 Kromosom parent 2
     * @return Array berisi 2 individu hasil persilangan
     */
    public Individu[] subGridBasedCrossover(boolean[][] kromosom1, boolean[][] kromosom2) {
        int chromosomeLength = kromosom1.length;
        boolean[][] child1 = new boolean[chromosomeLength][chromosomeLength];
        boolean[][] child2 = new boolean[chromosomeLength][chromosomeLength];

        /*
         * r1 dan c1 merupakan baris dan kolom sudut kiri atas grid sedangkan
         * r2 dan c2 merupakan baris dan kolom sudukan kanan bawah grid
         */
        int r1 = random.nextInt(chromosomeLength);
        int r2 = random.nextInt(r1, chromosomeLength);
        int c1 = random.nextInt(chromosomeLength);
        int c2 = random.nextInt(c1, chromosomeLength);

        /*
         * Untuk setiap baris dan kolom, jika baris dan kolom berada di dalam sub grid:
         * - Child 1 mengambil gene parent 2
         * - Child 2 mengambil gene parent 1
         * jika baris dan kolom di luar sub grid:
         * - Child 1 mengambil gene parent 1
         * - Child 2 mengambil gene parent 2
         *
         */
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

    /**
     * Strategi crossover dengan cara memberi peluang sama setiap gene untuk dibalik
     * dari true ke false dan dari false ke true.
     *
     * <p>Semua gene yang masih belum diketahui warnanya memiliki peluang 50% untuk
     * berganti warnanya</p>
     *
     * @param kromosom1 Kromosom parent 1
     * @param kromosom2 Kromosom parent 2
     * @return Array berisi 2 individu hasil persilangan
     */
    public Individu[] uniformCrossover(boolean[][] kromosom1, boolean[][] kromosom2, List<Cell> unknownCells) {
        int chromosomeLength = kromosom1.length;
        boolean[][] child1 = new boolean[chromosomeLength][chromosomeLength];
        boolean[][] child2 = new boolean[chromosomeLength][chromosomeLength];

        /*
         * Loop hanya dari cell yang tidak diketahui untuk menghindari mengganti
         * warna cell yang sudah diketahui dari heuristik. Setiap cell yang tidak diketahui
         * mempunyai peluang 50% untuk berubah warna.
         */
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
