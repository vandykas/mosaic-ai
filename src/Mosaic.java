import java.util.ArrayList;
import java.util.HashMap;

public class Mosaic {
    /*
    FixedColor berisi posisi cell yang sudah berisi warna fixed dan
    warna ini didapat dari heuristik
    PosToIdx untuk mentranslasi posisi 2D ke posisi 1D karena gene hanya
    menyimpan cell yang warnanya belum fixed
     */
    private final HashMap<Position, Boolean> fixedColor;
    private final HashMap<Position, Integer> posToIdx;
    private final Integer[][] grid;
    private final ArrayList<NumCell> numberCell;
    private final int rowSize;
    private final int columnSize;

    private static class NumCell {
        Position position;
        Integer value;

        public NumCell(Position position, Integer value) {
            this.position = position;
            this.value = value;
        }
    }

    public Mosaic(int rowSize, int columnSize, Integer[][] grid) {
        this.rowSize = rowSize;
        this.columnSize = columnSize;
        this.grid = grid;
        this.fixedColor = new HashMap<>();
        this.posToIdx = new HashMap<>();

        this.numberCell = new ArrayList<>();
        for (int i = 0; i < rowSize; i++) {
            for (int j = 0; j < columnSize; j++) {
                if (grid[i][j] != -1) {
                    numberCell.add(new NumCell(new Position(i, j), grid[i][j]));
                }
            }
        }
    }

    public int getRowSize() {
        return rowSize;
    }

    public int getColumnSize() {
        return columnSize;
    }

    public boolean isInTheGrid(int x, int y) {
        return x >= 0 && x < rowSize && y >= 0 && y < columnSize;
    }

    public void heuristic() {
        // TODO : Membuat heuristik untuk mosaic agar ukuran chromosome mengecil
    }

    public double fitnessFunction(boolean[] chromosome) {
        // Pergerakan untuk ke 8 arah sekitar cell tambah cell itu sendiri
        int[] moveRow = {0, -1, -1, 0, 1, 1, 1, 0, -1};
        int[] moveCol = {0, 0, 1, 1, 1, 0, -1, -1, -1};

        int fitness = 0;
        for (NumCell numCell : numberCell) {
            int curRow = numCell.position.getX(), curCol = numCell.position.getY();
            int blackCnt = 0;
            for (int j = 0; j < moveRow.length; j++) {
                int newRow = moveRow[j] + curRow;
                int newCol = moveCol[j] + curCol;
                if (isInTheGrid(newRow, newCol)) {
                    Position position = new Position(newRow, newCol);
                    if (isBlack(position, chromosome)) {
                        blackCnt++;
                    }
                }
            }
            fitness += Math.abs(numCell.value - blackCnt);
        }
        return 1.0 / (1 + fitness);
    }

    private boolean isBlack(Position p, boolean[] chromosome) {
        if (fixedColor.containsKey(p)) {
            return fixedColor.get(p);
        }
        return chromosome[posToIdx.get(p)];
    }
}