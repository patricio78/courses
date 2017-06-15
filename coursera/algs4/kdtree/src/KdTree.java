import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.StdDraw;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by patricio on 6/14/17.
 */
public class KdTree
{
    private Node root = null;
    private int size = 0;

    // construct an empty set of points
    public KdTree()
    {

    }

    // is the set empty?
    public           boolean isEmpty()
    {
        return root == null;
    }

    // number of points in the set
    public               int size()
    {
        return size;
    }

    // add the point to the set (if it is not already in the set)
    public              void insert(Point2D p)
    {
        if (p == null) throw new NullPointerException();

        root = insert(root, p, true, 0.0, 0.0, 1.0, 1.0);
    }

    private Node insert(Node node, Point2D p, boolean useXCoordinate, double x0, double y0, double x1, double y1)
    {
        if (node == null) {
            size++;
            RectHV r = new RectHV(x0, y0, x1, y1);
            return new Node(p, r);
        }
        // If the point already exists, just return
        else if (p.compareTo(node.point) == 0) return node;
        // The current node is vertical: compare x-coordinates
        if (useXCoordinate) {
            double cmp = p.x() - node.point.x();
            if (cmp < 0)
                node.left = insert(node.left, p, !useXCoordinate,x0, y0, node.point.x(), y1);
            else
                node.right = insert(node.right, p, !useXCoordinate, node.point.x(), y0, x1, y1);
        }
        // The current node is horizontal: compare y-coordinates
        else {
            double cmp = p.y() - node.point.y();
            if (cmp < 0)
                node.left = insert(node.left, p, !useXCoordinate,x0, y0, x1, node.point.y());
            else
                node.right = insert(node.right, p, !useXCoordinate, x0, node.point.y(), x1, y1);
        }
        return node;
    }

    // does the set contain point p?
    public           boolean contains(Point2D p)
    {
        if (p == null) throw new NullPointerException();

        return contains(root, p, true);
    }

    private boolean contains(Node node, Point2D p, boolean xCoordinate)
    {
        if (node == null) return false;
        else if (p.compareTo(node.point) == 0) return true;
        else {
            double cmp;
            if (xCoordinate) {
                cmp = p.x() - node.point.x();
            }
            else {
                cmp = p.y() - node.point.y();
            }
            if (cmp < 0) return contains(node.left, p, !xCoordinate);
            else return contains(node.right, p, !xCoordinate);
        }

    }

    // draw all points to standard draw
    public              void draw()
    {
        draw(root, true);
    }

    private void draw(Node node, boolean drawVert) {
        if (node == null) return;
        // Draw point
        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.setPenRadius(0.01);
        node.point.draw();
        // Draw vertical line with x-coordinates of the point and y-coordinates
        // of the parent rectangle
        if (drawVert) {
            StdDraw.setPenColor(StdDraw.RED);
            StdDraw.setPenRadius();
            StdDraw.line(node.point.x(), node.rectangle.ymin(), node.point.x(), node.rectangle.ymax());
        }
        // Draw horizontal line with y-coordinates of the point and x-coordinates
        // of the parent rectangle
        else {
            StdDraw.setPenColor(StdDraw.BLUE);
            StdDraw.setPenRadius();
            StdDraw.line(node.rectangle.xmin(), node.point.y(), node.rectangle.xmax(), node.point.y());
        }
        // Draw subtrees
        draw(node.left, !drawVert);
        draw(node.right, !drawVert);
    }

    // all points that are inside the rectangle
    public Iterable<Point2D> range(RectHV rect)
    {
        if (rect == null) throw new NullPointerException();

        final List<Point2D> result = new ArrayList<>();
        range(root, rect, result);
        return result;
    }

    private void range(Node node, RectHV rectangle, List<Point2D> result)
    {
        if (node == null || !rectangle.intersects(node.rectangle)) return;
        else {
            if (rectangle.contains(node.point)) {
                result.add(node.point);
            }
            range(node.left, rectangle, result);
            range(node.right, rectangle, result);
        }
    }

    // a nearest neighbor in the set to point p; null if the set is empty
    public           Point2D nearest(Point2D p)
    {
        if (p == null) throw new NullPointerException();

        if (isEmpty()) return null;

        return nearest(root, p, root.point, true);
    }

    private Point2D nearest(Node node, Point2D p, Point2D champion, boolean xCoordinate)
    {
        Point2D closest = champion;
        if (node == null) return closest;
        if (node.point.distanceSquaredTo(p) < closest.distanceSquaredTo(p)) {
            closest = node.point;
        }

        if (node.rectangle.distanceSquaredTo(p) < closest.distanceSquaredTo(p)) {
            Node near, far;

            if ((xCoordinate && (p.x() < node.point.x())) || (!xCoordinate && (p.y() < node.point.y()))) {
                near = node.left;
                far = node.right;
            }
            else {
                near = node.right;
                far = node.left;
            }

            closest = nearest(near, p, closest, !xCoordinate);
            closest = nearest(far, p, closest, !xCoordinate);
        }
        return closest;
    }

    // unit testing of the methods (optional)
    public static void main(String[] args)
    {
        String filename = args[0];
        In in = new In(filename);

        KdTree kdtree = new KdTree();
        while (!in.isEmpty()) {
            double x = in.readDouble();
            double y = in.readDouble();
            Point2D p = new Point2D(x, y);
            kdtree.insert(p);
        }

        Point2D range = kdtree.nearest(new Point2D(0.3, 0.0));
        System.out.println(range);
    }

    private static class Node
    {
        private Point2D point;
        private Node left, right;
        private RectHV rectangle;

        public Node(Point2D point, RectHV rectangle) {
            this.point = point;
            this.rectangle = rectangle;
        }
    }
}
