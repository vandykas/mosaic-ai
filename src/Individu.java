import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * Kelas untuk merepresentasikan satu individu (solusi) dalam populasi
 */
public class Individu {
    private int ukuran;
    private List<Integer> kromosom; // Daftar indeks sel hitam (0 sampai ukuran*ukuran-1)
    private double fitness;
    private Random random;
    
    /**
     * Konstruktor untuk membuat individu baru dengan heuristik
     * @param ukuran Ukuran grid
     * @param random Generator acak
     * @param mosaic Objek Mosaic untuk heuristik
     */
    public Individu(int ukuran, Random random, Mosaic mosaic) {
        this.ukuran = ukuran;
        this.random = random;
        this.kromosom = new ArrayList<>();
        this.fitness = Double.NEGATIVE_INFINITY;
        
        inisialisasiDenganHeuristik(mosaic);
    }
    
    /**
     * Konstruktor untuk membuat individu dari kromosom yang sudah ada
     * @param ukuran Ukuran grid
     * @param kromosom Kromosom yang sudah ada (daftar indeks)
     */
    public Individu(int ukuran, List<Integer> kromosom, Random random) {
        this.ukuran = ukuran;
        // Salin kromosom dan hapus duplikat
        this.kromosom = new ArrayList<>(new HashSet<>(kromosom));
        Collections.sort(this.kromosom);
        this.fitness = Double.NEGATIVE_INFINITY;
        this.random = random;
    }
    
    /**
     * Menginisialisasi kromosom dengan heuristik berdasarkan clue
     * @param mosaic Objek Mosaic yang berisi clue
     */
    private void inisialisasiDenganHeuristik(Mosaic mosaic) {
        int[][] clue = mosaic.getclue();
        double[][] probabilitas = new double[ukuran][ukuran];
        
        // Inisialisasi probabilitas dengan 0
        for (int i = 0; i < ukuran; i++) {
            for (int j = 0; j < ukuran; j++) {
                probabilitas[i][j] = 0.0;
            }
        }
        
        // Proses setiap clue
        for (int i = 0; i < ukuran; i++) {
            for (int j = 0; j < ukuran; j++) {
                if (clue[i][j] != -1) {
                    tambahkanPengaruhclue(probabilitas, i, j, clue[i][j]);
                }
            }
        }
        
        // Tambahkan sel hitam berdasarkan probabilitas
        for (int i = 0; i < ukuran; i++) {
            for (int j = 0; j < ukuran; j++) {
                // probabilitas ada kemungkinan lebih dari satu, jadi di clip
                double prob = Math.min(probabilitas[i][j], 1.0);
                
                if (random.nextDouble() < prob) {
                    int indeks = i * ukuran + j;
                    kromosom.add(indeks);
                }
            }
        }
        
        // Hapus duplikat (jika ada) dan urutkan
        kromosom = new ArrayList<>(new HashSet<>(kromosom));
        Collections.sort(kromosom);
    }
    
    /**
     * Menambahkan pengaruh clue ke grid probabilitas
     * @param probabilitas Grid probabilitas
     * @param barisclue Baris clue
     * @param kolomclue Kolom clue
     * @param nilaiclue Nilai clue (0-9)
     */
    private void tambahkanPengaruhclue(double[][] probabilitas, int barisclue, int kolomclue, int nilaiclue) {
        // Tentukan area sekitar clue (termasuk clue itu sendiri)
        int barisAwal = Math.max(0, barisclue - 1);
        int barisAkhir = Math.min(ukuran - 1, barisclue + 1);
        int kolomAwal = Math.max(0, kolomclue - 1);
        int kolomAkhir = Math.min(ukuran - 1, kolomclue + 1);
        
        // Hitung jumlah sel di area sekitar
        int jumlahSel = (barisAkhir - barisAwal + 1) * (kolomAkhir - kolomAwal + 1);
        
        // Hitung nilai yang akan ditambahkan ke setiap sel
        double nilaiPerSel = (double) ((nilaiclue / 3.5) / jumlahSel);
            
        // Tambahkan nilai ke setiap sel di area sekitar
        for (int i = barisAwal; i <= barisAkhir; i++) {
            for (int j = kolomAwal; j <= kolomAkhir; j++) {
                probabilitas[i][j] += nilaiPerSel;
            }
        }
    }
    
    /**
     * Mengonversi kromosom (daftar indeks) menjadi grid 2D
     * @return Grid 2D (0=putih, 1=hitam)
     */
    public int[][] toGrid2D() {
        int[][] grid = new int[ukuran][ukuran];
        
        // Inisialisasi semua sel dengan 0 (putih)
        for (int i = 0; i < ukuran; i++) {
            for (int j = 0; j < ukuran; j++) {
                grid[i][j] = 0;
            }
        }
        
        // Setel sel hitam berdasarkan kromosom
        for (int indeks : kromosom) {
            int baris = indeks / ukuran;
            int kolom = indeks % ukuran;
            grid[baris][kolom] = 1;
        }
        
        return grid;
    }
    
