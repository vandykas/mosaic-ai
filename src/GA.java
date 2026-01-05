import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GA {
    private final Mosaic mosaic;
    private Random random;
    private final int maxPopulationSize;
    private final double mutationRate;
    private final double elitismRate;
    private final int maxGeneration;
    private final double convergenceThreshold;
    private final int convergenceWindow;
    private List<Double> riwayatFitnessPopulasi;
    
    public GA(Mosaic mosaic, int maxPopulationSize, double mutationRate, double elitismRate, int maxGeneration,
              double convergence_threshold, int convergenceWindow) {
        this.mosaic = mosaic;
        this.maxPopulationSize = maxPopulationSize;
        this.mutationRate = mutationRate;
        this.elitismRate = elitismRate;
        this.maxGeneration = maxGeneration;
        this.convergenceThreshold = convergence_threshold;
        this.convergenceWindow = convergenceWindow;
        this.riwayatFitnessPopulasi = new ArrayList<>();
    }

    public void setRandom(int seed) {
        this.random = new Random(seed);
    }

    public void run(int repetisi) {
        for (int r = 0; r < repetisi; r++) {
            System.out.println("Repetisi ke-" + (r + 1));

            setRandom(r);
            Individu solusiTerbaik = simulate();

            System.out.println("Fitness terbaik: " + solusiTerbaik.getFitness());
            mosaic.printSolution(solusiTerbaik.getKromosom());
            System.out.println();
        }
    }

    private Individu simulate() {
        Populasi currPopulation = initPopulasi();
        Individu individuTerbaik = currPopulation.getIndividuTerbaik();

        int generasi = 0;
        boolean konvergen = false;
        while (generasi < maxGeneration && !konvergen) {
            Populasi nextPopulation = buatGenerasiBaru(currPopulation);

            Individu terbaikSaatIni = nextPopulation.getIndividuTerbaik();
            if (terbaikSaatIni.getFitness() > individuTerbaik.getFitness()) {
                individuTerbaik = terbaikSaatIni;
            }

            riwayatFitnessPopulasi.add(nextPopulation.hitungFitnessRataRata());
            if (generasi >= convergenceWindow) {
                konvergen = cekKonvergensi();
            }

            currPopulation = nextPopulation;
            generasi++;
        }
        return individuTerbaik;
    }

    private Populasi initPopulasi() {
        Populasi population = new Populasi(maxPopulationSize, mosaic, random);
        population.initPopulasi();
        population.sortPopulation();
        return population;
    }
    
    private Populasi buatGenerasiBaru(Populasi currPopulation) {
        Populasi nextPopulation = currPopulation.initPopulasiWithElitism(elitismRate);
        while (nextPopulation.getPopulationSize() < maxPopulationSize) {
            Individu parent1 = currPopulation.seleksiTournament(4);
            Individu parent2 = currPopulation.seleksiTournament(4);

            Individu[] children = parent1.onePointCrossover(parent2);
            children[0].mutasi(mutationRate);
            children[1].mutasi(mutationRate);

            nextPopulation.addIndividu(children[0]);
            if (nextPopulation.getPopulationSize() < maxPopulationSize) {
                nextPopulation.addIndividu(children[1]);
            }
        }
        nextPopulation.sortPopulation();
        return nextPopulation;
    }
    
    private boolean cekKonvergensi() {
        if (riwayatFitnessPopulasi.size() < convergenceWindow) {
            return false;
        }
        
        int start = riwayatFitnessPopulasi.size() - convergenceWindow;
        int end = riwayatFitnessPopulasi.size();
        
        double maxFitness = Double.NEGATIVE_INFINITY;
        double minFitness = Double.POSITIVE_INFINITY;
        
        for (int i = start; i < end; i++) {
            double fitness =  riwayatFitnessPopulasi.get(i);
            maxFitness = Math.max(maxFitness, fitness);
            minFitness = Math.min(minFitness, fitness);
        }
        
        double perbedaan = Math.abs(maxFitness - minFitness);
        return perbedaan <= convergenceThreshold;
    }
}
