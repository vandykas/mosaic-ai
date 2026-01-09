import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GA {
    private final Mosaic mosaic;
    private Random random;
    private final GAConfig config;
    private List<Double> riwayatFitnessPopulasi;
    
    public GA(Mosaic mosaic, GAConfig config) {
        this.mosaic = mosaic;
        this.config = config;
        this.riwayatFitnessPopulasi = new ArrayList<>();
    }

    public void setRandom(int seed) {
        this.random = new Random(seed);
    }

    public void run() {
        Individu bestOverallIndividu = null;
        for (int r = 0; r < config.repetisi(); r++) {
            System.out.println("Repetisi ke-" + (r + 1));

            setRandom(r);
            Individu solusiTerbaik = simulate();
            printBestIndividu(solusiTerbaik);

            bestOverallIndividu = (bestOverallIndividu == null) ? solusiTerbaik : compareIndividu(bestOverallIndividu, solusiTerbaik);
        }
        printBestIndividu(bestOverallIndividu);
    }

    private Individu simulate() {
        Populasi currPopulation = initPopulasi();
        Individu individuTerbaik = currPopulation.getIndividuTerbaik();

        int generasi = 0;
        boolean konvergen = false;
        while (generasi < config.maxGeneration() && !konvergen) {
            Populasi nextPopulation = buatGenerasiBaru(currPopulation);

            Individu terbaikSaatIni = nextPopulation.getIndividuTerbaik();
            individuTerbaik = compareIndividu(individuTerbaik, terbaikSaatIni);

            riwayatFitnessPopulasi.add(nextPopulation.hitungFitnessRataRata());
            if (generasi >= config.convergenceWindow()) {
                konvergen = cekKonvergensi();
            }

            currPopulation = nextPopulation;
            generasi++;
        }
        return individuTerbaik;
    }

    private Individu compareIndividu(Individu individuTerbaik, Individu terbaikSaatIni) {
        if (terbaikSaatIni.getFitness() > individuTerbaik.getFitness()) {
            return terbaikSaatIni;
        }
        return individuTerbaik;
    }

    private Populasi initPopulasi() {
        Populasi population = new Populasi(config.maxPopulationSize(), mosaic, random);
        population.initPopulasi();
        population.sortPopulation();
        return population;
    }
    
    private Populasi buatGenerasiBaru(Populasi currPopulation) {
        Populasi nextPopulation = currPopulation.initPopulasiWithElitism(config.elitismRate());
        while (nextPopulation.getPopulationSize() < config.maxPopulationSize()) {
            Individu parent1 = currPopulation.seleksiTournament(8);
            Individu parent2 = currPopulation.seleksiTournament(8);

            Individu[] children = new Individu[2];
            if (random.nextDouble() < config.crossoverRate()) {
                children = parent1.twoPointCrossover(parent2);
            }
            else {
                children[0] = new Individu(random, mosaic, parent1.getKromosom());
                children[1] = new Individu(random, mosaic, parent2.getKromosom());
            }

            children[0].mutasi(config.mutationRate());
            children[1].mutasi(config.mutationRate());

            nextPopulation.addIndividu(children[0]);
            if (nextPopulation.getPopulationSize() < config.maxPopulationSize()) {
                nextPopulation.addIndividu(children[1]);
            }
        }
        nextPopulation.sortPopulation();
        return nextPopulation;
    }
    
    private boolean cekKonvergensi() {
        if (riwayatFitnessPopulasi.size() < config.convergenceWindow()) {
            return false;
        }
        
        int start = riwayatFitnessPopulasi.size() - config.convergenceWindow();
        int end = riwayatFitnessPopulasi.size();
        
        double maxFitness = Double.NEGATIVE_INFINITY;
        double minFitness = Double.POSITIVE_INFINITY;
        
        for (int i = start; i < end; i++) {
            double fitness =  riwayatFitnessPopulasi.get(i);
            maxFitness = Math.max(maxFitness, fitness);
            minFitness = Math.min(minFitness, fitness);
        }
        
        double perbedaan = Math.abs(maxFitness - minFitness);
        return perbedaan <= config.convergenceThreshold();
    }

    private void printBestIndividu(Individu bestIndividu) {
        System.out.println("Fitness terbaik: " + bestIndividu.getFitness());
        mosaic.printSolution(bestIndividu.getKromosom());
        System.out.println();
    }
}
