import java.util.Arrays;
import java.util.Random;

public class Individu implements Comparable<Individu> {
    private Random random;
    private Mosaic mosaic;
    private boolean[] kromosom;
    private double fitness;

    public Individu(Random random, Mosaic mosaic, boolean[] kromosom) {
        this.random = random;
        this.mosaic = mosaic;
        this.fitness = mosaic.fitnessFunction(kromosom);
        this.kromosom = Arrays.copyOf(kromosom, kromosom.length);
    }

    public Individu(Random random, Mosaic mosaic) {
        this.random = random;
        this.mosaic = mosaic;

        this.kromosom = new boolean[mosaic.getUnknownCellsSize()];
        initKromosom();
        this.fitness = mosaic.fitnessFunction(kromosom);
    }

    @Override
    public int compareTo(Individu o) {
        return Double.compare(o.fitness, this.fitness);
    }

    public boolean[] getKromosom() {
        return this.kromosom;
    }

    public double getFitness() {
        return fitness;
    }

    private void initKromosom() {
        for (int i = 0; i < kromosom.length; i++) {
            if (random.nextDouble() > 0.5) {
                kromosom[i] = true;
            }
        }
    }

    public void mutasi(double mutation_rate) {
        for (int i = 0; i < kromosom.length; i++) {
            if (random.nextDouble() < mutation_rate) {
                kromosom[i] = !kromosom[i];
            }
        }
    }
    
    public Individu[] singlePointCrossover(Individu pasangan) {
        boolean[] child1 = kromosom.clone();
        boolean[] child2 = pasangan.getKromosom().clone();

        int chromosomeLength = kromosom.length;
        int crossoverPoint = random.nextInt(chromosomeLength);

        for (int i = crossoverPoint; i < chromosomeLength; i++) {
            boolean temp = child1[i];
            child1[i] = child2[i];
            child2[i] = temp;
        }

        return new Individu[]{new Individu(random, mosaic, child1), new Individu(random, mosaic, child2)};
    }

    public Individu[] doublePointCrossover(Individu pasangan) {
        boolean[] child1 = kromosom.clone();
        boolean[] child2 = pasangan.getKromosom().clone();

        int chromosomeLength = kromosom.length;

        int crossoverPoint1 = random.nextInt(chromosomeLength);
        int crossoverPoint2;
        do {
            crossoverPoint2 = random.nextInt(chromosomeLength);
        }
        while (crossoverPoint1 == crossoverPoint2);

        for (int i = Math.min(crossoverPoint1, crossoverPoint2); i < Math.max(crossoverPoint1, crossoverPoint2) ; i++) {
            boolean temp = child1[i];
            child1[i] = child2[i];
            child2[i] = temp;
        }

        return new Individu[]{new Individu(random, mosaic, child1), new Individu(random, mosaic, child2)};
    }
}