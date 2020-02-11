import org.junit.Test;

/**
 * Created by blagerweij on 9/20/2014.
 */
public class SudokuBoard2Test {

    @Test
    public void testImpossible() {
        int[][] cells = {
                { 0,0,0, 0,0,5, 0,8,0 },
                { 0,0,0, 6,0,1, 0,4,3 },
                { 0,0,0, 0,0,0, 0,0,0 },
                { 0,1,0, 5,0,0, 0,0,0 },
                { 0,0,0, 1,0,6, 0,0,0 },
                { 3,0,0, 0,0,0, 0,0,5 },
                { 5,3,0, 0,0,0, 0,6,1 },
                { 0,0,0, 0,0,0, 0,0,4 },
                { 0,0,0, 0,0,0, 0,0,0 }
        };
        SudokuBoard2 sudoku = new SudokuBoard2(cells);
        if (sudoku.search()) {
            throw new IllegalArgumentException("Should not find a solution for impossible sudoku");
        }
    }

    @Test
    public void testExample() {
        int[][] cells = {
                { 0,0,0, 2,6,0, 7,0,1 },
                { 6,8,0, 0,7,0, 0,9,0 },
                { 1,9,0, 0,0,4, 5,0,0 },
                { 8,2,0, 1,0,0, 0,4,0 },
                { 0,0,4, 6,0,2, 9,0,0 },
                { 0,5,0, 0,0,3, 0,2,8 },
                { 0,0,9, 3,0,0, 0,7,4 },
                { 0,4,0, 0,5,0, 0,3,6 },
                { 7,0,3, 0,1,8, 0,0,0 }
        };
        SudokuBoard2 sudoku = new SudokuBoard2(cells);
        if (!sudoku.search()) {
            throw new IllegalArgumentException("Failed to resolve");
        }
        SudokuBoard2 other = new SudokuBoard2(new int[][] {
                { 4,3,5, 2,6,9, 7,8,1 },
                { 6,8,2, 5,7,1, 4,9,3 },
                { 1,9,7, 8,3,4, 5,6,2 },
                { 8,2,6, 1,9,5, 3,4,7 },
                { 3,7,4, 6,8,2, 9,1,5 },
                { 9,5,1, 7,4,3, 6,2,8 },
                { 5,1,9, 3,2,6, 8,7,4 },
                { 2,4,8, 9,5,7, 1,3,6 },
                { 7,6,3, 4,1,8, 2,5,9 }
        });
        if (!sudoku.equals(other)) {
            throw new IllegalArgumentException("Different results");
        }
    }
}
