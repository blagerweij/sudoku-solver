import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeSet;

/**
 * Sudoku solver based on the Python implementation from Peter Norvig (http://norvig.com/sudoku.html).
 * This solution uses bitmasks and int-arrays to improve performance
 * 
 * @author Barry Lagerweij <blagerweij@gmail.com>
 */
public class SudokuBoard2 {

    private static final int UNITSIZE = 3; // Use 3 for a 9x9 sudo, use 2 for a 4x4 sudoku, etc.
    private static final int SIZE = UNITSIZE*UNITSIZE;
    private static final int COUNT = SIZE*SIZE;
    private static final int PEERCOUNT = SIZE+SIZE+(UNITSIZE-1)*(UNITSIZE-1)-2;
    private static final int[][] PEERS; // 2D array for each position stores the positions of all peers
    private static final int[][][] UNITS; // 3D array for each position stores the unit (X,Y,BLOCK) positions

    static {
        // populate the static lookup arrays
        // first populate XAXIS, YAXIS and BLOCK constants
        final int[][] XAXIS = new int[SIZE][SIZE];
        final int[][] YAXIS = new int[SIZE][SIZE];
        final int[][] BLOCK = new int[SIZE][SIZE];
        for (int y=0; y<SIZE; y++) {
            for (int x = 0; x < SIZE; x++) {
                XAXIS[y][x] = 
                YAXIS[x][y] = 
                BLOCK[(y/UNITSIZE)*UNITSIZE+x/UNITSIZE][(y%UNITSIZE)*UNITSIZE+x%UNITSIZE] = y*SIZE+x;
            }
        }
        // with the above units, let's populate the units for each cell
        UNITS = new int[COUNT][3][];
        for (int y=0; y<SIZE; y++) {
            for (int x = 0; x < SIZE; x++) {
                UNITS[y*SIZE+x][0] = XAXIS[y];
                UNITS[y*SIZE+x][1] = YAXIS[x];
                UNITS[y*SIZE+x][2] = BLOCK[(y/UNITSIZE)*UNITSIZE+x/UNITSIZE];
            }
        }
        // now populate the PEERS for each cell
        PEERS = new int[COUNT][PEERCOUNT];
        for (int y=0; y<SIZE; y++) {
            for (int x = 0; x < SIZE; x++) {
                TreeSet<Integer> peerSet = new TreeSet<Integer>();
                for (int p: XAXIS[y]) peerSet.add(p);
                for (int p: YAXIS[x]) peerSet.add(p);
                for (int p: BLOCK[(y/UNITSIZE)*UNITSIZE+x/UNITSIZE]) peerSet.add(p);
                peerSet.remove(y*SIZE+x); // remove current position from peers
                int[] peers = new int[PEERCOUNT];
                int n=0;
                for (Integer pos : peerSet) peers[n++] = pos;
                PEERS[y*SIZE+x] = peers;
            }
        }
    }
    private LinkedList<int[]> boards = new LinkedList<int[]>();
    private int[] cells;

    public SudokuBoard2(int[][] puzzle) {
        cells = new int[COUNT];
        Arrays.fill(cells,~(-1 << SIZE+1)-1);
        int pos = 0;
        for (int y=0; y<SIZE; y++) {
            for (int x=0; x<SIZE; x++, pos++) {
                final int value = puzzle[y][x];
                if (value != 0 && !assign(pos, value)) {
                    throw new IllegalStateException("Failed to populate initial board");
                }
            }
        }
    }

    /**
     * Using depth-first search and propagation, try all possible values.
     *
     * @return true when the board is valid (i.e. no contradictions). false otherwise
     */
    public final boolean search() {
        // find the position with the lowest count
        // if all cells have a size of 1, we've found a solution
        int lowestPos = -1;
        int lowestCount = SIZE+1;
        for (int pos=0; pos<COUNT; pos++) {
            int count = Integer.bitCount(cells[pos]);
            if (count > 1 && count < lowestCount) {
                lowestPos = pos;
                lowestCount = count;
            }
        }
        if (lowestPos == -1) {
            writeMatrix();
            return true; // all cells have a single distinct value, we have a solution
        } else {
            for (int value : getPossibleValues(cells[lowestPos])) {
                int[] copy = new int[COUNT];
                System.arraycopy(cells,0,copy,0,COUNT);
                boards.push(copy);
                if (assign(lowestPos, value) && search())
                    return true; // recursive search found a solution, propagate
                else
                    cells = boards.pop();
            }
        }
        return false; // no solution found
    }

    private final List<Integer> getPossibleValues(int allowed) {
        List<Integer> possibles = new ArrayList<Integer>(SIZE);
        for (int i=1, mask = 0x02; i<=SIZE; i++, mask <<= 1) {
            if ((allowed & mask) != 0) possibles.add(i);
        }
        return possibles;
    }

    /**
     * Assigns a value to a cell.
     * Instead of setting the value, we simply eliminate all others
     *
     * @param pos the cell to assign to
     * @param value the value for this cell
     * @return true when the assign was allowed. false if a contradiction is detected.
     */
    private final boolean assign(int pos, int value) {
        boolean all = true;
        for (int other : getPossibleValues(cells[pos])) {
            if (value != other) all &= eliminate(pos, other);
        }
        return all;
    }

    /**
     * Eliminate a value; propagate when values or places <= 2.
     * Return true, except when a contradiction is detected
     *
     * @param pos  the cell to eliminate from
     * @param value the value to eliminate
     * @return true when the elimination was allowed. false when a contradiction is detected
     */
    private final boolean eliminate(int pos, int value) {
        int allowed = cells[pos];
        if ((allowed & (1 << value)) == 0) {
            return true; // already eliminated
        }
        allowed &= ~(1L << value);
        cells[pos] = allowed;

        // (1) If a cell is reduced to one value d2, then eliminate d2 from the peers.
        if (allowed == 0) {
            return false; // contradiction, not an allowed combination
        } else if (Integer.bitCount(allowed) == 1) {
            int d2 = Integer.numberOfTrailingZeros(allowed); // after elimination we've found a distinct value, eliminate all peers
            boolean all = true;
            for (int peer : PEERS[pos]) all &= eliminate(peer, d2);
            if (!all) return false;
       }
        // (2) If a unit is reduced to only one cell/place for a value, then put it there.
        unitLoop:
        for (int[] unit: UNITS[pos]) {
            int possiblePos = -1;
            for (int unitPos : unit) {
                if ((cells[unitPos] & (1 << value)) != 0) {
                    if (possiblePos >= 0) {
                        continue unitLoop; // two possible cells found, continue with the next unit
                    } else {
                        possiblePos = unitPos;
                    }
                }
            }
            if (possiblePos >= 0 && !assign(possiblePos, value)) {
                return false;
            }
        }
        return true;
    }

    public void writeMatrix() {
        int pos = 0;
        for (int y = 0; y < SIZE; y++) {
            if (y % 3 == 0)
                System.out.println(" -----------------------");
            for (int x = 0; x < SIZE; x++, pos++) {
                if (x % 3 == 0) System.out.print("| ");
                int allowed = cells[pos];
                System.out.print(Integer.bitCount(allowed) > 1 ? ' ' : '0'+Integer.numberOfTrailingZeros(allowed));
                System.out.print(' ');
            }
            System.out.println("|");
        }
        System.out.println(" -----------------------");
    }
}