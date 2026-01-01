import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

/**
 * Kelas untuk merepresentasikan populasi dari individu-individu
 */
public class Populasi {
    private final Mosaic mosaic;
    private final Random random;
    private List<Individu> daftarIndividu;
    private double fitnessRataRata;

    /**
     * Konstruktor untuk membuat populasi baru
     * @param populasi Jumlah individu dalam populasi
     * @param ukuranGrid Ukuran grid puzzle
     * @param mosaic Objek Mosaic
     * @param random Generator acak
     */
    public Populasi(int populasi, int ukuranGrid, Mosaic mosaic, Random random) {
        this.mosaic = mosaic;
        this.random = random;
        this.daftarIndividu = new ArrayList<>();
        
        // Inisialisasi populasi dengan individu acak menggunakan heuristik
        for (int i = 0; i < populasi; i++) {
            Individu individu = new Individu(ukuranGrid, random, mosaic);
            individu.hitungFitness(mosaic);
            daftarIndividu.add(individu);
        }
        
        hitungFitnessRataRata();
        urutkanBerdasarkanFitness();
    }
    
    /**
     * Konstruktor untuk membuat populasi dari daftar individu
     * @param daftarIndividu Daftar individu
     * @param mosaic Objek Mosaic
     * @param random Generator acak
     */
    public Populasi(List<Individu> daftarIndividu, Mosaic mosaic, Random random) {
        this.mosaic = mosaic;
        this.random = random;
        this.daftarIndividu = daftarIndividu;
        
        hitungFitnessRataRata();
        urutkanBerdasarkanFitness();
    }
    
    /**
     * Menghitung fitness rata-rata populasi
     */
    public void hitungFitnessRataRata() {
        double total = 0;
        for (Individu individu : daftarIndividu) {
            total += individu.getFitness();
        }
        this.fitnessRataRata = total / daftarIndividu.size();
    }
    
    /**
     * Mengurutkan individu berdasarkan fitness (descending)
     */
    public void urutkanBerdasarkanFitness() {
        Collections.sort(daftarIndividu, new Comparator<Individu>() {
            @Override
            public int compare(Individu i1, Individu i2) {
                return Double.compare(i2.getFitness(), i1.getFitness());
            }
        });
    }
    
    /**
     * Seleksi orangtua menggunakan metode roulette wheel
     * @return Individu terpilih
     */
    public Individu seleksiRoulette() {
        // Hitung total fitness (setelah normalisasi agar positif)
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
    
    /**
     * Seleksi orangtua menggunakan metode rank
     * @return Individu terpilih
     */
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
    
    /**
     * Seleksi orangtua menggunakan metode tournament
     * @param ukuranTurnamen Ukuran turnamen
     * @return Individu terpilih
     */
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
    
    /**
     * Mengganti seluruh populasi dengan populasi baru
     * @param populasiBaru Populasi baru
     */
    public void gantiPopulasi(List<Individu> populasiBaru) {
        this.daftarIndividu = new ArrayList<>(populasiBaru);
        hitungFitnessRataRata();
        urutkanBerdasarkanFitness();
    }
    
    /**
     * Mendapatkan individu terbaik
     * @return Individu dengan fitness tertinggi
     */
    public Individu getIndividuTerbaik() {
        return daftarIndividu.get(0);
    }
    
    
    public List<Individu> getDaftarIndividu() {
        return daftarIndividu;
    }
    
    public double getFitnessRataRata() {
        return fitnessRataRata;
    }
    
    public int getUkuran() {
        return daftarIndividu.size();
    }
}