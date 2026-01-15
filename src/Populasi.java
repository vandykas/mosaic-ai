import java.util.ArrayList;
import java.util.Collections;

import java.util.List;
import java.util.Random;

/**
 * Mengelola populasi individu untuk Algoritma Genetika.
 * Menangani inisialisasi, elitisme, pembaruan probabilitas, dan pengurutan berdasarkan fitness.
 *
 * @author TODO to be filled
 */
public class Populasi {
    private final Mosaic mosaic;
    private final Random random;
    private final int maxPopulationSize;
    private final List<Individu> population;
    private final double[] probability;

    /**
     * Membangun Populasi baru.
     *
     * @param maxPopulationSize Jumlah maksimum individu yang diizinkan.
     * @param mosaic            Instance puzzle.
     * @param random            Generator angka acak.
     */
    public Populasi(int maxPopulationSize, Mosaic mosaic, Random random) {
        this.mosaic = mosaic;
        this.random = random;
        this.maxPopulationSize = maxPopulationSize;
        this.population = new ArrayList<>();
        this.probability = new double[mosaic.getUnknownCellsSize()];
    }

    public int getPopulationSize() {
        return population.size();
    }

    public Individu getIndividuTerbaik() {
        return population.get(0);
    }

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

    public void fillProbability() {
        List<Cell> unknownCells = mosaic.getUnknownCells();
        for (Individu individu : population) {
            boolean[][] kromosom = individu.getKromosom();
            for (int i = 0; i < unknownCells.size(); i++) {
                Cell cell = unknownCells.get(i);
                probability[i] += kromosom[cell.row()][cell.col()] ? 1 : 0;
            }
        }

        for (int i = 0; i < probability.length; i++) {
            probability[i] /= maxPopulationSize;
        }
    }

    public void addIndividu(Individu individu) {
        this.population.add(individu);
    }

    public void calculatePopulationFitness() {
        for (Individu individu : population) {
            individu.calculateFitness();
        }
    }

    public void calculatePopulationFitnessWithDiversity(double alpha) {
        for (Individu individu : population) {
            individu.calculateFitnessWithDiversity(alpha, probability);
        }
    }

    public double hitungFitnessRataRata() {
        double total = 0;
        for (Individu individu : population) {
            total += individu.getFitness();
        }
        return total / maxPopulationSize;
    }
    
    public void sortPopulation() {
        Collections.sort(population);
    }
    
    public Individu seleksiRoulette() {
        SelectionStrategy selectionStrategy = new SelectionStrategy(random, population);
        return selectionStrategy.seleksiRoulette();
    }
    
    public Individu seleksiRank() {
        SelectionStrategy selectionStrategy = new SelectionStrategy(random, population);
        return selectionStrategy.seleksiRank();
    }
    
    public Individu seleksiTournament(int ukuranTurnamen) {
        SelectionStrategy selectionStrategy = new SelectionStrategy(random, population);
        return selectionStrategy.seleksiTournament(ukuranTurnamen);
    }
    
}