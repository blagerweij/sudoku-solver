public class Sudoku {

    static boolean solve(int x, int y, int[][] cells) {
        if (x == 9) {
            if (y == 8)
                return true;
            else
                return solve(0, y+1,cells); // next row
        }
        if (cells[x][y] != 0)  // skip filled cells
            return solve(x+1,y,cells);

        for (int val = 1; val <= 9; ++val) {
            if (legal(x, y,val,cells)) {
                cells[x][y] = val;
                if (solve(x+1, y,cells))
                    return true;
            }
        }
        cells[x][y] = 0; // reset on backtrack
        return false;
    }

    static boolean legal(int x, int y, int val, int[][] cells) {
        for (int i = 0; i < 9; ++i)  // row
            if (val == cells[i][y])
                return false;

        for (int i = 0; i < 9; ++i) // col
            if (val == cells[x][i])
                return false;

        int bx = (x / 3)*3;
        int by = (y / 3)*3;
        for (int i = bx; i < bx+3; ++i) // box
            for (int j = by; j < by+3; ++j)
                if (val == cells[bx][by])
                    return false;

        return true; // no violations, so it's legal
    }

    static void writeMatrix(int[][] cells) {
        for (int y = 0; y < 9; ++y) {
            if (y % 3 == 0)
                System.out.println(" -----------------------");
            for (int x = 0; x < 9; ++x) {
                if (x % 3 == 0) System.out.print("| ");
                int val = cells[y][x];
                System.out.print((char)(val == 0 ? ' ' : '0'+val));
                System.out.print(' ');
            }
            System.out.println("|");
        }
        System.out.println(" -----------------------");
    }

    public static void main ( String[] args ) {
        int[][] cells = {

//            { 0,4,3, 7,0,0, 9,0,8 },
//            { 0,0,5, 0,3,0, 0,0,0 },
//            { 0,1,0, 0,0,0, 3,0,0 },
//            { 6,0,0, 0,2,7, 0,0,0 },
//            { 4,0,7, 0,0,0, 1,0,3 },
//            { 0,0,0, 5,4,0, 0,0,9 },
//            { 0,0,2, 0,0,0, 0,3,0 },
//            { 0,0,0, 0,5,0, 4,0,0 },
//            { 5,0,4, 0,0,1, 2,6,0 }

//                { 0,0,0, 0,0,7, 0,1,8 },
//                { 0,9,4, 1,5,0, 7,0,0 },
//                { 0,0,5, 6,0,0, 0,0,0 },
//                { 1,0,6, 0,0,0, 0,0,0 },
//                { 0,8,0, 0,7,0, 0,2,0 },
//                { 0,0,0, 0,0,0, 9,0,4 },
//                { 0,0,0, 0,0,3, 8,0,0 },
//                { 0,0,8, 0,2,9, 1,4,0 },
//                { 3,7,0, 4,0,0, 0,0,0 }
            { 8,1,0, 0,0,0, 0,0,0 },
            { 0,0,3, 6,0,0, 0,0,0 },
            { 0,7,0, 0,9,0, 2,0,0 },
            { 0,5,0, 0,0,7, 0,0,0 },
            { 0,0,0, 0,4,5, 7,0,0 },
            { 0,0,0, 1,0,0, 0,3,0 },
            { 0,0,1, 0,0,0, 0,6,8 },
            { 0,0,8, 5,0,0, 0,1,0 },
            { 0,9,0, 0,0,0, 4,0,0 }
        };
        writeMatrix(cells);
        if (solve(0,0,cells))
            writeMatrix(cells);
    }
}