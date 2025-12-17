import java.util.*;

public class Population {
    private final Mosaic mosaic;
    private final Random rand;
    private final List<Individual> population;
    private final int maxPopulationSize;
    private int populationSize;
    private final double elitismPct;

    public Population(Mosaic mosaic, Random rand, int maxPopulationSize, double elitismPct) {
        this.mosaic = mosaic;
        this.rand = rand;
        this.population = new ArrayList<>();
        this.maxPopulationSize = maxPopulationSize;
        this.elitismPct = elitismPct;
    }

    public int getPopulationSize() {
        return populationSize;
    }

    public void sortPopulation() {
        Collections.sort(population);
    }

    public Individual getBestIndividual() {
        return population.getFirst();
    }

    // TODO : Sesuaikan populasi awal dengan puzzle mosaic
    public void initPopulation() {
        List<Position> emptyPositions = mosaic.getEmptyPosition();
        int mosaicCount = mosaic.getMosaicsCount();

        for (int i = 0; i < maxPopulationSize; i++) {
            int[] chromosome = new int[mosaicCount];
            Set<Integer> usedPositions = new HashSet<>();

            for (int j = 0; j < mosaicCount; j++) {
                int pickedIdx;
                do {
                    pickedIdx = rand.nextInt(emptyPositions.size());
                }
                while (usedPositions.contains(pickedIdx));
                usedPositions.add(pickedIdx);
                chromosome[j] = pickedIdx;
            }

            population.add(new Individual(rand, chromosome));
        }
    }

    public Population initPopulationWithElitism() {
        int elitismCount = (int) (maxPopulationSize * elitismPct);
        Population nextPop = new Population(this.mosaic, this.rand, this.maxPopulationSize, this.elitismPct);
        for (int i = 0; i < elitismCount; i++) {
            nextPop.addIndividual(population.get(i));
        }
        return nextPop;
    }

    public void addIndividual(Individual individualToAdd) {
        this.population.add(individualToAdd);
        this.populationSize++;
    }

    public void evaluatePopulationCost() {
        List<Position> emptyPositions = mosaic.getEmptyPosition();
        for (Individual i : population) {
            List<Position> mosaicPos = getMosaicPos(emptyPositions, i.getChromosome());
            i.setCost(mosaic.getMinimumDistance(mosaicPos) / mosaic.getHouseCount());
        }
    }

    public double getMeanPopulationCost() {
        List<Position> emptyPositions = mosaic.getEmptyPosition();
        double totalPopulationCost = 0;
        for (Individual i : population) {
            List<Position> mosaicPos = getMosaicPos(emptyPositions, i.getChromosome());
            totalPopulationCost += mosaic.getMinimumDistance(mosaicPos);
        }
        return totalPopulationCost /  populationSize;
    }

    private List<Position> getMosaicPos(List<Position> emptyPositions, int[] chromosome) {
        List<Position> mosaicPos = new ArrayList<>();
        for (int alel : chromosome) {
            mosaicPos.add(emptyPositions.get(alel));
        }
        return mosaicPos;
    }

    /*
    Memilih parent dengan teknik roulette-wheel selection
     */
    public Individual selectParentRoulette() {
        double totalFitness = 0;
        for (Individual individual : population) {
            totalFitness += 1.0 / (1.0 + individual.getCost()); // Inverse for minimization
        }

        double randomValue = rand.nextDouble() * totalFitness;
        double currentSum = 0;

        for (Individual individual : population) {
            currentSum += 1.0 / (1.0 + individual.getCost());
            if (currentSum >= randomValue) {
                return individual;
            }
        }
        return population.getLast();
    }

    /*
    Memilih parent dengan teknik rank selection
     */
    public Individual selectParentRank() {
        int N = population.size();

        double totalRankWeight = (double) N * (N + 1) / 2.0;
        double randomValue = rand.nextDouble() * totalRankWeight;

        double currentSum = 0.0;
        for (int i = 0; i < N; i++) {
            int rank = N - i;
            currentSum += rank;
            if (currentSum >= randomValue) {
                return population.get(i);
            }
        }
        return population.getLast();
    }
}
