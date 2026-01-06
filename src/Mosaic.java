import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Mosaic {
    private final int ukuran;
    private final int[][] clue;
    private final CellState[][] partialSolution;
    private final List<NumCell> numberCell;
    private List<Cell> unknownCells;
    private double[] unknownCellsProb;

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
            Arrays.fill(this.partialSolution[i], CellState.UNKNOWN);
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

    record Cell(int row, int col) {}

    record NumCell(int row, int col, int clue) {}

    public int getUnknownCellsSize() {
        return unknownCells.size();
    }

    public double getUnknownCellsProb(int idx) {
        return unknownCellsProb[idx];
    }

    private ArrayList<Cell> getNeighbors(int row, int col) {
        ArrayList<Cell> neighbors = new ArrayList<>();
        for (int i = 0; i < MOVEROW.length; i++) {
            int newRow = MOVEROW[i] + row;
            int newCol = MOVECOL[i] + col;

            if (!isInTheGrid(newRow, newCol)) {
                continue;
            }

            neighbors.add(new Cell(newRow, newCol));
        }
        return neighbors;
    }

    public boolean isInTheGrid(int x, int y) {
        return x >= 0 && x < clue.length && y >= 0 && y < clue[0].length;
    }

    public void runHeuristic() {
        boolean isChanged = true;
        while (isChanged) {
            isChanged = false;
            for (NumCell cell : numberCell) {
                isChanged = (isChanged || checkClue(cell.row, cell.col, cell.clue));
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
        ArrayList<Cell> neighbors = getNeighbors(row, col);
        for (Cell cell : neighbors) {
            switch (partialSolution[cell.row][cell.col]) {
                case BLACK:
                    blackCount++;
                    break;
                case UNKNOWN:
                    unknownCount++;
                    break;
            }
        }

        int remainingBlack = curClue - blackCount;
        CellState cellState = searchCellColor(remainingBlack, unknownCount);
        if (cellState != CellState.UNKNOWN) {
            changeNeighbourColor(neighbors, cellState);
            return true;
        }
        return false;
    }

    private CellState searchCellColor(int remainingBlack, int unknownCount) {
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
            int row = cell.row;
            int col = cell.col;
            if (partialSolution[row][col] == CellState.UNKNOWN) {
                partialSolution[row][col] = color;
            }
        }
    }

    public void createUnknownCellsProbability() {
        int unknownCount = getUnknownCellsSize();
        this.unknownCellsProb = new double[unknownCount];
        for (int i = 0; i < unknownCount; i++) {
            addClueInfluence(i, unknownCells.get(i).row, unknownCells.get(i).col);
        }

        normalize();
    }

    private void addClueInfluence(int idx, int row, int col) {
        List<Cell> neighbors = getNeighbors(row, col);
        for (Cell cell : neighbors) {
            int clueVal = clue[cell.row][cell.col];
            if (clueVal != -1) {
                int blackCount = countNeighborsSpecificCell(partialSolution, cell.row, cell.col, CellState.BLACK);
                int unknownCount = countNeighborsSpecificCell(partialSolution, cell.row, cell.col, CellState.UNKNOWN);
                if (unknownCount == 0) {
                    continue;
                }

                int remainingBlack = clueVal - blackCount;
                double p = (remainingBlack / 3.5) / unknownCount;
                unknownCellsProb[idx] += p;
            }
        }
    }

    private void normalize() {
        double min = Double.POSITIVE_INFINITY;
        double max = Double.NEGATIVE_INFINITY;

        int unknownCount = getUnknownCellsSize();
        for (double prob : unknownCellsProb) {
            min = Math.min(min, prob);
            max = Math.max(max, prob);
        }

        if (max == min) {
            Arrays.fill(unknownCellsProb, 0.5);
        }
        else {
            for (int i = 0; i < unknownCount; i++) {
                unknownCellsProb[i] = (unknownCellsProb[i] - min) / (max - min);
            }
        }
    }

    public double fitnessFunction(boolean[] kromosom) {
        CellState[][] gridSolusi = makeSolutionGrid(kromosom);
        int fitness = 0;
        for (NumCell cell : numberCell) {
            int blackCnt = countNeighborsSpecificCell(gridSolusi, cell.row, cell.col, CellState.BLACK);
            fitness += Math.abs(cell.clue - blackCnt);
        }
        return 1.0 / (fitness + 1);
    }

    private CellState[][] makeSolutionGrid(boolean[] kromosom) {
        CellState[][] solutionGrid = new CellState[ukuran][ukuran];
        // Isi sisa grid dari warna fixed hasil heuristik
        for (int i = 0; i < ukuran; i++) {
            System.arraycopy(partialSolution[i], 0, solutionGrid[i], 0, ukuran);
        }

        // Isi grid dari kromosom buatan GA
        for (int i = 0; i < kromosom.length; i++) {
            int row = unknownCells.get(i).row;
            int col = unknownCells.get(i).col;
            solutionGrid[row][col] = kromosom[i] ? CellState.WHITE : CellState.BLACK;
        }
        return solutionGrid;
    }

    private int countNeighborsSpecificCell(CellState[][] grid, int row, int col, CellState target) {
        int cellCount = 0;
        for (Cell cell : getNeighbors(row, col)) {
            if (grid[cell.row][cell.col] == target) {
                cellCount++;
            }
        }
        return cellCount;
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
