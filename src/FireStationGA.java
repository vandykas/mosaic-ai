import java.io.*;
import java.util.*;

public class FireStationGA {
    private static final int POPULATION_SIZE = 300;
    private static final double MUTATION_RATE = 0.003;
    private static final double ELITISM_RATE = 0.2;
    private static final int MAX_GENERATIONS = 999999;
    private static final double CONVERGENCE_THRESHOLD = 0.005;
    private static final int CONVERGENCE_WINDOW = 6;
    private static final int REPETITIONS = 10;
    
    private int l, w, n, h, t;
    private CellType[][] grid;
    private List<Position> houses;
    private PrintWriter outputWriter;
    
    enum CellType {
        EMPTY, TREE, HOUSE, FIRE_STATION
    }
    
    static class Position {
        int x, y;
        Position(int x, int y) {
            this.x = x;
            this.y = y;
        }
        
        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            Position position = (Position) obj;
            return x == position.x && y == position.y;
        }
        
        @Override
        public int hashCode() {
            return Objects.hash(x, y);
        }
    }
    
    static class Individual implements Comparable<Individual> {
        int[] chromosome;
        double fitness;
        
        Individual(int[] chromosome) {
            this.chromosome = chromosome.clone();
        }
        
        @Override
        public int compareTo(Individual other) {
            return Double.compare(this.fitness, other.fitness);
        }
    }

    //=================================================================================================================
    //                                              KODE MULAI DARI SINI
    //=================================================================================================================
    public static void main(String[] args) {
        FireStationGA ga = new FireStationGA();
        ga.run();
    }

    //read input dulu
    //ngulang algo GA sebanyak REPETITION kali
    //setiap kali ngulang nyimpen fitness populasi per generasi trs di export ke file biar bs di plot
    public void run() {
        readInput();
        
        try {
            outputWriter = new PrintWriter(new FileWriter("fitness_results.txt"));
            
            for (int repetition = 1; repetition <= REPETITIONS; repetition++) {
                long seed = repetition;
                Random random = new Random(seed);
                
                outputWriter.println("S" + repetition);
                System.out.println("Starting repetition " + repetition + " with seed " + seed);
                
                List<Double> generationFitness = runGeneticAlgorithm(random);
                
                for (double fitness : generationFitness) {
                    outputWriter.println(fitness);
                }
            }
            
            outputWriter.close();
        } catch (IOException e) {
            System.err.println("Error writing output file: " + e.getMessage());
        }
    }

    //read input
    private void readInput() {
        Scanner scanner = new Scanner(System.in);
        
        // Read grid dimensions
        l = scanner.nextInt();
        w = scanner.nextInt();
        
        // Read n, h, t
        n = scanner.nextInt();
        h = scanner.nextInt();
        t = scanner.nextInt();
        
        // Initialize grid
        grid = new CellType[l][w];
        for (int i = 0; i < l; i++) {
            for (int j = 0; j < w; j++) {
                grid[i][j] = CellType.EMPTY;
            }
        }
        
        // Read houses
        houses = new ArrayList<>();
        for (int i = 0; i < h; i++) {
            int x = scanner.nextInt() - 1; // Convert to 0-indexed
            int y = scanner.nextInt() - 1;
            grid[x][y] = CellType.HOUSE;
            houses.add(new Position(x, y));
        }
        
        // Read trees
        for (int i = 0; i < t; i++) {
            int x = scanner.nextInt() - 1;
            int y = scanner.nextInt() - 1;
            grid[x][y] = CellType.TREE;
        }
        
        scanner.close();
    }

    //algo GA jalan disini
    private List<Double> runGeneticAlgorithm(Random random) {
        List<Individual> population = initializePopulation(random);
        evaluatePopulation(population);
        Collections.sort(population);
        
        List<Double> generationFitness = new ArrayList<>();
        
        double[] recentFitness = new double[CONVERGENCE_WINDOW];
        int convergenceCounter = 0;
        
        for (int generation = 1; generation <= MAX_GENERATIONS; generation++) {
            List<Individual> newPopulation = new ArrayList<>();
            
            // Elitism
            int elitismCount = (int) (POPULATION_SIZE * ELITISM_RATE);
            for (int i = 0; i < elitismCount; i++) {
                newPopulation.add(new Individual(population.get(i).chromosome));
            }
            
            // Create remaining individuals through selection and crossover
            while (newPopulation.size() < POPULATION_SIZE) {
                Individual parent1 = selectParent(population, random);
                Individual parent2 = selectParent(population, random);
                
                Individual[] children = crossover(parent1, parent2, random);
                
                mutate(children[0], random);
                mutate(children[1], random);
                
                repairChromosome(children[0], random);
                repairChromosome(children[1], random);
                
                newPopulation.add(children[0]);
                if (newPopulation.size() < POPULATION_SIZE) {
                    newPopulation.add(children[1]);
                }
            }
            
            population = newPopulation;
            evaluatePopulation(population);
            Collections.sort(population);
            
            double meanPopulFitness = getMeanPopulFitness(population);
            generationFitness.add(meanPopulFitness);
            
            // Check convergence
            recentFitness[convergenceCounter % CONVERGENCE_WINDOW] = meanPopulFitness;
            convergenceCounter++;
            
            if (convergenceCounter >= CONVERGENCE_WINDOW) {
                double minFitness = Double.MAX_VALUE;
                double maxFitness = Double.MIN_VALUE;
                
                for (double fitness : recentFitness) {
                    if (fitness < minFitness) minFitness = fitness;
                    if (fitness > maxFitness) maxFitness = fitness;
                }
                
                if (maxFitness - minFitness < CONVERGENCE_THRESHOLD) {
                    System.out.println("Converged at generation " + generation);
                    break;
                }
            }
            
            if (generation % 100 == 0) {
                System.out.println("Generation " + generation + ", Mean fitness: " + meanPopulFitness);
            }
        }
        
        return generationFitness;
    }

    //ngitung fitness populasi
    private double getMeanPopulFitness(List<Individual> population) {
        double sum = 0;
        for(Individual individual: population){
            sum += individual.fitness;
        }
        return sum / POPULATION_SIZE;
    }

    //ngisi populasi pertama kali dengan individual 
    private List<Individual> initializePopulation(Random random) {
        List<Individual> population = new ArrayList<>();
        List<Integer> emptyPositions = getEmptyPositions();
        
        for (int i = 0; i < POPULATION_SIZE; i++) {
            int[] chromosome = new int[n];
            Set<Integer> usedPositions = new HashSet<>();
            
            for (int j = 0; j < n; j++) {
                int pos;
                do {
                    int index = random.nextInt(emptyPositions.size());
                    pos = emptyPositions.get(index);
                } while (usedPositions.contains(pos));
                
                chromosome[j] = pos;
                usedPositions.add(pos);
            }
            
            population.add(new Individual(chromosome));
        }
        
        return population;
    }
    
    private List<Integer> getEmptyPositions() {
        List<Integer> emptyPositions = new ArrayList<>();
        for (int i = 0; i < l; i++) {
            for (int j = 0; j < w; j++) {
                if (grid[i][j] == CellType.EMPTY) {
                    emptyPositions.add(coordToLinear(i, j));
                }
            }
        }
        return emptyPositions;
    }

    private int coordToLinear(int x, int y) {
        return x * w + y + 1;
    }
    
    private Position linearToCoord(int linear) {
        linear--; // Convert to 0-indexed
        int x = linear / w;
        int y = linear % w;
        return new Position(x, y);
    }

    //set fitness individual 
    private void evaluatePopulation(List<Individual> population) {
        for (Individual individual : population) {
            individual.fitness = calculateFitness(individual.chromosome);
        }
    }
    
    private double calculateFitness(int[] chromosome) {
        // Create distance grid and initialize with max value
        int[][] distanceGrid = new int[l][w];
        for (int i = 0; i < l; i++) {
            Arrays.fill(distanceGrid[i], Integer.MAX_VALUE);
        }
        
        // Create visited grid
        boolean[][] visited = new boolean[l][w];
        
        // Multi-source BFS from all fire stations
        Queue<Position> queue = new LinkedList<>();
        
        // Mark obstacles and add fire stations to queue
        for (int i = 0; i < l; i++) {
            for (int j = 0; j < w; j++) {
                if (grid[i][j] == CellType.TREE || grid[i][j] == CellType.HOUSE) {
                    visited[i][j] = true; // Mark obstacles as visited
                }
            }
        }
        
        // Mark fire stations and start BFS from them
        for (int gene : chromosome) {
            Position pos = linearToCoord(gene);
            if (grid[pos.x][pos.y] == CellType.EMPTY) {
                distanceGrid[pos.x][pos.y] = 0;
                visited[pos.x][pos.y] = true;
                queue.add(new Position(pos.x, pos.y));
            }
        }
        
        // Perform multi-source BFS
        int[][] directions = {{0, 1}, {1, 0}, {0, -1}, {-1, 0}};
        
        while (!queue.isEmpty()) {
            Position current = queue.poll();
            int currentDistance = distanceGrid[current.x][current.y];
            
            for (int[] dir : directions) {
                int newX = current.x + dir[0];
                int newY = current.y + dir[1];
                
                if (isValidPosition(newX, newY) && !visited[newX][newY]) {
                    distanceGrid[newX][newY] = currentDistance + 1;
                    visited[newX][newY] = true;
                    queue.add(new Position(newX, newY));
                }
            }
        }
        
        // Calculate total distance to houses
        double totalDistance = 0;
        int reachableHouses = 0;
        
        for (Position house : houses) {
            // For each house, find the minimum distance from its neighbors
            int minHouseDistance = Integer.MAX_VALUE;
            
            for (int[] dir : directions) {
                int neighborX = house.x + dir[0];
                int neighborY = house.y + dir[1];
                
                if (isValidPosition(neighborX, neighborY)) {
                    if (distanceGrid[neighborX][neighborY] < minHouseDistance) {
                        minHouseDistance = distanceGrid[neighborX][neighborY];
                    }
                }
            }
            
            // Also check if the house itself has a fire station (distance 0)
            if (distanceGrid[house.x][house.y] < minHouseDistance) {
                minHouseDistance = distanceGrid[house.x][house.y];
            }
            
            if (minHouseDistance != Integer.MAX_VALUE) {
                totalDistance += minHouseDistance + 1; // +1 because we need to reach the house from neighbor
                reachableHouses++;
            }
        }
        
        if (reachableHouses == 0) return Double.MAX_VALUE;
        
        return totalDistance / reachableHouses;
    }
    
    private boolean isValidPosition(int x, int y) {
        return x >= 0 && x < l && y >= 0 && y < w;
    }
    
    private CellType[][] copyGrid() {
        CellType[][] copy = new CellType[l][w];
        for (int i = 0; i < l; i++) {
            System.arraycopy(grid[i], 0, copy[i], 0, w);
        }
        return copy;
    }

    // Roulette wheel selection
    private Individual selectParent(List<Individual> population, Random random) {
        double totalFitness = 0;
        for (Individual individual : population) {
            totalFitness += 1.0 / (1.0 + individual.fitness); // Inverse for minimization
        }
        
        double randomValue = random.nextDouble() * totalFitness;
        double currentSum = 0;
        
        for (Individual individual : population) {
            currentSum += 1.0 / (1.0 + individual.fitness);
            if (currentSum >= randomValue) {
                return individual;
            }
        }
        
        return population.get(population.size() - 1);
    }

    //select paranet pake RANK SELECTION
    //nanti di uncomment
    
    /* 
    private Individual selectParent(List<Individual> population, Random random) {
        // Rank selection: sort population by fitness and assign selection probabilities based on rank
        
        // Sort population by fitness (ascending - lower fitness is better)
        List<Individual> sortedPopulation = new ArrayList<>(population);
        Collections.sort(sortedPopulation);
        
        // Assign ranks - better individuals get higher ranks
        int populationSize = sortedPopulation.size();
        double[] selectionProbabilities = new double[populationSize];
        double totalRank = 0;
        
        // Calculate total rank sum (rank 1 for worst, rank N for best)
        for (int i = 0; i < populationSize; i++) {
            // Rank starts from 1 (worst) to populationSize (best)
            int rank = i + 1;
            totalRank += rank;
            selectionProbabilities[i] = rank;
        }
        
        // Convert to cumulative probabilities
        double cumulative = 0;
        double[] cumulativeProbabilities = new double[populationSize];
        for (int i = 0; i < populationSize; i++) {
            cumulative += selectionProbabilities[i] / totalRank;
            cumulativeProbabilities[i] = cumulative;
        }
        
        // Select using rank-based probabilities
        double randomValue = random.nextDouble();
        for (int i = 0; i < populationSize; i++) {
            if (randomValue <= cumulativeProbabilities[i]) {
                return sortedPopulation.get(i);
            }
        }
        
        // Fallback - return the best individual
        return sortedPopulation.get(populationSize - 1);
    }
        */

    private Individual[] crossover(Individual parent1, Individual parent2, Random random) {
        int[] child1 = parent1.chromosome.clone();
        int[] child2 = parent2.chromosome.clone();
        
        // Single point crossover
        int crossoverPoint = random.nextInt(n);
        
        for (int i = crossoverPoint; i < n; i++) {
            int temp = child1[i];
            child1[i] = child2[i];
            child2[i] = temp;
        }
        
        return new Individual[]{new Individual(child1), new Individual(child2)};
    }
    
    private void mutate(Individual individual, Random random) {
        List<Integer> emptyPositions = getEmptyPositions();
        
        for (int i = 0; i < n; i++) {
            if (random.nextDouble() < MUTATION_RATE) {
                int newPos;
                do {
                    int index = random.nextInt(emptyPositions.size());
                    newPos = emptyPositions.get(index);
                } while (contains(individual.chromosome, newPos));
                
                individual.chromosome[i] = newPos;
            }
        }
    }

    //mutasi sama crossover bisa bikin duplikat gene
    //disini dibaikinnya
    private void repairChromosome(Individual individual, Random random) {
        Set<Integer> usedPositions = new HashSet<>();
        List<Integer> emptyPositions = getEmptyPositions();
        
        for (int i = 0; i < n; i++) {
            int gene = individual.chromosome[i];
            Position pos = linearToCoord(gene);
            
            // Check if position is valid (empty and not duplicate)
            if (grid[pos.x][pos.y] != CellType.EMPTY || usedPositions.contains(gene)) {
                // Find a replacement
                int newGene;
                do {
                    int index = random.nextInt(emptyPositions.size());
                    newGene = emptyPositions.get(index);
                } while (usedPositions.contains(newGene));
                
                individual.chromosome[i] = newGene;
                gene = newGene;
            }
            
            usedPositions.add(gene);
        }
    }
    
    private boolean contains(int[] array, int value) {
        for (int element : array) {
            if (element == value) {
                return true;
            }
        }
        return false;
    }
    
    static class QueueNode {
        int x, y, distance;
        QueueNode(int x, int y, int distance) {
            this.x = x;
            this.y = y;
            this.distance = distance;
        }
    }

}
