import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class Mosaic {
    private final CellStatus[][] grid;
    private List<Position> emptyPositions;
    private final int rowSize;
    private final int columnSize;

    /*
    NUMBER : Cell yang berisi angka dari input menandakan banyak kotak hitam di kotak 9 x 9 sekitar angka
    BLACK : Cell yang berisi kotak hitam
    EMPTY : Cell kosong (atau putih jika pada puzzle di websitenya) yang bukanlah kotak hitam dan juga tidak berisi angka
     */
    enum CellStatus {
        NUMBER,
        BLACK,
        EMPTY
    }

    public Mosaic(int rowSize, int columnSize) {
        this.rowSize = rowSize;
        this.columnSize = columnSize;
        this.grid = new CellStatus[rowSize][columnSize];
        for (int i = 0; i < rowSize; i++) {
            for (int j = 0; j < columnSize; j++) {
                grid[i][j] =  CellStatus.EMPTY;
            }
        }
    }

    public int getRowSize() {
        return rowSize;
    }

    public int getColumnSize() {
        return columnSize;
    }

    public List<Position> getEmptyPosition() {
        if (emptyPositions == null) {
            this.emptyPositions = new ArrayList<>();
            for (int i = 0; i < rowSize; i++) {
                for (int j = 0; j < columnSize; j++) {
                    if (isEmpty(i, j)) {
                        emptyPositions.add(new Position(i, j));
                    }
                }
            }
        }
        return this.emptyPositions;
    }
    public boolean isEmpty(int x, int y) {
        return grid[x][y] == CellStatus.EMPTY;
    }

    public boolean isInTheGrid(int x, int y) {
        return x >= 0 && x < rowSize && y >= 0 && y < columnSize;
    }

    static class Node {
        Position pos;
        int dist;

        public Node(Position pos, int dist) {
            this.pos = pos;
            this.dist = dist;
        }
    }

    public double getMinimumDistance(List<Position> fireStationPos) {
        // TODO : Membuat fitness function
        return 0;
    }

    private double bfs(Queue<Node> queue, boolean[][] visited) {
        // TODO : Membuat fitness function
        return 0;
    }
}