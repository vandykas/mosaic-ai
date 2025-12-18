import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class Mosaic {
    private final Integer[][] grid;
    private List<Position> emptyPositions;
    private final int rowSize;
    private final int columnSize;

    public Mosaic(int rowSize, int columnSize, Integer[][] grid) {
        this.rowSize = rowSize;
        this.columnSize = columnSize;
        this.grid = grid;
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

    public double fitnessFunction(List<Position> fireStationPos) {
        // TODO : Membuat fitness function untuk mosaic
        return 0;
    }
}