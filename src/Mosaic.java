import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Merepresentasikan papan puzzle Mosaic.
 * Menyimpan status grid, petunjuk, dan menangani logika untuk penyelesaian heuristik
 * serta perhitungan fitness untuk Algoritma Genetika.
 *
 * @author Marco, Vandyka
 */
public class Mosaic {
    /** Panjang board puzzle mosaic */
    private final int ukuran;
    /** Board puzzle mosaic (-1 adalah cell kosong) */
    private final int[][] clue;
    /** Solusi parsial hasil heuristik deterministik */
    private final CellState[][] partialSolution;
    /** List posisi clue */
    private final List<NumCell> numberCells;
    /** List posisi cell yang tidak bisa di isi heuristik */
    private List<Cell> unknownCells;
    /** Peluang cell hitam untuk digunakan heuristik probabilistik */
    private double[] unknownCellsProb;

    /**
     * Membangun papan Mosaic baru.
     *
     * @param ukuran Ukuran grid (N x N).
     * @param clue Array 2D petunjuk (-1 menunjukkan tidak ada petunjuk).
     */
    public Mosaic(int ukuran, int[][] clue) {
        this.ukuran = ukuran;
        this.clue = new int[ukuran][ukuran];

        for (int i = 0; i < ukuran; i++) {
            System.arraycopy(clue[i], 0, this.clue[i], 0, ukuran);
        }

        this.partialSolution = new CellState[ukuran][ukuran];
        for (int i = 0; i < ukuran; i++) {
            Arrays.fill(this.partialSolution[i], CellState.UNKNOWN);
        }

        this.numberCells = new ArrayList<>();
        for (int i = 0; i < ukuran; i++) {
            for (int j = 0; j < ukuran; j++) {
                if (clue[i][j] != -1) {
                    numberCells.add(new NumCell(i, j, clue[i][j]));
                }
            }
        }
        this.unknownCells = new ArrayList<>();
    }

    public int getUnknownCellsSize() {
        return unknownCells.size();
    }

    public int getUkuran() {
        return ukuran;
    }

    public List<Cell> getUnknownCells() {
        return unknownCells;
    }

    public CellState[][] getPartialSolution() {
        return partialSolution;
    }

    public double getUnknownCellsProb(int idx) {
        return unknownCellsProb[idx];
    }

    /**
     * Menjalankan heuristik deterministik.
     */
    public void runHeuristic() {
        HeuristicSolver heuristicSolver = new HeuristicSolver(numberCells, partialSolution, ukuran);
        heuristicSolver.solve();
        putRemainingUnknownCell();
    }

    /**
     * Menjalankan GA tanpa menggunakan heuristik deterministik.
     */
    public void runWithoutHeuristic() {
        putRemainingUnknownCell();
    }

    /**
     * Untuk cell yang tidak dapat di isi heuristik deterministik, dimasukkan
     * ke dalam list.
     */
    private void putRemainingUnknownCell() {
        for (int i = 0; i < ukuran; i++) {
            for (int j = 0; j < ukuran; j++) {
                if (partialSolution[i][j] == CellState.UNKNOWN) {
                    unknownCells.add(new Cell(i, j));
                }
            }
        }
    }

    /**
     * Menghitunga peluang cell hitam untuk heuristik probabilistik yang akan
     * digunakan untuk mengarahkan populasi awal.
     */
    public void createUnknownCellsProbability() {
        ProbabilityCalculator probabilityCalculator = new ProbabilityCalculator(
                ukuran, unknownCells, partialSolution, clue
        );
        this.unknownCellsProb = probabilityCalculator.calculateProbability();
    }

    /**
     * Menghitung nilai fitness kromosom tanpa mempertimbangkan diversity.
     *
     * @param kromosom Kromosom untuk dihitung fitnessnya
     * @return Nilai fitness kromosom
     */
    public double fitnessFunction(boolean[][] kromosom) {
        FitnessCalculator fitnessCalculator = new FitnessCalculator(ukuran, numberCells, partialSolution, unknownCells);
        return fitnessCalculator.fitnessFunctionByScore(kromosom);
    }

