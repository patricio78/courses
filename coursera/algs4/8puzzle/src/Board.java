import edu.princeton.cs.algs4.In;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Board
{
    private final int [][] blocks;

    // construct a board from an n-by-n array of blocks
    // (where blocks[i][j] = block in row i, column j)
    public Board(int[][] blocks)
    {
        if (blocks == null) throw new NullPointerException();

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
        int count = 0;
        for (int i = 0; i < dimension(); i++) {
            for (int j = 0; j <  dimension(); j++) {
                int block = blocks[i][j];
                if (block != 0 && dimension() * i + j + 1 != block) {
                    count++;
                }
            }
        }
        return count;
    }

    // sum of Manhattan distances between blocks and goal
    public int manhattan()
    {
        int distance = 0;
        for (int i = 0; i < dimension(); i++) {
            for (int j = 0; j <  dimension(); j++) {
                int block = blocks[i][j];
                if (block != 0 && dimension() * i + j + 1 != block) {
                    int xgoal = (block -1) / dimension();
                    int ygoal = (block -1) % dimension();

                    int dx = Math.abs( i - xgoal );
                    int dy = Math.abs( j - ygoal );
                    distance += dx + dy;
                }
            }
        }
        return distance;
    }

    // is this board the goal board?
    public boolean isGoal()
    {
        for (int i = 0; i < dimension(); i++) {
            for (int j = 0; j <  dimension(); j++) {
                int block = blocks[i][j];
                if (i == dimension()-1 && j == dimension()-1) {
                    if (block != 0) return false;
                }
                else if (dimension() * i + j + 1 != block) {
                    return false;
                }
            }
        }
        return true;
    }

    // a board that is obtained by exchanging any pair of blocks
    public Board twin()
    {
        int i = 0,j = 0, i2 = 0, j2 = 0;
        int chosen = 0;
        for (int k = 0; k < dimension(); k++) {
            for (int l = 0; l < dimension(); l++) {
                if (blocks[k][l] != 0) {
                    if (chosen++ == 0) {
                        i = k;
                        j = l;
                    }
                    else {
                        i2 = k;
                        j2 = l;
                        break;
                    }
                }
            }

        }

        final Board board = new Board(blocks);
        board.swap(i, j, i2, j2);
        return board;
    }

    private static void swap(int i, int j, int i2, int j2, int[][] blocks)
    {
        int temp = blocks[i][j];
        blocks[i][j] = blocks[i2][j2];
        blocks[i2][j2] = temp;
    }

    private void swap(int i, int j, int i2, int j2)
    {
        swap(i, j, i2, j2, this.blocks);
    }

    // does this board equal y?
    public boolean equals(Object y)
    {
        if (! (y instanceof Board)) return false;

        Board other = (Board) y;
        for (int i = 0; i < blocks.length; i++) {
            if (!Arrays.equals(this.blocks[i], other.blocks[i])) {
                return false;
            }
        }
        return true;
    }

    // all neighboring boards
    public Iterable<Board> neighbors()
    {
        final List<Board> result = new ArrayList<>();
        for (int i = 0; i < dimension(); i++) {
            for (int j = 0; j < dimension(); j++) {

                if (blocks[i][j] == 0) {

                    if (i < dimension() - 1) {
                        Board board = new Board(this.blocks);
                        result.add(board);
                        board.swap(i, j, i + 1, j);
                    }
                    if (i > 0) {
                        Board board = new Board(this.blocks);
                        result.add(board);
                        board.swap(i, j, i - 1, j);
                    }
                    if (j < dimension() - 1) {
                        Board board = new Board(this.blocks);
                        result.add(board);
                        board.swap(i, j, i, j + 1);
                    }
                    if (j > 0) {
                        Board board = new Board(this.blocks);
                        result.add(board);
                        board.swap(i, j, i, j - 1);
                    }
                    break;
                }
            }
        }
        return result;
    }

    // string representation of this board (in the output format specified below)
    public String toString()
    {
        final int length = (int) Math.ceil(Math.log10(dimension() * dimension()));
        final StringBuilder builder = new StringBuilder();
        builder.append(dimension()).append("\n");
        for (int i = 0; i < dimension(); i++) {
            builder.append(" ");
            for (int j = 0; j < dimension(); j++) {
                builder.append(String.format("%" + length + "d ", blocks[i][j]));
            }
            builder.append("\n");
        }
        return builder.toString();
    }

    public static void main(String[] args) {
        // create initial board from file
        In in = new In(args[0]);
        int n = in.readInt();
        int[][] blocks = new int[n][n];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                blocks[i][j] = in.readInt();
        Board initial = new Board(blocks);
        Board another = new Board(blocks);

        System.out.println(initial.toString());
        System.out.printf("equals?: %b\n", initial.equals(another));
        System.out.printf("isGoal: %b\n", initial.isGoal());
        System.out.printf("hamming: %d\n", initial.hamming());
        System.out.printf("manhattan: %d\n", initial.manhattan());

        System.out.println("twin:" + initial.twin());
        System.out.println("neighbours");
        int totalNeighbours = 0;
        for (Board board : initial.neighbors()) {
            System.out.println("=======================");
            System.out.println(board);
            totalNeighbours++;
        }
        System.out.printf("total neighbours:%d\n", totalNeighbours);
    }
}
