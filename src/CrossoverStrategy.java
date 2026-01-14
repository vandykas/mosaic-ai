import java.util.Random;

/**
 * Mengimplementasikan berbagai strategi crossover untuk Algoritma Genetika.
 * Digunakan untuk menggabungkan dua kromosom parent untuk menghasilkan keturunan.
 * <p>
 * <strong>Catatan:</strong> Beberapa metode crossover (One-Point, Two-Point, Uniform)
 * tersedia untuk menentukan mana yang menghasilkan konvergensi terbaik.
 *
 * @author TODO to be filled
 */
public class CrossoverStrategy {
    private final Random random;
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

    public Individu[] onePointCrossover(boolean[] kromosom1,  boolean[] kromosom2) {
        boolean[] child1 = kromosom1.clone();
        boolean[] child2 = kromosom2.clone();

        int chromosomeLength = kromosom1.length;
        int crossoverPoint = random.nextInt(chromosomeLength);

        for (int i = crossoverPoint; i < chromosomeLength; i++) {
            boolean temp = child1[i];
            child1[i] = child2[i];
            child2[i] = temp;
        }
        return new Individu[]{new Individu(random, mosaic, child1), new Individu(random, mosaic, child2)};
    }

    public Individu[] twoPointCrossover(boolean[] kromosom1,  boolean[] kromosom2) {
        boolean[] child1 = kromosom1.clone();
        boolean[] child2 = kromosom2.clone();

        int chromosomeLength = kromosom1.length;

        int crossoverPoint1 = random.nextInt(chromosomeLength);
        int crossoverPoint2 = random.nextInt(chromosomeLength);
        while (crossoverPoint1 == crossoverPoint2) {
            crossoverPoint2 = random.nextInt(chromosomeLength);
        }

        for (int i = Math.min(crossoverPoint1, crossoverPoint2); i < Math.max(crossoverPoint1, crossoverPoint2) ; i++) {
            boolean temp = child1[i];
            child1[i] = child2[i];
            child2[i] = temp;
        }
        return new Individu[]{new Individu(random, mosaic, child1), new Individu(random, mosaic, child2)};
    }

    public Individu[] uniformCrossover(boolean[] kromosom1, boolean[] kromosom2) {
        boolean[] child1 = kromosom1.clone();
        boolean[] child2 = kromosom2.clone();

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
