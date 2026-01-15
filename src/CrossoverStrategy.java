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

    /**
     * Strategi crossover dengan cara membuat sebuah garis yang membelah kromosom parent
     * lalu untuk masing-masing potongan dimasukkan ke kromosom kedua anak secara bergantian.
     * Urutan kromosom anak 1 : parent 1 | parent 2
     * Urutan kromosom anak 2 : parent 2 | parent 1
     *
     * @param kromosom1 Kromosom dari parent pertama yang akan disilangkan
     * @param kromosom2 Kromosom dari parent kedua yang akan disilangkan
     * @return Mengembalikan array berisi dua Individu hasil persilangan
     */
    public Individu[] onePointCrossover(boolean[] kromosom1,  boolean[] kromosom2) {
        // Inisialisasi anak dengan kromosom parent
        boolean[] child1 = kromosom1.clone();
        boolean[] child2 = kromosom2.clone();

        // Mencari titik potong garis secara random
        int chromosomeLength = kromosom1.length;
        int crossoverPoint = random.nextInt(chromosomeLength);

        /*
         * Mulai dari titik potong hingga akhir kromosom, tukar kromosom
         * anak 1 dan anak 2 agar anak 1 mendapatkan kromosom dari parent 2 dan
         * anak 2 mendapatkan kromosom dari parent 1
         */
        for (int i = crossoverPoint; i < chromosomeLength; i++) {
            boolean temp = child1[i];
            child1[i] = child2[i];
            child2[i] = temp;
        }
        return new Individu[]{new Individu(random, mosaic, child1), new Individu(random, mosaic, child2)};
    }

    /**
     * Strategi crossover dengan cara membuat dua garis yang membelah kromosom parent
     * lalu untuk masing-masing potongan dimasukkan ke kromosom kedua anak secara bergantian.
     * Urutan kromosom anak 1 : parent 1 | parent 2 | parent 1
     * Urutan kromosom anak 2 : parent 2 | parent 1 | parent 2
     *
     * @param kromosom1 Kromosom dari parent pertama yang akan disilangkan
     * @param kromosom2 Kromosom dari parent kedua yang akan disilangkan
     * @return Mengembalikan array berisi dua Individu hasil persilangan
     */
    public Individu[] twoPointCrossover(boolean[] kromosom1,  boolean[] kromosom2) {
        // Inisialisasi anak dengan kromosom parent
        boolean[] child1 = kromosom1.clone();
        boolean[] child2 = kromosom2.clone();

        // Mencari dua titik potong garis secara random
        int chromosomeLength = kromosom1.length;
        int crossoverPoint1 = random.nextInt(chromosomeLength);
        int crossoverPoint2 = random.nextInt(chromosomeLength);

        // Memastikan 2 titik berbeda agar segmen crossover tidak memiliki panjang 0
        while (crossoverPoint1 == crossoverPoint2) {
            crossoverPoint2 = random.nextInt(chromosomeLength);
        }

        // Mulai dari titik kiri, tukar gene anak hingga titik kanan
        for (int i = Math.min(crossoverPoint1, crossoverPoint2); i < Math.max(crossoverPoint1, crossoverPoint2) ; i++) {
            boolean temp = child1[i];
            child1[i] = child2[i];
            child2[i] = temp;
        }
        return new Individu[]{new Individu(random, mosaic, child1), new Individu(random, mosaic, child2)};
    }

    /**
     * Strategi crossover dengan cara memberi setiap gene anak peluang untuk mengambil gene parent 1
     * atau parent 2.
     *
     * @param kromosom1 Kromosom dari parent pertama yang akan disilangkan
     * @param kromosom2 Kromosom dari parent kedua yang akan disilangkan
     * @return Mengembalikan array berisi dua Individu hasil persilangan
     */
    public Individu[] uniformCrossover(boolean[] kromosom1, boolean[] kromosom2) {
        // Inisialisasi anak dengan kromosom parent
        boolean[] child1 = kromosom1.clone();
        boolean[] child2 = kromosom2.clone();

        /*
         * Iterasi setiap gene kromosom dan jika memenuhi peluang, tukar gene
         * anak 1 dan anak 2. Peluang adalah 0.5 agar setiap gene memiliki peluang sama
         * untuk mengambil gene dari parent 1 atau 2
         */
        int chromosomeLength = kromosom1.length;
        for (int i = 0; i < chromosomeLength; i++) {
            if (random.nextDouble() < 0.5) {
                boolean temp = child1[i];
                child1[i] = child2[i];
                child2[i] = temp;
            }
        }
        return new Individu[]{new Individu(random, mosaic, child1), new Individu(random, mosaic, child2)};
    }
}
