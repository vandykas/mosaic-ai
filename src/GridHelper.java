import java.util.ArrayList;
import java.util.List;

public class GridHelper {
    // Pergerakan row dan col ke tetangga termasuk cell itu sendiri
    private static final int[] MOVEROW = {0, -1, -1, 0, 1, 1, 1, 0, -1};
    private static final int[] MOVECOL = {0, 0, 1, 1, 1, 0, -1, -1, -1};

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
