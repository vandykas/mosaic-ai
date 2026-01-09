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

    public int getUnknownCellsSize() {
        return unknownCells.size();
    }

    public double getUnknownCellsProb(int idx) {
        return unknownCellsProb[idx];
    }

    public void runHeuristic() {
        HeuristicSolver heuristicSolver = new HeuristicSolver(numberCell, partialSolution, ukuran);
        heuristicSolver.solve();
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

    public void createUnknownCellsProbability() {
        ProbabilityCalculator probabilityCalculator = new ProbabilityCalculator(
                ukuran, unknownCells, partialSolution, clue
        );
        this.unknownCellsProb = probabilityCalculator.calculateProbability();
    }

    public double fitnessFunction(boolean[] kromosom) {
        CellState[][] gridSolusi = GridHelper.makeSolutionGrid(kromosom, partialSolution, unknownCells);
        int fitness = 0;
        for (NumCell cell : numberCell) {
            int blackCnt = GridHelper.countNeighborsSpecificCell(gridSolusi, cell.row(),
                    cell.col(), CellState.BLACK, ukuran);
            fitness += Math.abs(cell.clue() - blackCnt);
        }
        return 1.0 / (fitness + 1);
    }

    public void printSolution(boolean[] kromosom) {
        CellState[][] solution = GridHelper.makeSolutionGrid(kromosom, partialSolution, unknownCells);
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
