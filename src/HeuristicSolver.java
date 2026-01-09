import java.util.ArrayList;
import java.util.List;

public class HeuristicSolver {
    private final List<NumCell> numberCell;
    private final CellState[][] partialSolution;
    private final int ukuran;

    public HeuristicSolver(List<NumCell> numberCell, CellState[][] partialSolution, int ukuran) {
        this.numberCell = numberCell;
        this.partialSolution = partialSolution;
        this.ukuran = ukuran;
    }

    public void solve() {
        boolean isChanged = true;
        while (isChanged) {
            isChanged = false;
            for (NumCell cell : numberCell) {
                isChanged = (isChanged || checkClue(cell.row(), cell.col(), cell.clue()));
            }
        }
    }

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

    private CellState determineCellColor(int remainingBlack, int unknownCount) {
        if (unknownCount == 0) {
            return CellState.UNKNOWN;
        }

        if (remainingBlack == 0) {
            return CellState.WHITE;
        }
        else if (remainingBlack == unknownCount) {
            return CellState.BLACK;
        }
        return  CellState.UNKNOWN;
    }

    private void changeNeighbourColor(ArrayList<Cell> neighbors, CellState color) {
        for (Cell cell : neighbors) {
            int row = cell.row();
            int col = cell.col();
            if (partialSolution[row][col] == CellState.UNKNOWN) {
                partialSolution[row][col] = color;
            }
        }
    }
}
