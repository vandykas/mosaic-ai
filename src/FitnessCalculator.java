import java.util.List;

/**
 * Menghitung nilai fitness untuk individu berdasarkan performanya dalam memecahkan puzzle
 * Mosaic.
 *
 * <p>Class ini menyediakan beberapa metode perhitungan fitness berdasarkan
 * error dan skor per clue</p>.
 *
 * @author Vandyka
 */
public class FitnessCalculator {
    /** Panjang board puzzle Mosaic N x N */
    private final int ukuran;

    /** List untuk posisi dan nilai clue pada board (clue) */
    private final List<NumCell> numberCells;

    /** Solusi parsial hasil heuristik deterministik */
    private final CellState[][] partialSolution;

    /** List untuk posisi cell yang masih belum diketahui warnanya */
    private final List<Cell> unknownCells;

    /**
     * Membangun FitnessCalculator baru.
     *
     * @param ukuran Ukuran grid (N x N).
     * @param numberCells Daftar sel yang memiliki angka petunjuk.
     * @param partialSolution Solusi parsial saat ini dari state grid.
     * @param unknownCells Daftar sel yang statusnya belum diketahui.
     */
    public FitnessCalculator(int ukuran, List<NumCell> numberCells, CellState[][] partialSolution, List<Cell> unknownCells) {
        this.ukuran = ukuran;
        this.numberCells = numberCells;
        this.partialSolution = partialSolution;
        this.unknownCells = unknownCells;
    }

    /**
     * Menghitung fitness berdasarkan total error seluruh clue.
     *
     * <p>Untuk setiap clue, dihitung selisih antara nilai clue dan banyak cell hitam di sekitarnya.
     * Selisih ini di kuadratkan untuk memberi penalti lebih besar pada error besar dan juga menghilangkan
     * nilai negatif</p>
     *
     * <p>Total error ditransformasikan dengan fungsi invers agar solusi dengan error lebih kecil
     * memiliki nilai fitness lebih besar</p>
     *
     * @param kromosom Solusi yang akan di nilai oleh fitness
     * @return Nilai fitness dalam rentang (0, 1] dimana 1 adalah solusi sempurna
     */
    public double fitnessFunctionByError(boolean[][] kromosom) {
        // Membuat grid solusi untuk mempermudah perhitungan tetangga cell hitam
        CellState[][] gridSolusi = GridHelper.makeSolutionGrid(kromosom, partialSolution, unknownCells);

        /*
         * Untuk setiap clue, cari banyak total cell hitam tetangganya dan cari selisih dengan clue.
         * Selisih ini di kuadratkan lalu dijumlahkan ke totalError untuk menghilangkan angka negatif
         * dan menghukum error lebih besar.
         */
        int totalError = 0;
        for (NumCell cell : numberCells) {
            int blackCnt = GridHelper.countNeighborsSpecificCell(gridSolusi, cell.row(),
                    cell.col(), CellState.BLACK, ukuran);
            int error = cell.clue() - blackCnt;
            totalError += error * error;
        }

        /*
         * Transformasi total error dengan fungsi invers.
         * Total error ditambahkan 1 untuk menghindari pembagian dengan 0 saat error 0.
         */
        return 1.0 / (totalError + 1);
    }

    /**
     * Menghitung fitness berdasarkan skor tiap clue.
     *
     * <p>Setiap clue diberi skor yang dihitung menggunakan
     * fungsi eksponensial berdasarkan absolut error.
     * Error kecil menghasilkan skor mendekati 1, sedangkan error besar
     * menghasilkan skor mendekati 0.</p>
     *
     * <p>Fitness akhir diperoleh dari rata-rata skor seluruh clue.</p>
     *
     * @param kromosom Solusi kandidat yang akan dinilai
     * @return Nilai fitness dalam rentang (0, 1]
     */
    public double fitnessFunctionByScore(boolean[][] kromosom) {
        // Membuat grid solusi untuk mempermudah perhitungan tetangga cell hitam
        CellState[][] gridSolusi = GridHelper.makeSolutionGrid(kromosom, partialSolution, unknownCells);

        /*
         * Untuk setiap clue, cari absolut error clue lalu konversi menjadi skor
         * dengan fungsi eksponensial
         */
        double totalScore = 0.0;
        int clueCnt = numberCells.size();
        for (NumCell cell : numberCells) {
            int blackCnt = GridHelper.countNeighborsSpecificCell(gridSolusi, cell.row(),
                    cell.col(), CellState.BLACK, ukuran);
            int error = Math.abs(cell.clue() - blackCnt);

            double score = Math.exp(-0.5 * error);
            totalScore += score;
        }

        // Total skor di rata-rata kan untuk mendapat nilai fitness akhir
        return totalScore / clueCnt;
    }
}
