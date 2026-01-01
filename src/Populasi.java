import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

public class Populasi {
    private final Mosaic mosaic;
    private final Random random;
    private final int populationSize;
    private List<Individu> daftarIndividu;
    private double fitnessRataRata;

    public Populasi(int populationSize, Mosaic mosaic, Random random) {
        this.mosaic = mosaic;
        this.random = random;
        this.populationSize = populationSize;
        this.daftarIndividu = new ArrayList<>();
    }

    public void initPopulasi() {
        for (int i = 0; i < populationSize; i++) {
            Individu individu = new Individu(random, mosaic);
            individu.hitungFitness();
            daftarIndividu.add(individu);
        }
    }
    
    public void hitungFitnessRataRata() {
        double total = 0;
        for (Individu individu : daftarIndividu) {
            total += individu.getFitness();
        }
        this.fitnessRataRata = total / daftarIndividu.size();
    }
    
    public void sortPopulation() {
        Collections.sort(daftarIndividu, Collections.reverseOrder());
    }
    
    public Individu seleksiRoulette() {
        double fitnessMin = Collections.min(daftarIndividu,
            Comparator.comparingDouble(Individu::getFitness)).getFitness();
        double offset = fitnessMin < 0 ? -fitnessMin + 1 : 1;
        
        double totalFitness = 0;
        for (Individu individu : daftarIndividu) {
            totalFitness += (individu.getFitness() + offset);
        }
        
        // Putaran roulette
        double roda = random.nextDouble() * totalFitness;
        double total = 0;
        
        for (Individu individu : daftarIndividu) {
            total += (individu.getFitness() + offset);
            if (total >= roda) {
                return individu;
            }
        }
        
        // Fallback: kembalikan individu pertama
        return daftarIndividu.get(0);
    }
    
    public Individu seleksiRank() {
        urutkanBerdasarkanFitness();
        int n = daftarIndividu.size();
        
        // Probabilitas berdasarkan rank (individu terbaik rank tertinggi)
        double totalRank = n * (n + 1) / 2.0;
        double roda = random.nextDouble() * totalRank;
        double total = 0;
        
        for (int i = 0; i < n; i++) {
            total += (i + 1); // Rank dimulai dari 1
            if (total >= roda) {
                return daftarIndividu.get(i);
            }
        }
        
        return daftarIndividu.get(0);
    }
    
    public Individu seleksiTournament(int ukuranTurnamen) {
        Individu terbaik = null;
        double fitnessTerbaik = Double.NEGATIVE_INFINITY;
        
        for (int i = 0; i < ukuranTurnamen; i++) {
            int indeks = random.nextInt(daftarIndividu.size());
            Individu kandidat = daftarIndividu.get(indeks);
            
            if (kandidat.getFitness() > fitnessTerbaik) {
                fitnessTerbaik = kandidat.getFitness();
                terbaik = kandidat;
            }
        }
        
        return terbaik;
    }
    
    public Individu getIndividuTerbaik() {
        return daftarIndividu.get(0);
    }

    public List<Individu> getDaftarIndividu() {
        return daftarIndividu;
    }
    
    public double getFitnessRataRata() {
        return fitnessRataRata;
    }
}