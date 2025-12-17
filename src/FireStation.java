import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class FireStation {
    private final CellStatus[][] grid;
    private List<Position> emptyPositions;
    private final int fireStationsCount;
    private int houseCount;
    private final int rowSize;
    private final int columnSize;

    enum CellStatus {
        EMPTY,
        TREE,
        HOUSE
    }

    public FireStation(int rowSize, int columnSize, int fireStationsCount) {
        this.rowSize = rowSize;
        this.columnSize = columnSize;
        this.fireStationsCount = fireStationsCount;
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

    public int getFireStationsCount() {
        return fireStationsCount;
    }

    public int getHouseCount() {
        return houseCount;
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

    public void addHouseToGrid(int x, int y) {
        grid[x][y] = CellStatus.HOUSE;
        houseCount++;
    }

    public void addTreeToGrid(int x, int y) {
        grid[x][y] = CellStatus.TREE;
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

    /*
    Mencari jarak minimal setiap rumah ke firestation menggunakan teknik
    bfs multi source dengan sourcenya adalah semua firestation
     */
    public double getMinimumDistance(List<Position> fireStationPos) {
        Queue<Node> queue = new LinkedList<>();
        boolean[][] visited = new boolean[rowSize][columnSize];
        for (Position fireStation : fireStationPos) {
            queue.add(new Node(fireStation, 0));
            visited[fireStation.getX()][fireStation.getY()] = true;
        }
        return bfs(queue, visited);
    }

    /*
    BFS multi source ke 4 arah dan memasukkan jarak jika bertemu rumah
     */
    private double bfs(Queue<Node> queue, boolean[][] visited) {
        int[] moveX = {-1, 0, 1, 0};
        int[] moveY = {0, 1, 0, -1};

        double dist = 0;
        int houseFound = 0;
        while (!queue.isEmpty()) {
            Node node = queue.poll();
            visited[node.pos.getX()][node.pos.getY()] = true;
            for (int i = 0; i < 4; i++) {
                int newX = node.pos.getX() + moveX[i];
                int newY = node.pos.getY() + moveY[i];
                Position newPos = new Position(newX, newY);
                Node newNode = new Node(newPos, node.dist + 1);
                if (isInTheGrid(newX, newY) && !visited[newX][newY] && grid[newX][newY] != CellStatus.TREE) {
                    if (grid[newX][newY] == CellStatus.HOUSE) {
                        dist += newNode.dist;
                        houseFound++;
                    }
                    else {
                        queue.add(newNode);
                    }
                    visited[newX][newY] = true;
                }
            }
        }
        return dist;
    }
}