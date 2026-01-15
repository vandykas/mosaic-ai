import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Merepresentasikan satu individu (kandidat solusi) dalam populasi.
 * Setiap individu memiliki kromosom (array boolean) yang merepresentasikan
 * status (Hitam/Putih) dari sel-sel yang tidak diketahui pada grid Mosaic.
 *
 * @author TODO to be filled
 */
public class Individu implements Comparable<Individu> {
    private final Random random;
    private final Mosaic mosaic;
    private final boolean[][] kromosom;
    private double fitness;

    /**
     * Membangun individu dengan kromosom tertentu.
     *
     * @param random   Generator angka acak.
     * @param mosaic   Instance puzzle.
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

    public void calculateFitnessWithDiversity(double alpha, double[] probability) {
        this.fitness = mosaic.fitnessFunctionWithDiversity(kromosom, probability, alpha);
    }

    public double getFitness() {
        return fitness;
    }

    public void initKromosom() {
        for (Cell cell : mosaic.getUnknownCells()) {
            kromosom[cell.row()][cell.col()] = random.nextDouble() < 0.5;
        }
    }

    public void initKromosomWithHeuristic() {
        CellState[][] partialSolution = mosaic.getPartialSolution();
        for (int i = 0; i < partialSolution.length; i++) {
            for (int j = 0; j < partialSolution.length; j++) {
                kromosom[i][j] = partialSolution[i][j] == CellState.BLACK;
            }
        }
    }

    public void initKromosomWithProbability() {
        List<Cell> unknownCells = mosaic.getUnknownCells();
        for (int i = 0; i < unknownCells.size(); i++) {
            Cell cell = unknownCells.get(i);
            kromosom[cell.row()][cell.col()] = random.nextDouble() < mosaic.getUnknownCellsProb(i);
        }
    }

    public void mutasi(double mutationRate) {
        for (Cell cell : mosaic.getUnknownCells()) {
            if (random.nextDouble() < mutationRate) {
                kromosom[cell.row()][cell.col()] = !kromosom[cell.row()][cell.col()];
            }
        }
    }
    
    public Individu[] rowBasedCrossover(Individu pasangan) {
        CrossoverStrategy crossoverStrategy = new CrossoverStrategy(random, mosaic);
        return crossoverStrategy.rowBasedCrossover(this.kromosom, pasangan.getKromosom());
    }

    public Individu[] colBasedCrossover(Individu pasangan) {
        CrossoverStrategy crossoverStrategy = new CrossoverStrategy(random, mosaic);
        return crossoverStrategy.colBasedCrossover(this.kromosom, pasangan.getKromosom());
    }

    public Individu[] rowAndColBasedCrossover(Individu pasangan) {
        CrossoverStrategy crossoverStrategy = new CrossoverStrategy(random, mosaic);
        return crossoverStrategy.rowAndColBasedCrossover(this.kromosom, pasangan.getKromosom());
    }

    public Individu[] subGridBasedCrossover(Individu pasangan) {
        CrossoverStrategy crossoverStrategy = new CrossoverStrategy(random, mosaic);
        return crossoverStrategy.subGridBasedCrossover(this.kromosom, pasangan.getKromosom());
    }

    public Individu[] uniformCrossover(Individu pasangan) {
        CrossoverStrategy crossoverStrategy = new CrossoverStrategy(random, mosaic);
        return crossoverStrategy.uniformCrossover(this.kromosom, pasangan.getKromosom(), mosaic.getUnknownCells());
    }
}