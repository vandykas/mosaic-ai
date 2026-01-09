import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

public class Populasi {
    private final Mosaic mosaic;
    private final Random random;
    private final int maxPopulationSize;
    private final List<Individu> population;

    public Populasi(int maxPopulationSize, Mosaic mosaic, Random random) {
        this.mosaic = mosaic;
        this.random = random;
        this.maxPopulationSize = maxPopulationSize;
        this.population = new ArrayList<>();
    }

    public int getPopulationSize() {
        return population.size();
    }

    public Individu getIndividuTerbaik() {
        return population.get(0);
    }

    public void initPopulasi() {
        for (int i = 0; i < maxPopulationSize; i++) {
            Individu individu = new Individu(random, mosaic);
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

    public void addIndividu(Individu individu) {
        this.population.add(individu);
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