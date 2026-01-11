import java.util.List;

public class FitnessCalculator {
    private final int ukuran;
    private final List<NumCell> numberCells;
    private final CellState[][] partialSolution;
    private final List<Cell> unknownCells;

    public FitnessCalculator(int ukuran, List<NumCell> numberCells, CellState[][] partialSolution, List<Cell> unknownCells) {
        this.ukuran = ukuran;
        this.numberCells = numberCells;
        this.partialSolution = partialSolution;
        this.unknownCells = unknownCells;
    }

    public double fitnessFunctionByError(boolean[] kromosom) {
        CellState[][] gridSolusi = GridHelper.makeSolutionGrid(kromosom, partialSolution, unknownCells);
        int totalError = 0;
        for (NumCell cell : numberCells) {
            int blackCnt = GridHelper.countNeighborsSpecificCell(gridSolusi, cell.row(),
                    cell.col(), CellState.BLACK, ukuran);
            int error = Math.abs(cell.clue() - blackCnt);
            totalError += error * error;
        }
        return 1.0 / (totalError + 1);
    }

    public double fitnessFunctionByScore(boolean[] kromosom) {
        CellState[][] gridSolusi = GridHelper.makeSolutionGrid(kromosom, partialSolution, unknownCells);

        double totalScore = 0.0;
        int clueCnt = numberCells.size();
        for (NumCell cell : numberCells) {
            int blackCnt = GridHelper.countNeighborsSpecificCell(gridSolusi, cell.row(),
                    cell.col(), CellState.BLACK, ukuran);
            int error = Math.abs(cell.clue() - blackCnt);

            double score = Math.exp(-0.5 * error);
            totalScore += score;
        }
        return totalScore / clueCnt;
    }
}