    /**
     * Menghitung fitness individu berdasarkan puzzle Mosaic
     * @param mosaic Objek Mosaic yang berisi clue
     */
    public void hitungFitness(Mosaic mosaic) {
        int[][] grid = toGrid2D();
        int totalDeviasi = mosaic.hitungDeviasi(grid);
        this.fitness = -totalDeviasi; // Fitness = -deviasi
    }
    
    /**
     * Melakukan mutasi pada individu
     * @param mutation_rate Probabilitas mutasi
     */
    public void mutasi(double mutation_rate) {
        List<Integer> kromosomBaru = new ArrayList<>();
        
        for (int indeks : kromosom) {
            if (random.nextDouble() < mutation_rate) {
                // Mutasi: pindahkan sel hitam ke salah satu dari 8 tetangga
                int indeksBaru = pindahkanSelHitam(indeks);
                kromosomBaru.add(indeksBaru);
            } else {
                kromosomBaru.add(indeks);
            }
        }
        
        // Ganti kromosom dengan yang baru dan hapus duplikat
        this.kromosom = hapusDuplikat(kromosomBaru);
    }
    
    /**
     * Memindahkan sel hitam ke salah satu dari 8 tetangga
     * @param indeks Indeks sel asal
     * @return Indeks sel tujuan
     */
    private int pindahkanSelHitam(int indeks) {
        int baris = indeks / ukuran;
        int kolom = indeks % ukuran;
        
        // Daftar arah yang mungkin (8 arah)
        int[] dBaris = {-1, -1, -1, 0, 0, 1, 1, 1};
        int[] dKolom = {-1, 0, 1, -1, 1, -1, 0, 1};
        
        // Pilih arah acak
        int arah = random.nextInt(8);
        int barisBaru = baris + dBaris[arah];
        int kolomBaru = kolom + dKolom[arah];
        
        // Periksa batas grid
        if (barisBaru >= 0 && barisBaru < ukuran && 
            kolomBaru >= 0 && kolomBaru < ukuran) {
            return barisBaru * ukuran + kolomBaru;
        }
        
        // Jika di luar batas, tetap di posisi semula
        return indeks;
    }
    
    /**
     * Melakukan crossover single-point dengan individu lain
     * @param pasangan Individu pasangan
     * @return Anak hasil crossover
     */
    public Individu crossover(Individu pasangan) {
        List<Integer> kromosom1 = this.kromosom;
        List<Integer> kromosom2 = pasangan.kromosom;
        
        // Flatten kromosom (sudah flat, jadi langsung digunakan)
        int panjang1 = kromosom1.size();
        int panjang2 = kromosom2.size();
        
        // Jika salah satu kromosom kosong, kembalikan salinan parent
        if (panjang1 == 0 || panjang2 == 0) {
            return new Individu(ukuran, kromosom1, random);
        }
        
        // Pilih titik crossover (bisa di tengah atau tidak)
        int titikCrossover1 = random.nextInt(panjang1);
        int titikCrossover2 = random.nextInt(panjang2);
        
        // Buat kromosom anak
        List<Integer> anakKromosom = new ArrayList<>();
        
        // Ambil bagian pertama dari parent1
        for (int i = 0; i < titikCrossover1; i++) {
            anakKromosom.add(kromosom1.get(i));
        }
        
        // Ambil bagian kedua dari parent2
        for (int i = titikCrossover2; i < panjang2; i++) {
            anakKromosom.add(kromosom2.get(i));
        }
        
        // Hapus duplikat
        anakKromosom = hapusDuplikat(anakKromosom);
        
        return new Individu(ukuran, anakKromosom, random);
    }
    
    /**
     * Menghapus duplikat dari kromosom
     * @param kromosom Kromosom dengan kemungkinan duplikat
     * @return Kromosom tanpa duplikat
     */
    private List<Integer> hapusDuplikat(List<Integer> kromosom) {
        Set<Integer> set = new HashSet<>(kromosom);
        List<Integer> hasil = new ArrayList<>(set);
        Collections.sort(hasil);
        return hasil;
    }
    
    /**
     * Membuat salinan individu
     * @return Salinan individu
     */
    public Individu salin() {
        Individu salinan = new Individu(ukuran, new ArrayList<>(kromosom), random);
        salinan.fitness = this.fitness;
        return salinan;
    }
    
    /**
     * Menampilkan grid solusi
     */
    public void tampilkanGrid() {
        int[][] grid = toGrid2D();
        System.out.println("Grid Solusi:");
        for (int i = 0; i < ukuran; i++) {
            for (int j = 0; j < ukuran; j++) {
                System.out.print(grid[i][j] + " ");
            }
            System.out.println();
        }
    }
    
    /**
     * Menampilkan kromosom (daftar indeks)
     */
    public void tampilkanKromosom() {
        System.out.println("Kromosom: " + kromosom);
    }
    
    public List<Integer> getKromosom() {
        return new ArrayList<>(kromosom);
    }
    
    public double getFitness() {
        return fitness;
    }
    
    public void setFitness(double fitness) {
        this.fitness = fitness;
    }
    
    public int getUkuranGrid() {
        return ukuran;
    }
}