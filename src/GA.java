import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Kelas utama algoritma genetika
 */
public class GA {
    private final Mosaic mosaic;
    private final Random random;
    private final int maxPopulationSize;
    private final double mutation_rate;
    private final double elitism_rate;
    private final int max_generation;
    private final double convergence_treshold;
    private final int convergence_window;
    private Individu individuTerbaik;
    private List<Double> riwayatFitnessPopulasi;
    
    public GA(Mosaic mosaic, int maxPopulationSize, double mutation_rate,
              double elitism_rate, int max_generation, double convergence_threshold,
              int convergence_window, int seed) {
        
        this.mosaic = mosaic;
        this.maxPopulationSize = populasi;
        this.mutation_rate = mutation_rate;
        this.elitism_rate = elitism_rate;
        this.max_generation = max_generation;
        this.convergence_treshold = convergence_threshold;
        this.convergence_window = convergence_window;
        this.random = new Random(seed);
        this.riwayatFitnessPopulasi = new ArrayList<>();
    }

    public void jalankan() {
        Populasi currPopulation = initPopulasi();
        individuTerbaik = currPopulation.getIndividuTerbaik();

        int generasi = 0;
        boolean konvergen = false;
        while (generasi < max_generation && !konvergen) {
            Populasi nextPopulation = buatGenerasiBaru();
            
            Individu terbaikSaatIni = nextPopulation.getIndividuTerbaik();
            if (terbaikSaatIni.getFitness() > individuTerbaik.getFitness()) {
                individuTerbaik = terbaikSaatIni;
            }
            
            riwayatFitnessPopulasi.add(nextPopulation.getFitnessRataRata());
            if (generasi >= convergence_window) {
                konvergen = cekKonvergensi();
            }
            
            if (generasi % 1000 == 0) {
                System.out.println("Generasi " + generasi +
                    " - Fitness terbaik: " + individuTerbaik.getFitness() +
                    " - Rata-rata: " + nextPopulation.getFitnessRataRata());
            }
            
            if (individuTerbaik.getFitness() == 0) {
                System.out.println("Solusi sempurna ditemukan pada generasi " + generasi);
                break;
            }
            generasi++;
        }
        
        System.out.println("Algoritma selesai. Generasi terakhir: " + generasi);
        System.out.println("Fitness terbaik: " + individuTerbaik.getFitness());
    }

    private Populasi initPopulasi() {
        Populasi population = new Populasi(maxPopulationSize, mosaic, random);
        population.initPopulasi();
        population.sortPopulation();
        return population;
    }
    
    private Populasi buatGenerasiBaru(Populasi currPopulation) {
        Populasi nextPopulation = currPopulation.initPopulasiWithElitism(maxPopulationSize);
        while (nextPopulation.getPopulationSize() < maxPopulationSize) {
            Individu parent1 = currPopulation.seleksiRoulette();
            Individu parent2 = currPopulation.seleksiRoulette();

            Individu[] children = parent1.singlePointCrossover(parent2);
            children[0].mutasi(mutation_rate);
            children[1].mutasi(mutation_rate);

            nextPopulation.addIndividu(children[0]);
            if (nextPopulation.getPopulationSize() < maxPopulationSize) {
                nextPopulation.addIndividu(children[1]);
            }
        }
        return nextPopulation;
    }
    
    private boolean cekKonvergensi() {
        if (riwayatFitnessPopulasi.size() < convergence_window) {
            return false;
        }
        
        // Ambil jendela terakhir
        List<Double> jendela = riwayatFitnessPopulasi.subList(
            riwayatFitnessPopulasi.size() - convergence_window,
            riwayatFitnessPopulasi.size());
        
        // Cari nilai maksimum dan minimum
        double maks = Double.NEGATIVE_INFINITY;
        double min = Double.POSITIVE_INFINITY;
        
        for (double fitness : jendela) {
            if (fitness > maks) maks = fitness;
            if (fitness < min) min = fitness;
        }
        
        // Hitung perbedaan
        double perbedaan = Math.abs(maks - min);
        
        return perbedaan <= convergence_treshold;
    }
    
    public Individu getIndividuTerbaik() {
        return individuTerbaik;
    }
}
