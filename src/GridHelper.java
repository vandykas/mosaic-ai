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

    public static boolean isInTheGrid(int x, int y, int gridSize) {
        return x >= 0 && x < gridSize && y >= 0 && y < gridSize;
    }

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

    public static CellState[][] makeSolutionGrid(boolean[] kromosom, CellState[][] partialSolution, List<Cell> unknownCells) {
        int ukuran = partialSolution.length;
        CellState[][] solutionGrid = new CellState[ukuran][ukuran];
        for (int i = 0; i < ukuran; i++) {
            System.arraycopy(partialSolution[i], 0, solutionGrid[i], 0, ukuran);
        }

        for (int i = 0; i < kromosom.length; i++) {
            int row = unknownCells.get(i).row();
            int col = unknownCells.get(i).col();
            solutionGrid[row][col] = kromosom[i] ? CellState.WHITE : CellState.BLACK;
        }
        return solutionGrid;
    }
}
