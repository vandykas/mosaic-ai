import java.util.ArrayList;
import java.util.List;

/**
 * Penyelesai deterministik yang menggunakan logika dasar untuk menyimpulkan status sel.
 * Mengiterasi petunjuk bernomor dan menerapkan propagasi batasan (constraint propagation)
 * untuk mengidentifikasi sel yang pasti hitam (terisi) atau putih (kosong).
 *
 * @author Vandyka
 */
public class HeuristicSolver {
    /** List posisi clue dan value clue */
    private final List<NumCell> numberCell;
    /** Penyimpanan solusi yang ditemukan heuristik */
    private final CellState[][] partialSolution;
    /** Ukuran board puzzle N x N */
    private final int ukuran;

    /**
     * Membangun HeuristicSolver baru.
     *
     * @param numberCell Daftar semua sel petunjuk bernomor di papan.
     * @param partialSolution Solusi grid saat ini (untuk diperbarui).
     * @param ukuran Ukuran grid (N x N).
     */
    public HeuristicSolver(List<NumCell> numberCell, CellState[][] partialSolution, int ukuran) {
        this.numberCell = numberCell;
        this.partialSolution = partialSolution;
        this.ukuran = ukuran;
    }

    /**
     * Menjalankan heuristik single point (hanya bergantung ke 1 clue saja) dengan
     * berusaha terus mencari warna setiap tetangga clue. Jika tidak ada
     * cell yang dapat diubah maka heuristik stop dan akan lanjut ke algoritma
     * genetika.
     */
    public void solve() {
        boolean isChanged = true;
        while (isChanged) {
            isChanged = false;
            for (NumCell cell : numberCell) {
                isChanged = (isChanged || checkClue(cell.row(), cell.col(), cell.clue()));
            }
        }
    }

    /**
     * Tetangga clue (3 x 3) akan di hitung warna hitam dan unknown.
     * Banyak warna hitam dan unknown akan digunakan untuk menyimpulkan
     * suatu warna cell jika memungkinkan.
     *
     * @param row Baris clue untuk di cek
     * @param col Kolom clue untuk di cek
     * @param curClue Nilai clue
     * @return True jika ada cell yang dapat diubah dan false jika tidak ada
     */
    private boolean checkClue(int row, int col, int curClue) {
        int blackCount = 0, unknownCount = 0;
        ArrayList<Cell> neighbors = GridHelper.getNeighbors(row, col, ukuran);
        for (Cell cell : neighbors) {
            switch (partialSolution[cell.row()][cell.col()]) {
                case BLACK:
                    blackCount++;
                    break;
                case UNKNOWN:
                    unknownCount++;
                    break;
            }
        }

        int remainingBlack = curClue - blackCount;
        CellState cellState = determineCellColor(remainingBlack, unknownCount);
        if (cellState != CellState.UNKNOWN) {
            changeNeighbourColor(neighbors, cellState);
            return true;
        }
        return false;
    }

    /**
     * Cell dapat disimpulkan berwarna hitam jika jumlah hitam diperlukan
     * clue sama dengan jumlah cell unknown.
     * Cell dapat disimpulkan berwarna putih jika jumlah hitam yang diperlukan
     * sudah 0.
     *
     * @param remainingBlack Cell hitam tersisa yang diperlukan clue
     * @param unknownCount Cell unknown di sekitar clue
     * @return Warna cell putih atau hitam jika bisa diubah dan unknown jika tidak bisa
     */
    private CellState determineCellColor(int remainingBlack, int unknownCount) {
        // Mencegah memperbarui cell yang clue nya sudah terpenuhi agar tidak infinite loop
        if (unknownCount == 0) {
            return CellState.UNKNOWN;
        }

        if (remainingBlack == 0) {
            return CellState.WHITE;
        }
        else if (remainingBlack == unknownCount) {
            return CellState.BLACK;
        }
        return CellState.UNKNOWN;
    }

    /**
     * Mengubah semua cell unknown dengan warna yang ditemukan
     *
     * @param neighbors Lokasi tetangga sebuah cell
     * @param color Warna untuk tetangga yang cell masih unknown
     */
    private void changeNeighbourColor(ArrayList<Cell> neighbors, CellState color) {
        for (Cell cell : neighbors) {
            int row = cell.row();
            int col = cell.col();

            // Mencegah mengubah cell yang sudah diwarnai
            if (partialSolution[row][col] == CellState.UNKNOWN) {
                partialSolution[row][col] = color;
            }
        }
    }
}
