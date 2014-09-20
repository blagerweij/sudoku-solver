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

}
