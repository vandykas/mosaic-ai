/**
 * Record konfigurasi untuk Algoritma Genetika.
 * Menyimpan semua hyperparameter yang digunakan untuk mengontrol proses evolusi.
 * <p>
 * <strong>Catatan:</strong> Nilai optimal untuk hyperparameter ini belum ditentukan secara eksperimental.
 *
 * @param maxPopulationSize Ukuran maksimum individu dalam populasi.
 * @param mutationRate      Probabilitas gen diubah (flip) selama mutasi.
 * @param elitismRate       Persentase individu terbaik yang dibawa tanpa perubahan ke generasi berikutnya.
 * @param crossoverRate     Probabilitas terjadinya crossover antara dua orang tua.
 * @param maxGeneration     Jumlah maksimum generasi yang akan dijalankan.
 * @param convergenceThreshold Ambang batas varians fitness untuk menganggap populasi telah konvergen.
 * @param convergenceWindow    Jumlah generasi yang dilihat ke belakang untuk pengecekan konvergensi.
 * @param heuristicRate     Persentase populasi awal yang diisi dengan solusi heuristik.
 * @param alphaStart        Nilai awal untuk pembobotan diversitas dalam fungsi fitness.
 * @param repetisi          Jumlah pengulangan seluruh proses GA untuk ketahanan statistik.
 * @author TODO to be filled
 */
public record GAConfig(int maxPopulationSize, double mutationRate, double elitismRate, double crossoverRate,
                       int maxGeneration, double convergenceThreshold, int convergenceWindow,
                       double heuristicRate, double alphaStart, int repetisi) {
}
