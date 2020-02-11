import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class SuguruBoard {
    final int width;
    final int height;
    final int length;
    final int[] sections;
    final int[] board;
    final int[][] neighbors;
    final int[][] units;

    public SuguruBoard(int width, int[] sections) {
        this.sections = sections;
        this.length = sections.length;
        this.board = new int[this.length];
        this.width = width;
        this.height = this.length / width;
        this.neighbors = new int[this.length][];
        this.units = new int[this.length][];
        final Map<Integer, Set<Integer>> groupMap = new HashMap<>();
        for (int y = 0, i = 0; y < height; y++) {
            for (int x = 0; x < width; x++, i++) {
                groupMap.computeIfAbsent(sections[i], (n) -> new HashSet<>()).add(i);
            }
        }
        groupMap.forEach((groupNumber, groupSet) -> {
            final int size = groupSet.size();
            final int initial = ~(-1 << size + 1) - 1; // initialize all bits (excl. bit 0)
            for (int node : groupSet) {
                board[node] = initial;
                units[node] = new int[size];
                int i = 0;
                for (int groupNode : groupSet) {
                    units[node][i++] = groupNode;
                }
            }
        });
        for (int y = 0, n = 0; y < height; y++) {
            for (int x = 0; x < width; x++, n++) {
                final Set<Integer> neighborSet = Arrays.stream(units[n]).boxed().collect(Collectors.toSet());
                for (int i = Math.max(0, x - 1); i < Math.min(width, x + 2); i++) {
                    for (int j = Math.max(0, y - 1); j < Math.min(height, y + 2); j++) {
                        neighborSet.add(j * width + i);
                    }
                }
                neighborSet.remove(n); // remove self from neighbors
                neighbors[n] = new int[neighborSet.size()];
                int i = 0;
                for (int neighborPos : neighborSet) {
                    neighbors[n][i++] = neighborPos;
                }
            }
        }
    }

    public void init(int x, int y, int value) {
        assign(y * width + x, value);
    }

    public boolean search() {
        // find node with smallest unsolved options
        int lowestPos = -1;
        int lowestCount = 9999;
        for (int i = 0; i < this.length; i++) {
            final int bitcount = Integer.bitCount(board[i]);
            if (bitcount > 1 && bitcount < lowestCount) {
                lowestPos = i;
                lowestCount = bitcount;
            }
        }
        if (lowestPos == -1) {
            return true; // all cells have a single distinct value, we have a solution
        }
        final int bitset = board[lowestPos];
        for (int i = 1, mask = 2; i <= 5; i++, mask <<= 1) {
            if ((bitset & mask) != 0) {
                final int[] copy = new int[this.length];
                System.arraycopy(board, 0, copy, 0, length);
                if (assign(lowestPos, i) && search()) {
                    return true; // recursive search found a solution, propagate
                }
                System.arraycopy(copy, 0, board, 0, length); // restore board
            }
        }
        return false;
    }

    public boolean assign(int pos, int value) {
        boolean all = true;
        for (int other = 1, mask = 2; other <= 5; other++, mask <<= 1) {
            if ((board[pos] & mask) != 0 && other != value) {
                all &= eliminate(pos, other);
            }
        }
        return all;
    }

    public boolean eliminate(int pos, int value) {
        int bitset = board[pos];
        final int mask = 1 << value;
        if ((bitset & mask) == 0) {
            return true; // already eliminated
        }
        bitset &= ~mask;
        board[pos] = bitset;

        // (1) If a cell is reduced to one value d2, then eliminate d2 from the peers.
        if (bitset == 0) {
            return false; // contradiction, not an allowed combination
        } else if (Integer.bitCount(bitset) == 1) {
            int d2 = Integer.numberOfTrailingZeros(bitset); // after elimination we've found a distinct value, eliminate all peers
            boolean all = true;
            for (int peer : neighbors[pos]) all &= eliminate(peer, d2);
            if (!all) return false;
        }
        // (2) If a unit is reduced to only one cell/place for a value, then put it there.
        int possiblePos = -1;
        for (int unitPos : units[pos]) {
            if ((board[unitPos] & mask) != 0) {
                if (possiblePos >= 0) {
                    return true; // two possible cells found, continue
                } else {
                    possiblePos = unitPos;
                }
            }
        }
        return possiblePos < 0 || assign(possiblePos, value);
    }

    public void print() {
        for (int x = 0; x < width; ++x) {
            System.out.print("+---");
        }
        System.out.println("+");
        for (int y = 0, i = 0; y < height; ++y) {
            System.out.print("|");
            for (int x = 0; x < width; x++, i++) {
                System.out.print(' ');
                System.out.print(Integer.bitCount(board[i]) == 1 ? (char) ('0' + Integer.numberOfTrailingZeros(board[i])) : ' ');
                System.out.print(x < width - 1 && sections[i + 1] == sections[i] ? "  " : " |");
            }
            System.out.println();
            i -= width;
            for (int x = 0; x < width; x++, i++) {
                System.out.print(y < height - 1 && sections[i + width] == sections[i] ? "+   " : "+---");
            }
            System.out.println("+");
        }
    }
}
