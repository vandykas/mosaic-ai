import java.util.List;
import java.util.Random;

/**
 * Mengimplementasikan berbagai strategi seleksi untuk Algoritma Genetika.
 * Digunakan untuk memilih parents untuk generasi berikutnya berdasarkan fitness.
 * <p>
 * <strong>Catatan:</strong> Berbagai strategi seleksi (Roulette, Rank, Tournament)
 * disediakan untuk eksperimen, menemukan yang paling efektif.
 *
 * @author Marco
 */
public class SelectionStrategy {
    private final Random random;
    private final List<Individu> population;

    /**
     * Membangun Strategi Seleksi baru.
     *
     * @param random Generator angka acak.
     * @param population Populasi saat ini untuk dipilih.
     */
    public SelectionStrategy(Random random, List<Individu> population) {
        this.random = random;
        this.population = population;
    }

    /**
     * Memilih satu individu dari populasi menggunakan strategi seleksi roulette wheel.
     *
     * <p>Cara kerja roulette wheel selection adalah setiap individu memiliki peluang
     * terpilih yang sebanding dengan nilai fitness-nya. Semakin tinggi fitness,
     * semakin besar bagian individu pada roda roulette.</p>
     *
     * @return Individu terpilih dari populasi
     */
    public Individu seleksiRoulette() {
        double totalFitness = 0;
        for (Individu individu : population) {
            totalFitness += individu.getFitness();
        }

        double roda = random.nextDouble() * totalFitness;
        double total = 0;

        for (Individu individu : population) {
            total += individu.getFitness();
            if (total >= roda) {
                return individu;
            }
        }
        return population.getFirst();
    }

    /**
     * Memilih satu individu dari populasi menggunakan strategi seleksi berbasis peringkat (rank).
     *
     * <p>Cara kerja rank selection:
     * <ol>
     *   <li>Individu diurutkan berdasarkan fitness dari yang terbaik hingga terburuk.</li>
     *   <li>Setiap individu diberi peluang sebanding dengan rank nya. Individu terbaik memiliki rank tertinggi.</li>
     *   <li>Proses seleksi mirip roulette wheel, tapi menggunakan rank sebagai pengganti fitness.</li>
     * </ol>
     * </p>
     *
     * @return Individu terpilih dari populasi
     */
    public Individu seleksiRank() {
        int n = population.size();

        double totalRank = n * (n + 1) / 2.0;
        double roda = random.nextDouble() * totalRank;

        double total = 0;
        for (int i = 0; i < n; i++) {
            int rank = n - i;
            total += rank;
            if (total >= roda) {
                return population.get(i);
            }
        }
        return population.getFirst();
    }

    /**
     * Memilih satu individu dari populasi menggunakan strategi seleksi turnamen.
     *
     * <p>Cara kerja tournament selection:
     * <ol>
     *   <li>Ambil sejumlah individu secara acak sesuai {@code ukuranTurnamen}.</li>
     *   <li>Bandingkan nilai fitness mereka dan pilih individu dengan fitness tertinggi.</li>
     * </ol>
     * </p>
     *
     * @param ukuranTurnamen Jumlah individu yang diikutkan dalam satu turnamen
     * @return Individu terpilih dari turnamen
     */
    public Individu seleksiTournament(int ukuranTurnamen) {
        Individu terbaik = null;
        double fitnessTerbaik = Double.NEGATIVE_INFINITY;

        for (int i = 0; i < ukuranTurnamen; i++) {
            int indeks = random.nextInt(population.size());
            Individu kandidat = population.get(indeks);

            if (kandidat.getFitness() > fitnessTerbaik) {
                fitnessTerbaik = kandidat.getFitness();
                terbaik = kandidat;
            }
        }
        return terbaik;
    }
}