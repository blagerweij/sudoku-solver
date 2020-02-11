import org.junit.Test;

import static org.junit.Assert.fail;

public class SuguruSolverTest {
    @Test
    public void testSimple() {
        System.out.println("Hello");
        SuguruBoard board = new SuguruBoard(4, new int [] {
                1, 2, 2, 2,
                1, 2, 2, 3,
                1, 3, 3, 3 ,
                4, 4, 4, 3,
                5, 5, 4, 4
        });
        board.init(2,0,5);
        board.init(1,3,4);
        board.init(3,4,1);
        board.init(2,2,5);
        board.print();
        if (board.search()) {
            board.print();
        }
    }

    @Test
    public void test2() {
        SuguruBoard board = new SuguruBoard(4, new int [] {
                1, 2, 2, 2,
                3, 2, 2, 4,
                3, 3, 3, 4,
                5, 5, 3, 4,
                5, 5, 5, 4
        });
        board.init(1, 0, 4);
        board.init(3, 0, 3);
        board.init(1, 4, 5);
        board.init(3, 4, 3);
        board.print();
        if (board.search()) {
            board.print();
        } else {
            board.print();
            fail();
        }
    }

    @Test
    public void testLarge() {
        SuguruBoard board = new SuguruBoard(9, new int [] {
                1, 1, 1, 1, 2, 2, 3, 3, 3,
                4, 4, 1, 2, 2, 3, 3, 7, 7,
                4, 4, 5, 5, 6, 6, 6, 7, 7,
                4, 8, 8, 5, 5, 6, 6,11, 7,
                8, 8, 8, 9, 5,10,11,11,11
        });
        board.init(0,0,1);
        board.init(3,0,5);
        board.init(8,0,4);
        board.init(0,2,3);
        board.init(7,2,5);
        board.init(2,3,3);
        board.init(4,3,4);
        board.init(0,4,2);
        board.print();
        if (board.search()) {
            board.print();
        } else {
            board.print();
            fail();
        }
    }

    @Test
    public void test3() {
        SuguruBoard board = new SuguruBoard(4, new int [] {
                1, 1, 1, 2,
                3, 1, 2, 2,
                3, 1, 2, 4,
                3, 3, 4, 4,
                3, 4, 4, 5
        });
        board.init(1, 0, 1);
        board.init(0, 2, 2);
        board.init(3, 3, 4);
        board.init(1, 4, 2);
        board.print();
        if (board.search()) {
            board.print();
        } else {
            board.print();
            fail();
        }
    }
}
