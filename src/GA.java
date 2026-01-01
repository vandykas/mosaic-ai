import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Kelas utama algoritma genetika
 */
public class GA {
    private Mosaic mosaic;
    private int populasi;
    private double mutation_rate;
    private double elitism_rate;
    private int max_generation;
    private double convergence_treshold;
    private int convergence_window;
    private Random random;
    private Populasi populasiSaatIni;
    private Individu individuTerbaik;
    private List<Double> riwayatFitnessPopulasi;
    
    /**
     * Konstruktor algoritma genetika
     */
    public GA(Mosaic mosaic, int populasi, double mutation_rate, 
              double elitism_rate, int max_generation, double convergence_threshold,
              int convergence_window, int seed) {
        
        this.mosaic = mosaic;
        this.populasi = populasi;
        this.mutation_rate = mutation_rate;
        this.elitism_rate = elitism_rate;
        this.max_generation = max_generation;
        this.convergence_treshold = convergence_threshold;
        this.convergence_window = convergence_window;
        this.random = new Random(seed);
        this.riwayatFitnessPopulasi = new ArrayList<>();
        
        // Inisialisasi populasi awal
        this.populasiSaatIni = new Populasi(
            populasi, mosaic.getUkuran(), mosaic, random);
        this.individuTerbaik = populasiSaatIni.getIndividuTerbaik().salin();
    }

    /**
     * Menjalankan algoritma genetika
     */
    public void jalankan() {
        int generasi = 0;
        boolean konvergen = false;
        
        while (generasi < max_generation && !konvergen) {
            // Buat generasi baru
            Populasi generasiBaru = buatGenerasiBaru();
            
            // Ganti populasi lama dengan yang baru
            populasiSaatIni.gantiPopulasi(generasiBaru.getDaftarIndividu());
            
            // Update individu terbaik
            Individu terbaikSaatIni = populasiSaatIni.getIndividuTerbaik();
            if (terbaikSaatIni.getFitness() > individuTerbaik.getFitness()) {
                individuTerbaik = terbaikSaatIni.salin();
            }
            
            // Simpan fitness rata-rata populasi untuk pengecekan konvergensi
            riwayatFitnessPopulasi.add(populasiSaatIni.getFitnessRataRata());
            
            // Cek konvergensi
            if (generasi >= convergence_window) {
                konvergen = cekKonvergensi();
            }
            
            // Cetak progress setiap 1000 generasi
            if (generasi % 1000 == 0) {
                System.out.println("Generasi " + generasi + 
                    " - Fitness terbaik: " + individuTerbaik.getFitness() +
                    " - Rata-rata: " + populasiSaatIni.getFitnessRataRata());
            }
            
            // Cek apakah solusi sempurna ditemukan
            if (individuTerbaik.getFitness() == 0) {
                System.out.println("Solusi sempurna ditemukan pada generasi " + generasi);
                break;
            }
            
            generasi++;
        }
        
        System.out.println("Algoritma selesai. Generasi terakhir: " + generasi);
        System.out.println("Fitness terbaik: " + individuTerbaik.getFitness());
    }
    
    /**
     * Membuat generasi baru dari populasi saat ini
     * @return Populasi baru
     */
    private Populasi buatGenerasiBaru() {
        List<Individu> generasiBaru = new ArrayList<>();
        
        // Elitisme: ambil individu terbaik langsung ke generasi baru
        int jumlahElit = (int) (populasi * elitism_rate);
        populasiSaatIni.urutkanBerdasarkanFitness();
        
        for (int i = 0; i < jumlahElit; i++) {
            generasiBaru.add(populasiSaatIni.getDaftarIndividu().get(i).salin());
        }
        
        // Isi sisa populasi dengan crossover dan mutasi
        while (generasiBaru.size() < populasi) {
            // Pilih metode seleksi
            Individu orangtua1 = populasiSaatIni.seleksiRoulette();
            Individu orangtua2 = populasiSaatIni.seleksiRoulette();
            
            // Crossover
            Individu anak = orangtua1.crossover(orangtua2);
            
            // Mutasi
            anak.mutasi(mutation_rate);
            
            // Hitung fitness anak
            anak.hitungFitness(mosaic);
            
            generasiBaru.add(anak);
        }
        
        return new Populasi(populasi, mosaic.getUkuran(), mosaic, random) {
            {
                this.gantiPopulasi(generasiBaru);
            }
        };
    }
    
    /**
     * Mengecek apakah populasi telah konvergen
     * @return true jika sudah konvergen
     */
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
    
    public Populasi getPopulasiSaatIni() {
        return populasiSaatIni;
    }
}
