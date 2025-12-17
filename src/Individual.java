import java.util.*;

public class Individual implements Comparable<Individual> {
    private final Random rand;
    private final int[] chromosome;
    private double cost;

    public Individual(Random rand, int[] chromosome) {
        this.rand = rand;
        this.chromosome = chromosome;
    }

    public int[] getChromosome() {
        return this.chromosome;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    @Override
    public int compareTo(Individual o) {
        return Double.compare(this.cost, o.cost);
    }

    public Individual[] crossover(Individual parent2) {
        int[] child1 = chromosome.clone();
        int[] child2 = parent2.chromosome.clone();

        int chromosomeLength = chromosome.length;

        // Single point crossover
        int crossoverPoint = rand.nextInt(chromosomeLength);

        for (int i = crossoverPoint; i < chromosomeLength; i++) {
            int temp = child1[i];
            child1[i] = child2[i];
            child2[i] = temp;
        }

        return new Individual[]{new Individual(rand, child1), new Individual(rand, child2)};
    }

    /*
    Mutasi alel dengan rate sebesar mutationRate
     */
    public void mutate(List<Position> emptyPositions, double mutationRate) {
        for (int i = 0; i < chromosome.length; i++) {
            if (rand.nextDouble() < mutationRate) {
                int pickedIdx;
                do {
                    pickedIdx = rand.nextInt(emptyPositions.size());
                }
                while (contains(pickedIdx));
                chromosome[i] = pickedIdx;
            }
        }
    }

    private boolean contains(int value) {
        for (int alel : chromosome) {
            if (alel == value) {
                return true;
            }
        }
        return false;
    }

    public void repairChromosome(int fireStationCount) {
        Set<Integer> usedPositions = new HashSet<>();

        for (int i = 0; i < chromosome.length; i++) {
            int gene = chromosome[i];

            // Check if position is valid (empty and not duplicate)
            if (usedPositions.contains(gene)) {
                // Find a replacement
                int newGene;
                do {
                    newGene = rand.nextInt(fireStationCount);
                }
                while (usedPositions.contains(newGene));

                chromosome[i] = newGene;
                gene = newGene;
            }
            usedPositions.add(gene);
        }
    }

    public void printResult(List<Position> emptyPositions) {
        System.out.printf("%d %.5f\n", chromosome.length, cost);
        for (int i : chromosome) {
            System.out.println((emptyPositions.get(i).getX() + 1) + " " + (emptyPositions.get(i).getY() + 1));
        }
    }
}
