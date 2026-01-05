import java.util.ArrayList;
import java.util.List;

public class Mosaic {
    private final int ukuran;
    private final int[][] clue;
    private final CellState[][] partialSolution;
    private final List<NumCell> numberCell;
    private List<Cell> unknownCells;

    private enum CellState {
        UNKNOWN,
        BLACK,
        WHITE
    }

    // Pergerakan row dan col ke tetangga termasuk cell itu sendiri
    private final int[] MOVEROW = {0, -1, -1, 0, 1, 1, 1, 0, -1};
    private final int[] MOVECOL = {0, 0, 1, 1, 1, 0, -1, -1, -1};

    public Mosaic(int ukuran, int[][] clue) {
        this.ukuran = ukuran;
        this.clue = new int[ukuran][ukuran];

        for (int i = 0; i < ukuran; i++) {
            System.arraycopy(clue[i], 0, this.clue[i], 0, ukuran);
        }

        this.partialSolution = new CellState[ukuran][ukuran];
        for (int i = 0; i < ukuran; i++) {
            for (int j = 0; j < ukuran; j++) {
                partialSolution[i][j] = CellState.UNKNOWN;
            }
        }

        this.numberCell = new ArrayList<>();
        for (int i = 0; i < ukuran; i++) {
            for (int j = 0; j < ukuran; j++) {
                if (clue[i][j] != -1) {
                    numberCell.add(new NumCell(i, j, clue[i][j]));
                }
            }
        }
    }

    public int getUnknownCellsSize() {
        return unknownCells.size();
    }

    private static class Cell {
        int row;
        int col;

        public Cell(int row, int col) {
            this.row = row;
            this.col = col;
        }
    }

    private static class NumCell {
        int row;
        int col;
        int value;

        public NumCell(int row, int col, int value) {
            this.row = row;
            this.col = col;
            this.value = value;
        }
    }

    public void runHeuristic() {
        boolean isChanged = true;
        while (isChanged) {
            isChanged = false;

            for (int row = 0; row < ukuran; row++) {
                for (int col = 0; col < ukuran; col++) {
                    int curClue = clue[row][col];
                    if (curClue >= 0) {
                        isChanged = (isChanged || checkClue(row, col, curClue));
                    }
                }
            }
        }
        putRemainingUnknownCell();
    }

    private void putRemainingUnknownCell() {
        unknownCells = new ArrayList<>();
        for (int i = 0; i < ukuran; i++) {
            for (int j = 0; j < ukuran; j++) {
                if (partialSolution[i][j] == CellState.UNKNOWN) {
                    unknownCells.add(new Cell(i, j));
                }
            }
        }
    }

    private boolean checkClue(int row, int col, int curClue) {
        int blackCount = 0, unknownCount = 0;
        for (int i = 0; i < MOVEROW.length; i++) {
            int newRow = MOVEROW[i] + row;
            int newCol = MOVECOL[i] + col;
            if (!isInTheGrid(newRow, newCol)) {
                continue;
            }

            switch (partialSolution[newRow][newCol]) {
                case BLACK:
                    blackCount++;
                    break;
                case UNKNOWN:
                    unknownCount++;
                    break;
            }
        }

        int remainingBlack = curClue - blackCount;

        // Ubah semua ke hitam
        if (canFilledWithBlack(remainingBlack, unknownCount)) {
            changeNeighbourColor(row, col, CellState.BLACK);
            return true;
        }
        // Ubah semua ke putih
        else if (canFilledWithWhite(remainingBlack, unknownCount)) {
            changeNeighbourColor(row, col, CellState.WHITE);
            return true;
        }
        return false;
    }

    private boolean canFilledWithBlack(int remainingBlack, int unknown) {
        return remainingBlack == unknown && unknown > 0;
    }

    private boolean canFilledWithWhite(int remainingBlack, int unknown) {
        return remainingBlack == 0 && unknown > 0;
    }

    private void changeNeighbourColor(int row, int col, CellState color) {
        for (int i = 0; i < MOVEROW.length; i++) {
            int newRow = MOVEROW[i] + row;
            int newCol = MOVECOL[i] + col;
            if (!isInTheGrid(newRow, newCol)) {
                continue;
            }

            if (partialSolution[newRow][newCol] == CellState.UNKNOWN) {
                partialSolution[newRow][newCol] = color;
            }
        }
    }

    public double fitnessFunction(boolean[] kromosom) {
        CellState[][] gridSolusi = makeSolutionGrid(kromosom);
        int fitness = 0;
        for (NumCell cell : numberCell) {
            int blackCnt = hitungSelHitamSekitar(gridSolusi, cell.row, cell.col);
            fitness += Math.abs(cell.value - blackCnt);
        }
        return 1.0 / (fitness + 1);
    }

    private CellState[][] makeSolutionGrid(boolean[] kromosom) {
        CellState[][] solutionGrid = new CellState[ukuran][ukuran];

        // Isi grid dari kromosom buatan GA
        for (int i = 0; i < kromosom.length; i++) {
            int row = unknownCells.get(i).row;
            int col = unknownCells.get(i).col;
            solutionGrid[row][col] = kromosom[i] ? CellState.WHITE : CellState.BLACK;
        }

        // Isi sisa grid dari warna fixed hasil heuristik
        for (int i = 0; i < ukuran; i++) {
            for (int j = 0; j < ukuran; j++) {
                if (partialSolution[i][j] != CellState.UNKNOWN) {
                    solutionGrid[i][j] = partialSolution[i][j];
                }
            }
        }
        return solutionGrid;
    }

    private int hitungSelHitamSekitar(CellState[][] grid, int baris, int kolom) {
        int blackCount = 0;
        for (int i = 0; i < MOVEROW.length; i++) {
            int newRow = baris + MOVEROW[i];
            int newCol = kolom + MOVECOL[i];
            if (isInTheGrid(newRow, newCol) && grid[newRow][newCol] == CellState.BLACK) {
                blackCount++;
            }
        }
        return blackCount;
    }

    public boolean isInTheGrid(int x, int y) {
        return x >= 0 && x < clue.length && y >= 0 && y < clue[0].length;
    }

    public void printSolution(boolean[] kromosom) {
        CellState[][] solution = makeSolutionGrid(kromosom);
        for (int i = 0; i < ukuran; i++) {
            for (int j = 0; j < ukuran; j++) {
                System.out.print(solution[i][j] == CellState.WHITE ? "P " : "H ");
            }
            System.out.println();
        }
    }

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

