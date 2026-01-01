import java.util.ArrayList;
import java.util.List;

public class Mosaic {
    private final int ukuran;
    private final int[][] clue;
    private final int[][] partialSolution;
    private final List<NumCell> numberCell;
    private List<Cell> unknownCells;

    // Pergerakan row dan col ke tetangga termasuk cell itu sendiri
    private final int[] MOVEROW = {0, -1, -1, 0, 1, 1, 1, 0, -1};
    private final int[] MOVECOL = {0, 0, 1, 1, 1, 0, -1, -1, -1};

    public Mosaic(int ukuran, int[][] clue) {
        this.ukuran = ukuran;
        this.clue = new int[ukuran][ukuran];

        for (int i = 0; i < ukuran; i++) {
            System.arraycopy(clue[i], 0, this.clue[i], 0, ukuran);
        }

        this.partialSolution = new int[ukuran][ukuran];

        this.numberCell = new ArrayList<>();
        for (int i = 0; i < ukuran; i++) {
            for (int j = 0; j < ukuran; j++) {
                if (clue[i][j] != -1) {
                    numberCell.add(new NumCell(i, j, clue[i][j]));
                }
            }
        }
        heuristic();
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

    private void heuristic() {
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
                if (partialSolution[i][j] == 0) {
                    unknownCells.add(new Cell(i, j));
                }
            }
        }
    }

    private boolean checkClue(int row, int col, int curClue) {
        /*
        0 = tidak diketahui
        1 = putih
        2 = hitam
         */
        int[] colorCount = new int[3];
        for (int i = 0; i < MOVEROW.length; i++) {
            int newRow = MOVEROW[i] + row;
            int newCol = MOVECOL[i] + col;
            if (!isInTheGrid(newRow, newCol)) {
                continue;
            }
            colorCount[partialSolution[newRow][newCol]]++;
        }

        int remainingBlack = curClue - colorCount[2];

        // Ubah semua ke hitam
        if (canFilledWithBlack(remainingBlack, colorCount[0])) {
            changeNeigbourColor(row, col, 2);
            return true;
        }
        // Ubah semua ke putih
        else if (canFilledWithWhite(remainingBlack, colorCount[0])) {
            changeNeigbourColor(row, col, 1);
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

    private void changeNeigbourColor(int row, int col, int color) {
        for (int i = 0; i < MOVEROW.length; i++) {
            int newRow = MOVEROW[i] + row;
            int newCol = MOVECOL[i] + col;
            if (!isInTheGrid(newRow, newCol)) {
                continue;
            }

            if (partialSolution[newRow][newCol] == 0) {
                partialSolution[newRow][newCol] = color;
            }
        }
    }

    public int fitnessFunction(boolean[] kromosom) {
        int[][] gridSolusi = makeSolutionGrid(kromosom);
        int fitness = 0;
        for (NumCell cell : numberCell) {
            int blackCnt = hitungSelHitamSekitar(gridSolusi, cell.row, cell.col);
            fitness += Math.abs(cell.value - blackCnt);
        }
        return -fitness;
    }

    private int[][] makeSolutionGrid(boolean[] kromosom) {
        int[][] solutionGrid = new int[ukuran][ukuran];

        // Isi grid dari kromosom buatan GA
        for (int i = 0; i < kromosom.length; i++) {
            int row = unknownCells.get(i).row;
            int col = unknownCells.get(i).col;
            solutionGrid[row][col] = kromosom[i] ? 2 : 1;
        }

        // Isi sisa grid dari warna fixed hasil heuristik
        for (int i = 0; i < ukuran; i++) {
            for (int j = 0; j < ukuran; j++) {
                if (partialSolution[i][j] != 0) {
                    solutionGrid[i][j] = partialSolution[i][j];
                }
            }
        }
        return solutionGrid;
    }

    private int hitungSelHitamSekitar(int[][] grid, int baris, int kolom) {
        int hitung = 0;
        for (int i = 0; i < MOVEROW.length; i++) {
            int newRow = baris + MOVEROW[i];
            int newCol = kolom + MOVECOL[i];
            if (isInTheGrid(newRow, newCol) && grid[newRow][newCol] == 2) {
                hitung++;
            }
        }
        return hitung;
    }

    public int getUkuran() {
        return ukuran;
    }

    public int getUnknownCellsSize() {
        return unknownCells.size();
    }

    public int[][] getclue() {
        return clue;
    }

    public boolean isInTheGrid(int x, int y) {
        return x >= 0 && x < clue.length && y >= 0 && y < clue[0].length;
    }
}

