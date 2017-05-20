import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdRandom;
import edu.princeton.cs.algs4.StdStats;

/**
 * Created by patricio on 5/19/17.
 */
public class PercolationStats
{
    private double mean;
    private double stddev;
    private double confidenceLo, confidenceHi;

    public PercolationStats(int n, int trials)
    {
        if (n <= 0 || trials <= 0) throw new IllegalArgumentException();

        double[] threshold = new double[trials];
        for (int i = 0; i < trials; i++) {
//            Stopwatch stopwatch = new Stopwatch();
            final Percolation percolation = new Percolation(n);
            while (!percolation.percolates()) {
                int row = StdRandom.uniform(n) + 1;
                int col = StdRandom.uniform(n) + 1;
                if (!percolation.isOpen(row, col)) {
                    percolation.open(row, col);
                }
            }
//            StdOut.printf("%f\n", stopwatch.elapsedTime());
            threshold[i] = percolation.numberOfOpenSites() / (double) (n * n);
        }
        mean = StdStats.mean(threshold);
        stddev = StdStats.stddev(threshold);

        double v = 1.96*stddev / Math.sqrt(trials);
        confidenceLo = mean - v;
        confidenceHi = mean + v;
    }

    public double mean()
    {
        return mean;
    }
    public double stddev()
    {
        return stddev;
    }

    public double confidenceLo()
    {
        return confidenceLo;
    }
    public double confidenceHi()
    {
        return confidenceHi;
    }

    public static void main(String[] args)
    {
        PercolationStats stats = new PercolationStats(Integer.parseInt(args[0]), Integer.parseInt(args[1]));
        StdOut.printf("mean                     = %f\n", stats.mean());
        StdOut.printf("stddev                   = %f\n", stats.stddev());
        StdOut.printf("95%% confidence interval = [%f, %f]\n", stats.confidenceLo(), stats.confidenceHi());
    }
}
