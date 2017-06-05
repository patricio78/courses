import java.util.Arrays;

public class Board
{
    private final int [][] blocks;

    // construct a board from an n-by-n array of blocks
    // (where blocks[i][j] = block in row i, column j)
    public Board(int[][] blocks)
    {
        this.blocks = new int[blocks.length][];
        for (int i = 0; i < blocks.length; i++) {
            this.blocks[i] = Arrays.copyOf(blocks[i], blocks[i].length);
        }
    }

    // board dimension n
    public int dimension()
    {
        return blocks.length;
    }

    // number of blocks out of place
    public int hamming()
    {

    }

    // sum of Manhattan distances between blocks and goal
    public int manhattan()
    {}

    // is this board the goal board?
    public boolean isGoal()
    {}

    // a board that is obtained by exchanging any pair of blocks
    public Board twin()
    {}

    // does this board equal y?
    public boolean equals(Object y)
    {}

    // all neighboring boards
    public Iterable<Board> neighbors()
    {}

    // string representation of this board (in the output format specified below)
    public String toString()
    {}

    public static void main(String[] args)
    {}
}
