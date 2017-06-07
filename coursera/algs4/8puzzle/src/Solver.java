import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.MinPQ;
import edu.princeton.cs.algs4.StdOut;

import java.util.ArrayList;
import java.util.List;

public class Solver
{
    private MinPQ<SearchNode> pq;
    private MinPQ<SearchNode> pqTwin;
//    private final SearchNode solution;
    private final List<Board> shortestPath = new ArrayList<>();
    private int numberOfMoves = -1;


    // find a solution to the initial board (using the A* algorithm)
    public Solver(Board initial)
    {
        pq = new MinPQ<>();
        pqTwin = new MinPQ<>();

        pq.insert(new SearchNode(initial, 0, null));
        pqTwin.insert(new SearchNode(initial.twin(), 0, null));

        SearchNode solution = null;
        while (!pq.isEmpty() || !pqTwin.isEmpty()) {
            solution = processQueue(pq);
            if (solution != null) break;

            solution = processQueue(pqTwin);
            if (solution != null) {
                solution = null;
                break;
            }
        }
        if (solution != null) {
            numberOfMoves = solution.numberOfMoves;

            SearchNode current = solution;
            while (current != null) {
                shortestPath.add(0, current.board);
                current = current.previous;
            }
        }
        pq = null;
        pqTwin = null;
    }

    private static SearchNode processQueue(final MinPQ<SearchNode> queue)
    {
        if (!queue.isEmpty()) {
            final SearchNode searchNode = queue.delMin();
            if (searchNode.board.isGoal()) {
                return searchNode;
            }
            for (Board neighbour : searchNode.board.neighbors()) {
                if (searchNode.previous == null || !neighbour.equals(searchNode.previous.board)) {
                    queue.insert(new SearchNode(neighbour, searchNode.numberOfMoves+1, searchNode));
                }
            }
        }
        return null;
    }

    // is the initial board solvable?
    public boolean isSolvable()
    {
        return moves() >= 0;
    }

    // min number of moves to solve initial board; -1 if unsolvable
    public int moves()
    {
        return numberOfMoves;
    }

    // sequence of boards in a shortest solution; null if unsolvable
    public Iterable<Board> solution()
    {
        Iterable<Board> result = null;
        if (isSolvable()) {
            result = shortestPath;
        }
        return result;
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

        // solve the puzzle
        Solver solver = new Solver(initial);

        // print solution to standard output
        if (!solver.isSolvable())
            StdOut.println("No solution possible");
        else {
            StdOut.println("Minimum number of moves = " + solver.moves());
            for (Board board : solver.solution())
                StdOut.println(board);
        }
    }

    private static class SearchNode
        implements Comparable<SearchNode>
    {
        private final Board board;
        private final int numberOfMoves;
        private final SearchNode previous;

        private SearchNode(Board board, int numberOfMoves, SearchNode previous) {
            this.board = board;
            this.numberOfMoves = numberOfMoves;
            this.previous = previous;
        }

        @Override
        public int compareTo(SearchNode o) {
            return board.manhattan() + numberOfMoves - (o.board.manhattan() + o.numberOfMoves);
        }
    }
}
