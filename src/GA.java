import java.util.*;

public class GA {
    private final FireStation fireStation;
    private final Random rand;
    private final int totalGeneration;
    private final int maxPopulationSize;
    private final double mutationRate;
    private final double crossOverRate;
    private final double elitismPct;
    private final double convergenceThreshold;
    private final int convergenceWindow;

    public GA(FireStation fireStation, int totalGeneration, int maxPopulationSize, double mutationRate, double crossOverRate, double elitismPct, Random rand, int convergenceWindow, double convergenceThreshold) {
        this.fireStation = fireStation;
        this.totalGeneration = totalGeneration;
        this.maxPopulationSize = maxPopulationSize;
        this.mutationRate = mutationRate;
        this.crossOverRate = crossOverRate;
        this.elitismPct = elitismPct;
        this.rand = rand;
        this.convergenceWindow = convergenceWindow;
        this.convergenceThreshold = convergenceThreshold;
    }

    public Individual runGenAlgo() {
        Population population = new Population(fireStation, rand, maxPopulationSize, elitismPct);
        population.initPopulation();
        population.evaluatePopulationCost();
        population.sortPopulation();

        double[] recentFitness = new double[convergenceWindow];
        int convergenceCounter = 0;

        for (int generation = 1; generation <= totalGeneration; generation++) {
            Population nextPop = population.initPopulationWithElitism();
            nextPop.initPopulationWithElitism();

            while (nextPop.getPopulationSize() < maxPopulationSize) {
                Individual parent1 = population.selectParentRank();
                Individual parent2 = population.selectParentRank();

                if (rand.nextDouble() < crossOverRate) {
                    Individual[] children = parent1.crossover(parent2);

                    // Coba mutasi kedua anak
                    children[0].mutate(fireStation.getEmptyPosition(), mutationRate);
                    children[1].mutate(fireStation.getEmptyPosition(), mutationRate);

                    // Perbaiki kromosom karena bisa terjadi duplikat akibat kawin silang
                    children[0].repairChromosome(fireStation.getFireStationsCount());
                    children[1].repairChromosome(fireStation.getFireStationsCount());

                    nextPop.addIndividual(children[0]);
                    if (nextPop.getPopulationSize() < maxPopulationSize) {
                        nextPop.addIndividual(children[1]);
                    }
                }
            }

            population = nextPop;
            population.evaluatePopulationCost();
            population.sortPopulation();
            System.out.printf("Generation: %d best distance: %.5f\n", generation, population.getBestIndividual().getCost());

            double meanPopulFitness = population.getMeanPopulationCost();

            // Check convergence
            recentFitness[convergenceCounter % convergenceWindow] = meanPopulFitness;
            convergenceCounter++;

            if (convergenceCounter >= convergenceWindow) {
                double minFitness = Double.MAX_VALUE;
                double maxFitness = Double.MIN_VALUE;

                for (double fitness : recentFitness) {
                    minFitness = Math.min(minFitness, fitness);
                    maxFitness = Math.max(maxFitness, fitness);
                }

                if (maxFitness - minFitness < convergenceThreshold) {
                    System.out.println("Converged at generation " + generation);
                    break;
                }
            }
        }
        return population.getBestIndividual();
    }
}

