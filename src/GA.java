import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Kelas utama dari Algoritma Genetika.
 * Mensimulasikan algoritma genetika mulai dari inisialisasi hingga konvergen.
 *
 * <p>Algoritma berlangsung melalui inisialisasi generasi, seleksi, crossover, dan mutasi
 * sampai konvergen atau jumlah generasi maksimum tercapai.<p>
 *
 * @author Marco, Vandyka
 */
public class GA {
    /** Objek mosaic yang berisi segala hal berkaitan puzzle Mosaic */
    private final Mosaic mosaic;

    /** Generator random yang akan diisi seed tertentu */
    private Random random;

    /** Konfigurasi GA untuk menyimpan hyperparameter */
    private final GAConfig config;

    /** Riwayat fitness populasi untuk pengecekan konvergensi */
    private List<Double> riwayatFitnessPopulasi;

    /**
     * Membangun instance Algoritma Genetika baru.
     *
     * @param mosaic Instance puzzle yang akan diselesaikan.
     * @param config Parameter konfigurasi untuk GA.
     */
    public GA(Mosaic mosaic, GAConfig config) {
        this.mosaic = mosaic;
        this.config = config;
        this.riwayatFitnessPopulasi = new ArrayList<>();
    }

    /**
     * Membuat objek random baru dengan seed yang ditentukan.
     *
     * @param seed Seed untuk objek random agar eksperimen bisa dibuat kembali
     */
    public void setRandom(int seed) {
        this.random = new Random(seed);
    }

    /**
     * Menjalankan algoritma genetika sebanyak repetisi yang ditentukan.
     * Setiap selesai jalan, individu terbaik generasi ditampilkan di output
     * lalu di akhir repetisi, individu terbaik seluruh repetisi ditampilkan juga.
     */
    public void run() {
        Individu bestOverallIndividu = null;
        for (int r = 0; r < config.repetisi(); r++) {
            System.out.println("=== Repetisi ke-" + (r + 1) + " ===");

            setRandom(r);
            Individu solusiTerbaik = simulate();
            printBestIndividu(solusiTerbaik);

            bestOverallIndividu = (bestOverallIndividu == null) ? solusiTerbaik : compareIndividu(bestOverallIndividu, solusiTerbaik);
        }
        System.out.println("=== Individu Terbaik seluruh repetisi ===");
        printBestIndividu(bestOverallIndividu);
    }

    /**
     * Simulasi algoritma genetika dari inisialisasi generasi, seleksi, crossover, dan
     * mutasi hingga konvergen atau mencapai maksimum generasi.
     *
     * @return Individu terbaik seluruh generasi
     */
    private Individu simulate() {
        // Inisialisasi populasi awal
        Populasi currPopulation = initPopulasi();
        Individu individuTerbaik = currPopulation.getIndividuTerbaik();

        /*
         * Selama belum konvergen dan belum mencapai maksimum generasi:
         * - Buat generasi baru
         * - Cari dan bandingkan individu terbaik generasi baru
         * - Cek konvergen jika sudah mencapai window konvergen
         */
        int generasi = 0;
        boolean konvergen = false;
        while (generasi < config.maxGeneration() && !konvergen) {
            Populasi nextPopulation = buatGenerasiBaru(currPopulation, generasi);

            Individu terbaikSaatIni = nextPopulation.getIndividuTerbaik();
            individuTerbaik = compareIndividu(individuTerbaik, terbaikSaatIni);

            // Pengecekan konvergen dilakukan menggunakan rata-rata fitness populasi
            riwayatFitnessPopulasi.add(nextPopulation.hitungFitnessRataRata());
            if (generasi >= config.convergenceWindow()) {
                konvergen = cekKonvergensi();
            }

            currPopulation = nextPopulation;
            generasi++;
        }
        return individuTerbaik;
    }

    /**
     * Membandingkan nilai fitness 2 individu.
     *
     * @param individuTerbaik Individu terbaik keseluruhan
     * @param terbaikSaatIni Individu terbaik 1 generasi
     * @return Individu yang memiliki nilai fitness lebih besar
     */
    private Individu compareIndividu(Individu individuTerbaik, Individu terbaikSaatIni) {
        if (terbaikSaatIni.getFitness() > individuTerbaik.getFitness()) {
            return terbaikSaatIni;
        }
        return individuTerbaik;
    }

    /**
     * Inisialisasi populasi awal dengan heuristik probabilistik dan
     * acak lalu menghitung fitness semua individu dalam populasi.
     *
     * @return Populasi awal algoritma genetika
     */
    private Populasi initPopulasi() {
        // Inisialisasi populasi awal dengan kombinasi heuristik dan random
        Populasi population = new Populasi(config.maxPopulationSize(), mosaic, random);
        population.initPopulasi(config.heuristicRate());

        /*
         * Menghitung fitness semua individu dalam populasi.
         * Ada 2 cara perhitungan fitness:
         * 1. Fitness dasar tanpa pertimbangan diversity
         * 2. Fitness dengan pertimbangan diversity
         *
         * Alpha digunakan untuk mengatur bobot bonus diversity dan akan terus berkurang seiring
         * generasi bertambah agar tidak eksplorasi terus.
         */
        double alpha = config.alphaStart() * (1.0 - 1.0 / config.maxGeneration());
        population.fillAverage();
        population.calculatePopulationFitnessWithDiversity(alpha);
//        population.calculatePopulationFitness();

        // Urutkan individu berdasarkan nilai fitness dari terbesar ke terkecil
        population.sortPopulation();
        return population;
    }

