import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ProbabilityCalculator {
    private final int ukuran;
    private final List<Cell> unknownCells;
    private final CellState[][] partialSolution;
    private final int[][] clue;

    public ProbabilityCalculator(int ukuran, List<Cell> unknownCells, CellState[][] partialSolution, int[][] clue) {
        this.ukuran = ukuran;
        this.unknownCells = unknownCells;
        this.partialSolution = partialSolution;
        this.clue = clue;
    }

    public double[] calculateProbability() {
        int unknownCount = unknownCells.size();
        double[] probability = new double[unknownCount];

        for (int i = 0; i < unknownCount; i++) {
            addClueInfluence(probability, i, unknownCells.get(i).row(), unknownCells.get(i).col());
        }

        normalize(probability);
        return probability;
    }

    private void addClueInfluence(double[] probabilty, int idx, int row, int col) {
        ArrayList<Cell> neighbors = GridHelper.getNeighbors(row, col, ukuran);
        for (Cell cell : neighbors) {
            int clueVal = clue[cell.row()][cell.col()];
            if (clueVal != -1) {
                int blackCount = GridHelper.countNeighborsSpecificCell(partialSolution, cell.row(),
                        cell.col(), CellState.BLACK, ukuran);
                int unknownCount = GridHelper.countNeighborsSpecificCell(partialSolution, cell.row(),
                        cell.col(), CellState.UNKNOWN, ukuran);
                if (unknownCount == 0) {
                    continue;
                }

                int remainingBlack = clueVal - blackCount;
                double p = (remainingBlack / 3.5) / unknownCount;
                probabilty[idx] += p;
            }
        }
    }

    private void normalize(double[] probability) {
        double min = Double.POSITIVE_INFINITY;
        double max = Double.NEGATIVE_INFINITY;

        int unknownCount = unknownCells.size();
        for (double prob : probability) {
            min = Math.min(min, prob);
            max = Math.max(max, prob);
        }

        if (max == min) {
            Arrays.fill(probability, 0.5);
        }
        else {
            for (int i = 0; i < unknownCount; i++) {
                probability[i] = (probability[i] - min) / (max - min);
            }
        }
    }
}
