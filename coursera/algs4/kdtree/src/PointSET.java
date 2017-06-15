import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.StdDraw;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

/**
 * Created by patricio on 6/14/17.
 */
public class PointSET
{
    private final TreeSet<Point2D> points;

    // construct an empty set of points
    public         PointSET()
    {
        points = new TreeSet<>();
    }

    // is the set empty?
    public           boolean isEmpty()
    {
        return points.isEmpty();
    }

    // number of points in the set
    public               int size()
    {
        return points.size();
    }

    // add the point to the set (if it is not already in the set)
    public              void insert(Point2D p)
    {
        if (p == null) throw new NullPointerException();

        points.add(p);
    }

    // does the set contain point p?
    public           boolean contains(Point2D p)
    {
        if (p == null) throw new NullPointerException();

        return points.contains(p);
    }

    // draw all points to standard draw
    public              void draw()
    {
        for (Point2D point : points) {
            StdDraw.point(point.x(), point.y());
        }
    }

    // all points that are inside the rectangle
    public Iterable<Point2D> range(RectHV rect)
    {
        if (rect == null) throw new NullPointerException();

        final List<Point2D> result = new ArrayList<>();
        for (Point2D point : points) {
            if (rect.contains(point)) {
                result.add(point);
            }
        }

        return result;
    }

    // a nearest neighbor in the set to point p; null if the set is empty
    public           Point2D nearest(Point2D p)
    {
        if (p == null) throw new NullPointerException();

        Point2D result = null;
        for (Point2D point : points) {
            if (result == null
                    || point.distanceSquaredTo(p) < result.distanceSquaredTo(p)) {
                result = point;
            }
        }
        return result;

    }

    // unit testing of the methods (optional)
    public static void main(String[] args)
    {
        String filename = args[0];
        In in = new In(filename);

        PointSET pointSet = new PointSET();
        while (!in.isEmpty()) {
            double x = in.readDouble();
            double y = in.readDouble();
            Point2D p = new Point2D(x, y);
            pointSet.insert(p);
        }

        Point2D nearest = pointSet.nearest(new Point2D(0.3, 0.0));
        System.out.println(nearest);
    }
}
