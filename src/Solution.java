import java.util.List;

/*
Kelas untuk mempermudah penyimpanan solusi yang ditemukan
 */
public class Solution implements Comparable<Solution> {
    private List<Position> fireStationPos;
    private double bestDistance;

    public Solution(List<Position> fireStationPos, double bestDistance) {
        this.fireStationPos = fireStationPos;
        this.bestDistance = bestDistance;
    }

    public List<Position> getFireStationPos() {
        return fireStationPos;
    }

    public void setFireStationPos(List<Position> fireStationPos) {
        this.fireStationPos = fireStationPos;
    }

    public double getBestDistance() {
        return bestDistance;
    }

    public void setBestDistance(double bestDistance) {
        this.bestDistance = bestDistance;
    }

    @Override
    public int compareTo(Solution other) {
        return Double.compare(this.bestDistance, other.bestDistance);
    }

    public void printSolution() {
        System.out.printf("%d %.5f\n", fireStationPos.size(), bestDistance);
//        for (Position position : fireStationPos) {
//            System.out.println((position.getX() + 1) + " " + (position.getY() + 1));
//        }
    }
}
