import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Menghitung probabilitas setiap cell yang tidak diketahui (unknown) sebagai hitam.
 * Probabilitas dihitung berdasarkan pengaruh petunjuk (clue) di sekitar cell tersebut.
 *
 * @author Marco
 */
public class ProbabilityCalculator {
    /** Panjang board puzzle mosaic */
    private final int ukuran;
    /** List lokasi cell yang masih unknown */
    private final List<Cell> unknownCells;
    /** Solusi parsial hasil heuristik deterministik */
    private final CellState[][] partialSolution;
    /** Board puzzle mosaic */
    private final int[][] clue;

    /**
     * Membangun ProbabilityCalculator baru.
     *
     * @param ukuran Ukuran grid (N x N).
     * @param unknownCells Daftar cell yang statusnya belum diketahui.
     * @param partialSolution Solusi parsial saat ini dari state grid.
     * @param clue Array 2D petunjuk di papan.
     */
    public ProbabilityCalculator(int ukuran, List<Cell> unknownCells, CellState[][] partialSolution, int[][] clue) {
        this.ukuran = ukuran;
        this.unknownCells = unknownCells;
        this.partialSolution = partialSolution;
        this.clue = clue;
    }

    /**
     * Menghitung probabilitas setiap cell unknown untuk menjadi cell hitam
     * berdasarkan pengaruh clue di sekitarnya.
     *
     * <p>Setiap cell unknown akan dievaluasi terhadap clue tetangganya, lalu
     * hasil pengaruh tersebut dijumlahkan dan di normalisasi agar berada
     * dalam rentang [0, 1].</p>
     *
     * @return Array probabilitas untuk setiap cell unknown
     */
    public double[] calculateProbability() {
        int unknownCount = unknownCells.size();
        double[] probability = new double[unknownCount];

        // Menghitung kontribusi clue terhadap cell unknown
        for (int i = 0; i < unknownCount; i++) {
            addClueInfluence(probability, i, unknownCells.get(i).row(), unknownCells.get(i).col());
        }

        // Normalisasi agar nilai peluang dalam rentang [0, 1]
        normalize(probability);
        return probability;
    }

    /**
     * Menambahkan pengaruh clue di sekitar sebuah cell unknown ke nilai probabilitasnya.
     *
     * <p>Pengaruh dihitung berdasarkan cellisih jumlah cell hitam yang masih dibutuhkan
     * oleh clue dengan jumlah cell unknown di sekitarnya.</p>
     *
     * @param probability Array probabilitas yang akan diperbarui
     * @param idx Indeks cell unknown dalam array probabilitas
     * @param row Baris cell unknown
     * @param col Kolom cell unknown
     */
    private void addClueInfluence(double[] probability, int idx, int row, int col) {
        ArrayList<Cell> neighbors = GridHelper.getNeighbors(row, col, ukuran);
        for (Cell cell : neighbors) {
            int clueVal = clue[cell.row()][cell.col()];
            if (clueVal != -1) {
                int blackCount = GridHelper.countNeighborsSpecificCell(partialSolution, cell.row(),
                        cell.col(), CellState.BLACK, ukuran);
                int unknownCount = GridHelper.countNeighborsSpecificCell(partialSolution, cell.row(),
                        cell.col(), CellState.UNKNOWN, ukuran);

                // Cell hitam dibutuhkan dibagi 3.5 agar peluang hitam tidak terlalu besar
                int remainingBlack = clueVal - blackCount;
                double p = (remainingBlack / 3.5) / unknownCount;
                probability[idx] += p;
            }
        }
    }

    /**
     * Normalisasi setiap peluang dalam array dengan menggunakan metode min-max scaling agar
     * tidak ada informasi yang hilang.
     *
     * @param probability Array peluang cell hitam yang belum di normalisasi
     */
    private void normalize(double[] probability) {
        double min = Double.POSITIVE_INFINITY;
        double max = Double.NEGATIVE_INFINITY;

        int unknownCount = unknownCells.size();
        for (double prob : probability) {
            min = Math.min(min, prob);
            max = Math.max(max, prob);
        }

        // Jika peluang tertinggi dan terendah sama, samakan semua peluang setiap cell menjadi 50%
        if (max == min) {
            Arrays.fill(probability, 0.5);
        }
        else {
            for (int i = 0; i < unknownCount; i++) {
                probability[i] = (probability[i] - min) / (max - min);
            }
        }
    }
}
