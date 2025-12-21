import java.util.ArrayList;
import java.util.List;

public class Mosaic {
    private int ukuran;
    private int[][] clue;
    private List<NumCell> numberCell;

    /**
     * Konstruktor untuk membuat objek Mosaic
     * @param ukuran Ukuran grid (n x n)
     * @param clue Grid berisi angka clue
     */
    public Mosaic(int ukuran, int[][] clue) {
        this.ukuran = ukuran;
        this.clue = new int[ukuran][ukuran];

        for (int i = 0; i < ukuran; i++) {
            System.arraycopy(clue[i], 0, this.clue[i], 0, ukuran);
        }

        this.numberCell = new ArrayList<NumCell>();
        for (int i = 0; i < ukuran; i++) {
            for (int j = 0; j < ukuran; j++) {
                if (clue[i][j] != -1) {
                    numberCell.add(new NumCell(i, j, clue[i][j]));
                }
            }
        }
    }

    private static class NumCell {
        int row;
        int col;
        Integer value;

        public NumCell(int row, int col, Integer value) {
            this.row = row;
            this.col = col;
            this.value = value;
        }
    }

    public int fitnessFunction(int[][] gridSolusi) {
        int fitness = 0;
        for (NumCell numCell : numberCell) {
            int curRow = numCell.row, curCol = numCell.col;
            int blackCnt = hitungSelHitamSekitar(gridSolusi, curRow, curCol);
            fitness += Math.abs(numCell.value - blackCnt);
        }
        return -fitness;
    }

    /**
     * Menghitung jumlah sel hitam di sekitar sel (termasuk sel itu sendiri)
     * @param grid Grid solusi
     * @param baris Posisi baris
     * @param kolom Posisi kolom
     * @return Jumlah sel hitam di sekitarnya
     */
    private int hitungSelHitamSekitar(int[][] grid, int baris, int kolom) {
        // Pergerakan untuk ke 8 arah sekitar cell tambah cell itu sendiri
        int[] moveRow = {0, -1, -1, 0, 1, 1, 1, 0, -1};
        int[] moveCol = {0, 0, 1, 1, 1, 0, -1, -1, -1};

        int hitung = 0;
        for (int i = 0; i < moveRow.length; i++) {
            int newRow = baris + moveRow[i];
            int newCol = kolom + moveCol[i];
            if (isInTheGrid(newRow, newCol) && grid[newRow][newCol] == 1) {
                hitung++;
            }
        }
        return hitung;
    }

    public int getUkuran() {
        return ukuran;
    }

    public int[][] getclue() {
        return clue;
    }

    public boolean isInTheGrid(int x, int y) {
        return x >= 0 && x < clue.length && y >= 0 && y < clue[0].length;
    }
}