    /**
     * Pembuatan generasi baru terdiri dari beberapa tahap:
     * 1. Isi populasi baru dengan elitism
     * 2. Untuk sisanya dilakukan seleksi dan crossover untuk menentukan parent dan membuat offspring
     * 3. Lakukan mutasi untuk anak hasil crossover
     * 4. Menghitung fitness semua individu dalam populasi
     *
     * @param currPopulation Isi populasi sekarang
     * @param generasi Angka generasi ke berapa
     * @return Populasi baru berdasarkan populasi sebelumnya
     */
    private Populasi buatGenerasiBaru(Populasi currPopulation, int generasi) {
        // Isi sebagian populasi baru dengan elitism populasi sebelum
        Populasi nextPopulation = currPopulation.initPopulasiWithElitism(config.elitismRate());

        // Isi sisa populasi melalui seleksi dan crossover
        while (nextPopulation.getPopulationSize() < config.maxPopulationSize()) {

            // Melakukan seleksi untuk mendapatkan parent yang akan di silangkan
            Individu parent1 = currPopulation.seleksiTournament(16);
            Individu parent2 = currPopulation.seleksiTournament(16);

            // Persilangan hanya terjadi ketika memenuhi peluang crossover
            if (random.nextDouble() < config.crossoverRate()) {
                Individu[] children = parent1.subGridBasedCrossover(parent2);

                // Anak hasil persilangan mengalami mutasi untuk eksplorasi
                children[0].mutasi(config.mutationRate());
                children[1].mutasi(config.mutationRate());

                // Masukkan kedua anak ke dalam populasi jika populasi masih mencukupi
                nextPopulation.addIndividu(children[0]);
                if (nextPopulation.getPopulationSize() < config.maxPopulationSize()) {
                    nextPopulation.addIndividu(children[1]);
                }
            }
        }

        /*
         * Menghitung fitness semua individu dalam populasi.
         * Ada 2 cara perhitungan fitness:
         * 1. Fitness dasar tanpa pertimbangan diversity
         * 2. Fitness dengan pertimbangan diversity
         *
         * Alpha digunakan untuk mengatur bobot bonus diversity dan akan terus berkurang seiring
         * generasi bertambah agar tidak eksplorasi terus.
         */
        nextPopulation.fillAverage();
        double alpha = config.alphaStart() * (1.0 - (double) generasi / config.maxGeneration());
        nextPopulation.calculatePopulationFitnessWithDiversity(alpha);
//        nextPopulation.calculatePopulationFitness();

        // Urutkan populasi berdasarkan nilai fitness dari terbesar ke terkecil
        nextPopulation.sortPopulation();
        return nextPopulation;
    }

    /**
     * Melakukan cek apakah algoritma genetika sudah konvergen atau belum
     * dengan membandingkan selisih nilai fitness terbesar dan terkecil
     * dalam window dengan threshold yang sudah ditentukan.
     *
     * @return True atau false algoritma genetika sudah konvergen atau belum
     */
    private boolean cekKonvergensi() {
        // Menghindari error dimana window konvergen lebih besar dari riwayat fitness
        if (riwayatFitnessPopulasi.size() < config.convergenceWindow()) {
            return false;
        }

        // Mencari awal window dan akhir window untuk di cek
        int start = riwayatFitnessPopulasi.size() - config.convergenceWindow();
        int end = riwayatFitnessPopulasi.size();

        // Inisialisasi maksimal fitness dan minimum fitness
        double maxFitness = Double.NEGATIVE_INFINITY;
        double minFitness = Double.POSITIVE_INFINITY;

        // Mencari dari awal window hingga akhir fitness terbesar dan terkecil
        for (int i = start; i < end; i++) {
            double fitness =  riwayatFitnessPopulasi.get(i);
            maxFitness = Math.max(maxFitness, fitness);
            minFitness = Math.min(minFitness, fitness);
        }

        /*
         * Jika selisih fitness terbesar dan terkecil lebih kecil atau sama dengan
         * threshold, maka algoritma genetika dianggap sudah konvergen.
         */
        double perbedaan = Math.abs(maxFitness - minFitness);
        return perbedaan <= config.convergenceThreshold();
    }

    /**
     * Print nilai fitness dan individu untuk pengecekan hasil dan solusi yang ditemukan
     * algoritma.
     *
     * @param bestIndividu Individu terbaik yang ingin dilihat nilai fitness dan solusinya
     */
    private void printBestIndividu(Individu bestIndividu) {
        System.out.println("Fitness terbaik: " + bestIndividu.getFitness());
        mosaic.printSolution(bestIndividu.getKromosom());
        System.out.println();
    }
}
