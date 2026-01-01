import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

public class Populasi {
    private final Mosaic mosaic;
    private final Random random;
    private final int maxPopulationSize;
    private final List<Individu> daftarIndividu;
    private int populationSize;
    private double fitnessRataRata;

    public Populasi(int maxPopulationSize, Mosaic mosaic, Random random) {
        this.mosaic = mosaic;
        this.random = random;
        this.maxPopulationSize = maxPopulationSize;
        this.daftarIndividu = new ArrayList<>();
    }

    public int getPopulationSize() {
        return populationSize;
    }

    public void initPopulasi() {
        for (int i = 0; i < populationSize; i++) {
            Individu individu = new Individu(random, mosaic);
            individu.hitungFitness();
            daftarIndividu.add(individu);
        }
    }

    public Populasi initPopulasiWithElitism(double elitismRate) {
        int elitismCount = (int) (maxPopulationSize * elitismRate);
        Populasi nextPop = new Populasi(maxPopulationSize, mosaic, random);
        for (int i = 0; i < elitismCount; i++) {
            nextPop.addIndividu(daftarIndividu.get(i));
        }
        return nextPop;
    }

    public void addIndividu(Individu individu) {
        this.daftarIndividu.add(individu);
        this.populationSize++;
    }

    public void hitungFitnessRataRata() {
        double total = 0;
        for (Individu individu : daftarIndividu) {
            total += individu.getFitness();
        }
        this.fitnessRataRata = total / daftarIndividu.size();
    }
    
    public void sortPopulation() {
        daftarIndividu.sort(Collections.reverseOrder());
    }
    
    public Individu seleksiRoulette() {
        double totalFitness = 0;
        for (Individu individu : daftarIndividu) {
            totalFitness += individu.getFitness();
        }
        
        double roda = random.nextDouble() * totalFitness;
        double total = 0;
        
        for (Individu individu : daftarIndividu) {
            total += individu.getFitness();
            if (total >= roda) {
                return individu;
            }
        }
        return daftarIndividu.get(0);
    }
    
    public Individu seleksiRank() {
        int n = daftarIndividu.size();
        
        double totalRank = n * (n + 1) / 2.0;
        double roda = random.nextDouble() * totalRank;

        double total = 0;
        for (int i = 0; i < n; i++) {
            int rank = n - i;
            total += rank;
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