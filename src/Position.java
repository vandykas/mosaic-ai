public class Position {
    private int x;
    private int y;

    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    @Override
    public boolean equals(Object otherPos) {
        if (otherPos == this) {
            return true;
        }
        else if (!(otherPos instanceof Position)) {
            return false;
        }
        return this.x == ((Position) otherPos).x && this.y == ((Position) otherPos).y;
    }
}
