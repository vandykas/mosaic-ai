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

    public double fitnessFunctionNoReward(boolean[] kromosom) {
        CellState[][] gridSolusi = GridHelper.makeSolutionGrid(kromosom, partialSolution, unknownCells);
        int error = 0;
        for (NumCell cell : numberCells) {
            int blackCnt = GridHelper.countNeighborsSpecificCell(gridSolusi, cell.row(),
                    cell.col(), CellState.BLACK, ukuran);
            error += Math.abs(cell.clue() - blackCnt);
        }
        return 1.0 / (error + 1);
    }

    public double fitnessFunctionWithScore(boolean[] kromosom) {
        CellState[][] gridSolusi = GridHelper.makeSolutionGrid(kromosom, partialSolution, unknownCells);

        double totalScore = 0.0;
        int clueCnt = numberCells.size();
        for (NumCell cell : numberCells) {
            int blackCnt = GridHelper.countNeighborsSpecificCell(gridSolusi, cell.row(),
                    cell.col(), CellState.BLACK, ukuran);
            int error = Math.abs(cell.clue() - blackCnt);

            double score = switch (error) {
                case 0 -> 1.0;
                case 1 -> 0.7;
                case 2 -> 0.4;
                case 3 -> 0.2;
                default -> 0.1;
            };
            totalScore += score;
        }
        return totalScore / clueCnt;
    }
}