    /**
     * Menghitung nilai fitness kromosom dengan mempertimbangkan diversity kromosom.
     * Seberapa berpengaruh diversity, diatur dengan alpha.
     *
     * @param kromosom Kromosom untuk dihitung fitnessnya
     * @param average Array berisi rata-rata cell berwarna hitam dalam sebuah populasi
     * @param alpha Bobot untuk mengatur seberapa berpengaruh nilai diversity
     * @return Nilai fitness kromosom ditambah nilai diversity
     */
    public double fitnessFunctionWithDiversity(boolean[][] kromosom, double[] average, double alpha) {
        FitnessCalculator fitnessCalculator = new FitnessCalculator(ukuran, numberCells, partialSolution, unknownCells);
        double fitness = fitnessCalculator.fitnessFunctionByScore(kromosom);
        if (fitness == 1.0) {
            return fitness;
        }

        double diversity = calculateDiversity(kromosom, average);

        // Alpha menyesuaikan bobot diversity dan memastikan hasil di rentan [0, 1]
        return (1 - alpha) * fitness + alpha * diversity;
    }

    /**
     * Menghitung nilai diversity sebuah kromosom relatif terhadap populasi saat ini.
     *
     * <p>Diversity dihitung berdasarkan perbedaan setiap gen (cell) terhadap
     * distribusi rata-rata populasi. Untuk setiap cell yang belum diketahui
     * (unknown cell), nilai kontribusi diversity ditentukan sebagai:</p>
     *
     * <ul>
     *   <li>{@code 1 - average[i]} jika gen bernilai hitam (true)</li>
     *   <li>{@code average[i]} jika gen bernilai putih (false)</li>
     * </ul>
     *
     * <p>Dengan begitu, gene yang jarang muncul dalam populasi akan memberikan
     * nilai diversity yang lebih besar. Nilai akhir dinormalisasi ke rentang
     * {@code [0, 1]} dengan membagi dengan jumlah cell.</p>
     *
     * @param kromosom Kromosom untuk dihitung seberapa diverse dibandingkan populasinya
     * @param average Array rata-rata sebuah cell berwarna hitam dalam sebuah populasi
     * @return Nilai diversity kromosom
     */
    private double calculateDiversity(boolean[][] kromosom, double[] average) {
        double diversity = 0;
        for (int i = 0; i < average.length; i++) {
            Cell cell = unknownCells.get(i);
            diversity += kromosom[cell.row()][cell.col()] ? 1 - average[i] : average[i];
        }
        return diversity / average.length;
    }

    /**
     * Print hasil kromosom dan juga menghitung banyak clue yang salah.
     *
     * @param kromosom Kromosom yang ingin di print
     */
    public void printSolution(boolean[][] kromosom) {
        CellState[][] solution = GridHelper.makeSolutionGrid(kromosom);
        int diff = 0;
        for (int i = 0; i < ukuran; i++) {
            for (int j = 0; j < ukuran; j++) {
                System.out.print(solution[i][j] == CellState.WHITE ? "P " : "H ");
                if (clue[i][j] != -1) {
                    if (clue[i][j] != GridHelper.countNeighborsSpecificCell(solution, i, j, CellState.BLACK, ukuran)) {
                        diff++;
                    }
                }
            }
            System.out.println();
        }
        System.out.println("Banyak clue salah: " + diff);
    }

    /**
     * Print hasil heuristik deterministik untuk melihat cell apa saja yang diselesaikan
     * heuristik.
     */
    public void printHeuristicSolution() {
        for (int i = 0; i < ukuran; i++) {
            for (int j = 0; j < ukuran; j++) {
                switch (partialSolution[i][j]) {
                    case BLACK:
                        System.out.print("H ");
                        break;
                    case WHITE:
                        System.out.print("P ");
                        break;
                    default:
                        System.out.print("U ");
                        break;
                }
            }
            System.out.println();
        }
    }
}
