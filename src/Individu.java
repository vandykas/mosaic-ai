import java.util.Arrays;
import java.util.Random;

public class Individu implements Comparable<Individu> {
    private final Random random;
    private final Mosaic mosaic;
    private final boolean[] kromosom;
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

    public void initKromosom() {
        for (int i = 0; i < kromosom.length; i++) {
            kromosom[i] = random.nextDouble() > 0.5;
        }
    }

    public void initKromosomWithProbability() {
        for (int i = 0; i < kromosom.length; i++) {
            kromosom[i] = random.nextDouble() > mosaic.getUnknownCellsProb(i);
        }
    }

    public void mutasi(double mutation_rate) {
        for (int i = 0; i < kromosom.length; i++) {
            if (random.nextDouble() < mutation_rate) {
                kromosom[i] = !kromosom[i];
            }
        }
        this.fitness = mosaic.fitnessFunction(kromosom);
    }
    
    public Individu[] onePointCrossover(Individu pasangan) {
        CrossoverStrategy crossoverStrategy = new CrossoverStrategy(random, mosaic);
        return crossoverStrategy.onePointCrossover(this.kromosom, pasangan.getKromosom());
    }

    public Individu[] twoPointCrossover(Individu pasangan) {
        CrossoverStrategy crossoverStrategy = new CrossoverStrategy(random, mosaic);
        return crossoverStrategy.twoPointCrossover(this.kromosom, pasangan.getKromosom());
    }

    public Individu[] uniformCrossover(Individu pasangan) {
        CrossoverStrategy crossoverStrategy = new CrossoverStrategy(random, mosaic);
        return crossoverStrategy.uniformCrossover(this.kromosom, pasangan.getKromosom());
    }
}