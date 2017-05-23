import edu.princeton.cs.algs4.StdRandom;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class RandomizedQueue<Item> implements Iterable<Item> {
    private Item[] items;
    private int size = 0;

    public RandomizedQueue() {
        items = (Item[]) new Object[1];
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public Item dequeue() {
        if (size == 0) throw new NoSuchElementException();

        int randomIndex = StdRandom.uniform(size);
        Item item = items[randomIndex];
        if (randomIndex == size - 1) {
            items[randomIndex] = null;
        } else {
            items[randomIndex] = items[size - 1];
            items[size - 1] = null;
        }

        if (size == items.length / 4) {
            resize(items.length / 2);
        }
        size--;
        return item;
    }

    public int size() {
        return size;
    }

    public void enqueue(Item item) {
        if (item == null) throw new NullPointerException();

        if (size == items.length) resize(2 * items.length);
        items[size++] = item;
    }

    public Item sample() {
        if (size == 0) throw new NoSuchElementException();
        return items[StdRandom.uniform(size)];
    }

    public Iterator<Item> iterator() {
        return new RandomQueueIterator();
    }

    private void resize(int capacity) {
        Item[] copy = (Item[]) new Object[capacity];
        for (int i = 0; i < size; i++)
            copy[i] = items[i];
        items = copy;
    }

    // Iterator for RandomizedQueue
    private class RandomQueueIterator implements Iterator<Item> {
        private int i = 0;
        private int[] indices;

        public RandomQueueIterator() {
            indices = new int[size];
            for (int j = 0; j < indices.length; j++) {
                indices[j] = j;
            }
            StdRandom.shuffle(indices);
        }

        public boolean hasNext()
        {
            return i < size;
        }

        public Item next() {
            if (!hasNext()) throw new java.util.NoSuchElementException("No more items in iteration.");
            return items[indices[i++]];
        }

        public void remove() {
            throw new UnsupportedOperationException("remove() is not supported");
        }
    }

    public static void main(String[] args) {
    }
}