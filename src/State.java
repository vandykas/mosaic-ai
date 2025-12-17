import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class State {
    private final List<Position> state;
    private final Random rand;
    private final Mosaic env;

    // TODO : Sesuaikan input dengan puzzle mosaic
    public State(Mosaic env, Random rand) {
        this.state = new ArrayList<>(env.getMosaicsCount());
        this.env = env;
        this.rand = rand;
        generateStartingState();
    }

    public State(Mosaic env, List<Position> state, Random rand) {
        this.state = state;
        this.env = env;
        this.rand = rand;
    }

    public List<Position> getState() {
        return state;
    }

    /*
    Membuat state awal secara random. Pengambilan posisi akan diulang terus
    selama belum valid
     */
    // TODO : Sesuaikan state awal dengan puzzle mosaic
    public void generateStartingState() {
        int x, y;
        Position fireStationPos;
        for (int i = 0; i < env.getMosaicsCount(); i++) {
            do {
                x = this.rand.nextInt(env.getRowSize());
                y = this.rand.nextInt(env.getColumnSize());
                fireStationPos = new Position(x, y);
            }
            while (!env.isEmpty(x, y) || state.contains(fireStationPos));
            state.add(fireStationPos);
        }
    }

    /*
    Membuat state tetangga dengan memilih 1 fire station dan arahnya (atas, kanan, bawah, kiri)
    secara acak. Tetangga hanya mengubah 1 fire station ke 4 arah agar tidak terlalu jauh dari state
    sekarang (memperbanyak eksploitasi ketimbang eksplorasi)
     */
    public State generateNeighbor() {
        // TODO : Sesuaikan pencarian tetangga dengan puzzle mosaic
    }
}