import edu.princeton.cs.algs4.WeightedQuickUnionUF;

/**
 * Created by patricio on 5/19/17.
 */
public class Percolation {
    private final boolean[][] opened;
    private WeightedQuickUnionUF quickUnionUF;
    private WeightedQuickUnionUF fullUnionUF;
    private int n;
    private int openSites = 0;
    public Percolation(int n)
    {
        if (n <= 0) throw new IllegalArgumentException();
        this.n = n;
        quickUnionUF = new WeightedQuickUnionUF(n * n + 2);
        fullUnionUF = new WeightedQuickUnionUF(n * n + 2);
        opened = new boolean[n][n];
    }

    public void open(int row, int col)
    {
        checkBoundaries(row, col);

        if (isOpen(row, col)) return;

        int p = indexRowCol(row, col);
        if (row > 1) {
            if (isOpen(row-1, col)) {
                quickUnionUF.union(p, indexRowCol(row - 1, col));
                fullUnionUF.union(p, indexRowCol(row - 1, col));
            }
        }
        else {
            quickUnionUF.union(p, n*n);
            fullUnionUF.union(p, n*n);
        }

        if (row < n) {
            if (isOpen(row+1, col)) {
                quickUnionUF.union(p, indexRowCol(row+1, col));
                fullUnionUF.union(p, indexRowCol(row+1, col));
            }
        }
        else {
            quickUnionUF.union(p, n*n+1);
        }

        if (col > 1) {
            if (isOpen(row, col-1)) {
                quickUnionUF.union(p, indexRowCol(row, col-1));
                fullUnionUF.union(p, indexRowCol(row, col-1));
            }
        }

        if (col < n) {
            if (isOpen(row, col+1)) {
                quickUnionUF.union(p, indexRowCol(row, col+1));
                fullUnionUF.union(p, indexRowCol(row, col+1));
            }
        }

        opened[row-1][col-1] = true;
        openSites++;
    }

    private void checkBoundaries(int row, int col) {
        if (row < 1 || row > n) throw new IndexOutOfBoundsException();
        if (col < 1 || col > n) throw new IndexOutOfBoundsException();
    }

    private int indexRowCol(int row, int col) {
        return (row-1)*n + col-1;
    }

    public boolean isOpen(int row, int col)
    {
        checkBoundaries(row, col);

        return opened[row-1][col-1];
    }

    public boolean isFull(int row, int col) {

        checkBoundaries(row, col);
        return isOpen(row, col) && fullUnionUF.connected(indexRowCol(row, col), n * n);


    }

    public     int numberOfOpenSites()
    {
        return openSites;
    }

    public boolean percolates()
    {
        return quickUnionUF.connected(n*n, n*n+1);
    }

//    public static void main(String[] args) throws FileNotFoundException {
//        System.setIn(new FileInputStream(args[0]));
//        int n = StdIn.readInt();
//        Percolation percolation = new Percolation(n);
//        while(!StdIn.isEmpty()) {
//            int p = StdIn.readInt();
//            int q = StdIn.readInt();
//            percolation.open(p, q);
//        }
//        PercolationVisualizer.draw(percolation, n);
//    }
}
