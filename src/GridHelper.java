import java.util.ArrayList;
import java.util.List;

/**
 * Kelas bantuan untuk operasi berbasis grid.
 * Menangani pencarian tetangga, pengecekan batas, dan konversi representasi kromosom
 * ke status grid sebenarnya.
 *
 * @author Vandyka
 */
public class GridHelper {
    /*
     * Array ini merepresentasikan offset (pergeseran) relatif dari titik pusat
     * (0,0) untuk mengunjungi 9 sel grid 3x3.
     * Pasangan (MOVEROW[i], MOVECOL[i]) adalah sebagai berikut:
     * Indeks 0: (0, 0) -> Sel itu sendiri
     * Indeks 1: (-1, 0) -> Atas
     * Indeks 2: (-1, 1) -> Atas-Kanan
     * Indeks 3: (0, 1) -> Kanan
     * Indeks 4: (1, 1) -> Bawah-Kanan
     * Indeks 5: (1, 0) -> Bawah
     * Indeks 6: (1, -1) -> Bawah-Kiri
     * Indeks 7: (0, -1) -> Kiri
     * Indeks 8: (-1, -1) -> Atas-Kiri
     */
    private static final int[] MOVEROW = { 0, -1, -1, 0, 1, 1, 1, 0, -1 };
    private static final int[] MOVECOL = { 0, 0, 1, 1, 1, 0, -1, -1, -1 };

    /**
     * Mencari tetangga (3 x 3) sebuah cell dengan bantuan array pergerakan ke 9 arah.
     *
     * @param row Baris cell yang akan dicari tetangganya
     * @param col Kolom cell yang akan dicari tetangganya
     * @param gridSize Ukuran board puzzle
     * @return Mengembalikan array berisi lokasi cell tetangga dari cell parameter
     */
    public static ArrayList<Cell> getNeighbors(int row, int col, int gridSize) {
        ArrayList<Cell> neighbors = new ArrayList<>();
        for (int i = 0; i < MOVEROW.length; i++) {
            int newRow = MOVEROW[i] + row;
            int newCol = MOVECOL[i] + col;

            if (!isInTheGrid(newRow, newCol, gridSize)) {
                continue;
            }

            neighbors.add(new Cell(newRow, newCol));
        }
        return neighbors;
    }

    /**
     * Membandingkan baris dan kolom dengan ukuran papan untuk mengetahui
     * jika baris tersebut berada di dalam atau di luar papan.
     *
     * @param row Baris untuk dicek
     * @param col Kolom untuk dicek
     * @param gridSize Ukuran board puzzle
     * @return True jika baris dan kolom masuk dalam board selain itu false
     */
    public static boolean isInTheGrid(int row, int col, int gridSize) {
        return row >= 0 && row < gridSize && col >= 0 && col < gridSize;
    }

    /**
     *
     * @param grid Board puzzle
     * @param row Baris cell yang akan dicari banyak warna target di tetangganya
     * @param col Kolom cell ynag akan dicari banyak warna target di tetangganya
     * @param target Warna cell target yang ingin dihitung
     * @param gridSize Ukuran board puzzle
     * @return
     */
    public static int countNeighborsSpecificCell(CellState[][] grid, int row, int col,
                                           CellState target, int gridSize) {
        int cellCount = 0;
        for (Cell cell : GridHelper.getNeighbors(row, col, gridSize)) {
            if (grid[cell.row()][cell.col()] == target) {
                cellCount++;
            }
        }
        return cellCount;
    }

    /**
     * Mengubah array boolean kromosom menjadi array CellState agar dapat digunakan
     * di method lainnya.
     *
     * @param kromosom Kromosom solusi yang ingin di transformasi
     * @return Array CellState hasil transformasil kromosom
     */
    public static CellState[][] makeSolutionGrid(boolean[][] kromosom) {
        int ukuran = kromosom.length;
        CellState[][] solutionGrid = new CellState[ukuran][ukuran];
        for (int i = 0; i < ukuran; i++) {
            for (int j = 0; j < ukuran; j++) {
                solutionGrid[i][j] = kromosom[i][j] ? CellState.BLACK : CellState.WHITE;
            }
        }
        return solutionGrid;
    }
}
