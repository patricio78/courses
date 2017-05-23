import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Created by patricio on 5/22/17.
 */
public class Deque<Item> implements Iterable<Item> {
    private Node first, last;
    private int size = 0;

    public Deque()
    {

    }
    public boolean isEmpty()
    {
        return first == last;
    }

    public int size()
    {
        return size;
    }

    public void addFirst(Item item)
    {
        if (item == null) throw new NullPointerException();

        final Node oldFirst = first;
        first = new Node();
        first.item = item;
        first.next = oldFirst;

        if (oldFirst == null)
        {
            last = first;
        }
        else {
            oldFirst.prev = first;
        }

        size++;

    }
    public void addLast(Item item)
    {
        if (item == null) throw new NullPointerException();

        final Node oldLast = last;
        last = new Node();
        last.item = item;
        last.prev = oldLast;

        if (oldLast == null)
        {
            first = last;
        }
        else {
            oldLast.next = last;
        }

        size++;

    }
    public Item removeFirst()
    {
        if (first == null) throw new NoSuchElementException();

        final Item item = first.item;
        first = first.next;
        if (first == null) {
            last = null;
        }
        else {
            first.prev = null;
        }
        size--;
        return item;
    }

    public Item removeLast()
    {
        if (last == null) throw new NoSuchElementException();

        final Item item = last.item;
        last = last.prev;
        if (last == null) {
            first = null;
        }
        else {
            last.next = null;
        }
        size--;
        return item;
    }

    public Iterator<Item> iterator()
    {
        return new DequeueIterator(first, last);

    }
    public static void main(String[] args)
    {
        Deque<String> deque = new Deque();

        deque.addFirst("first1");
        deque.addFirst("first2");
        deque.addLast("last1");

        String s = deque.removeFirst();
        s = deque.removeLast();
        s = deque.removeLast();
        ;
    }

    private class Node {
        private Item item;
        private Node prev, next;
    }

    private class DequeueIterator
        implements Iterator<Item>
    {
        private Node first,     last;

        private DequeueIterator(Node first, Node last) {
            this.first = first;
            this.last = last;
        }

        @Override
        public boolean hasNext() {
            return first != last;
        }

        @Override
        public Item next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            final Item item = first.item;
            first = first.next;
            return item;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}