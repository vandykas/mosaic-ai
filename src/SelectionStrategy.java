import java.util.List;
import java.util.Random;

/**
 * Mengimplementasikan berbagai strategi seleksi untuk Algoritma Genetika.
 * Digunakan untuk memilih parents untuk generasi berikutnya berdasarkan fitness.
 * <p>
 * <strong>Catatan:</strong> Berbagai strategi seleksi (Roulette, Rank, Tournament)
 * disediakan untuk eksperimen, menemukan yang paling efektif.
 *
 * @author TODO to be filled
 */
public class SelectionStrategy {
    private final Random random;
    private final List<Individu> population;

    /**
     * Membangun Strategi Seleksi baru.
     *
     * @param random     Generator angka acak.
     * @param population Populasi saat ini untuk dipilih.
     */
    public SelectionStrategy(Random random, List<Individu> population) {
        this.random = random;
        this.population = population;
    }

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