import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdOut;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BruteCollinearPoints
{
    private List<LineSegment> segments = new ArrayList<>();
    public BruteCollinearPoints(Point[] points)
    {
        if (points == null) throw new NullPointerException();

        Point[] dupPoints = new Point[points.length];
        System.arraycopy(points, 0, dupPoints, 0, points.length);

        Arrays.sort(dupPoints);
        checkPoints(dupPoints);

        for (int i = 0; i < points.length; i++) {
            final Point p = points[i];
            if (p == null) throw new NullPointerException();
            for (int j = i+1; j < points.length; j++) {
                final Point q = points[j];
                if (q == null) throw new NullPointerException();
                for (int k = j+1; k < points.length; k++) {
                    final Point r = points[k];
                    if (r == null) throw new NullPointerException();
                    for (int l = k+1; l < points.length; l++) {
                        final Point s = points[l];
                        if (s == null) throw new NullPointerException();

                        if (p.compareTo(q) == 0 || p.compareTo(r) == 0 || p.compareTo(s) == 0) throw new IllegalArgumentException();

                        final double slopeToQ = p.slopeTo(q);
                        final double slopeToR = p.slopeTo(r);
                        final double slopeToS = p.slopeTo(s);

                        if (slopeToQ == slopeToR && slopeToR == slopeToS) {
                            final Point[] pointsInSegment = new Point[4];
                            pointsInSegment[0] = p;
                            pointsInSegment[1] = q;
                            pointsInSegment[2] = r;
                            pointsInSegment[3] = s;

                            Arrays.sort(pointsInSegment);

                            segments.add(new LineSegment(pointsInSegment[0], pointsInSegment[3]));
                        }
                    }
                }
            }

        }
    }

    public           int numberOfSegments()
    {
        return segments.size();
    }

    public LineSegment[] segments()

    {
        return segments.toArray(new LineSegment[0]);
    }

    private void checkPoints(Point[] points)
    {

        if (points[0] == null) throw new NullPointerException();

        for (int i = 1; i < points.length; i++) {
            if (points[i] == null) throw new NullPointerException();

            if (points[i-1].compareTo(points[i]) == 0) throw new IllegalArgumentException();
        }
    }

    public static void main(String[] args)
    {
        // read the n points from a file
        In in = new In(args[0]);
        int n = in.readInt();
        Point[] points = new Point[n];
        for (int i = 0; i < n; i++) {
            int x = in.readInt();
            int y = in.readInt();
            points[i] = new Point(x, y);
        }

        // draw the points
        StdDraw.enableDoubleBuffering();
        StdDraw.setXscale(0, 32768);
        StdDraw.setYscale(0, 32768);
        for (Point p : points) {
            p.draw();
        }
        StdDraw.show();

        // print and draw the line segments
        BruteCollinearPoints collinear = new BruteCollinearPoints(points);
        for (LineSegment segment : collinear.segments()) {
            StdOut.println(segment);
            segment.draw();
        }
        StdDraw.show();
    }
}
