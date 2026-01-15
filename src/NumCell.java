/**
 * Merepresentasikan sel petunjuk bernomor dalam puzzle Mosaic.
 *
 * @param row Indeks baris.
 * @param col Indeks kolom.
 * @param clue Jumlah sel hitam yang diperlukan di lingkungan sekitar (0-9).
 * @author Vandyka
 */
record NumCell(int row, int col, int clue) {}