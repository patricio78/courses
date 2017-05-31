import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdOut;

import java.util.*;

public class FastCollinearPoints
{
    private final LineSegment[] lineSegments;

    public FastCollinearPoints(Point[] points)
    {
        if (points == null) throw new NullPointerException();

        List<Segment> segments = new ArrayList<>();

        Point[] dupPoints = Arrays.copyOf(points, points.length);
        Arrays.sort(dupPoints);
        checkPoints(dupPoints);

        for (int i = 0; i < points.length; i++) {
            Point p = points[i];

            if (p == null) throw new NullPointerException();

            Arrays.sort(dupPoints, p.slopeOrder());
            List<Point> collinear = new ArrayList<>(4);

            double previousSlope = p.slopeTo(dupPoints[0]);
            for (int j = 1; j < dupPoints.length; j++) {

                final double slopeToJ = p.slopeTo(dupPoints[j]);

                if (slopeToJ == previousSlope) {
                    collinear.add(dupPoints[j]);
                }
                else {
                    addSegment(collinear, segments);
                    collinear = new ArrayList<>(4);
                    collinear.add(p);
                    collinear.add(dupPoints[j]);
                }
                previousSlope = slopeToJ;

            }

            addSegment(collinear, segments);

        }

        lineSegments = computeResult(segments);
    }

    private void addSegment(final List<Point> collinear, final List<Segment> segments) {
        if (collinear.size() >= 4) {
            final Point[] collinearArray = collinear.toArray(new Point[collinear.size()]);
            Arrays.sort(collinearArray);
            final Segment segment = new Segment(collinearArray[0], collinearArray[collinearArray.length - 1]);

            int index = Collections.binarySearch(segments, segment);
            if (index >= 0) return;

            segments.add(-index-1, segment);
        }
    }

    private void checkPoints(Point[] points)
    {

        if (points[0] == null) throw new NullPointerException();

        for (int i = 1; i < points.length; i++) {
            if (points[i] == null) throw new NullPointerException();

            if (points[i-1].compareTo(points[i]) == 0) throw new IllegalArgumentException();
        }
    }

    public           int numberOfSegments()
    {
        return lineSegments.length;
    }

    public LineSegment[] segments()
    {
        return Arrays.copyOf(lineSegments, lineSegments.length);
    }

    private LineSegment[] computeResult(List<Segment> segments) {
        LineSegment[] lineSegments = new LineSegment[segments.size()];
        for (int i = 0; i < lineSegments.length; i++) {
            final Segment segment = segments.get(i);
            lineSegments[i] = new LineSegment(segment.p, segment.q);
        }
        return lineSegments;
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
        FastCollinearPoints collinear = new FastCollinearPoints(points);
        for (LineSegment segment : collinear.segments()) {
            StdOut.println(segment);
            segment.draw();
        }
        StdDraw.show();
    }

    private static class Segment
        implements Comparable<Segment>
    {
        private final Point p,q;

        public Segment(Point p, Point q) {
            this.p = p;
            this.q = q;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Segment segment = (Segment) o;
            return p.compareTo(segment.p) == 0 &&
                    q.compareTo(segment.q) == 0;
        }

        @Override
        public int hashCode() {
            return Objects.hash(p, q);
        }

        @Override
        public int compareTo(Segment o) {
            final int pCompare = p.compareTo(o.p);
            if (pCompare < 0) return -1;
            else if (pCompare == 0) {
                return q.compareTo(o.q);
            }
            else {
                return 1;
            }
        }
    }
}
