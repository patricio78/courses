import choco.cp.model.CPModel;
import choco.cp.solver.CPSolver;
import choco.kernel.model.Model;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.solver.Solver;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

import static choco.Choco.*;

/**
 * The class <code>KnapsackSolver</code> is an implementation of a greedy algorithm to solve the knapsack problem.
 *
 */
public class KnapsackSolver {

    /**
     * The main class
     */
    public static void main(String[] args) {
        try {
            solve(args);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Read the instance, solve it, and print the solution in the standard output
     */
    public static void solve(String[] args) throws IOException {
        String fileName = null;

        // get the temp file name
        for(String arg : args){
            if(arg.startsWith("-file=")){
                fileName = arg.substring(6);
            }
        }
        if(fileName == null)
            return;

        // read the lines out of the file
        List<String> lines = new ArrayList<>();

        try (BufferedReader input = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = input.readLine()) != null) {
                lines.add(line);
            }
        }


        // parse the data in the file
        String[] firstLine = lines.get(0).split("\\s+");
        int items = Integer.parseInt(firstLine[0]);
        int capacity = Integer.parseInt(firstLine[1]);

        int[] values = new int[items];
        int[] weights = new int[items];

        for(int i=1; i < items+1; i++){
            String line = lines.get(i);
            String[] parts = line.split("\\s+");

            values[i-1] = Integer.parseInt(parts[0]);
            weights[i-1] = Integer.parseInt(parts[1]);
        }

        lines.clear();

        // a trivial greedy algorithm for filling the knapsack
        // it takes items in-order until the knapsack is full
        int value = 0;
        boolean[] taken = new boolean[items];

//        value = greedy(capacity, values, weights, taken);
        value = branchAndBound(capacity, values, weights, taken);
//        value = dynamicProgramming(capacity, values, weights, taken);
//        value = cp(capacity, values, weights, taken);

        // prepare the solution in the specified output format
        System.out.println(value+" 1");
        for(int i=0; i < items; i++){
            System.out.print((taken[i] ? "1" : "0" )+" ");
        }
        System.out.println("");
    }

    private static int cp(int capacity, int[] values, int[] weights, boolean[] taken)
    {
        Item[] weightedItems = new Item[values.length];
        for (int i = 0; i < weightedItems.length; i++) {
            weightedItems[i] = new Item(i, values[i]/(float)weights[i]);
        }
        Arrays.sort(weightedItems, new Comparator<Item>() {
            @Override
            public int compare(Item o1, Item o2) {
                return o1.weight < o2.weight ? 1 : -1;
            }
        });

        float[] bounds = new float[values.length];
        for (int i = 0; i < bounds.length; i++) {
            bounds[i] = bound(capacity, values, weights, weightedItems, i);
        }

        float upperBound = bound(capacity, values, weights, weightedItems, -1);

        Model model              = new CPModel();
        Solver solver            = new CPSolver();
        IntegerVariable[] select = makeIntVarArray("select", values.length,0 , 1);
        IntegerVariable totVal   = makeIntVar("totVal", 0, (int) Math.ceil(upperBound));
        model.addConstraint(leq(scalar(weights, select),capacity));
        model.addConstraint(eq(scalar(values, select),totVal));
        solver.read(model);
        solver.maximize(solver.getVar(totVal), true);
        for (int i = 0; i < select.length; i++) {
            taken[i] = solver.getVar(select[i]).getVal() == 1;

        }
        return solver.getVar(totVal).getVal();
    }

    private static int greedy(int capacity, int[] values, int[] weights, boolean[] taken)
    {
        Item[] weightedItems = new Item[values.length];
        for (int i = 0; i < weightedItems.length; i++) {
            weightedItems[i] = new Item(i, values[i]/(float)weights[i]);
        }
        Arrays.sort(weightedItems, new Comparator<Item>() {
            @Override
            public int compare(Item o1, Item o2) {
                return o1.weight < o2.weight ? 1 : -1;
            }
        });


        int weight = 0;
        int value = 0;
        for(int i=0; i < values.length; i++){
            if(weight + weights[weightedItems[i].index] <= capacity){
                taken[weightedItems[i].index] = true;
                value += values[weightedItems[i].index];
                weight += weights[weightedItems[i].index];
            } else {
//                taken[weightedItems[i].index] = false;
                break;
            }
        }
        return value;
    }

    private static int branchAndBound(int capacity, int[] values, int[] weights, boolean[] taken)
    {
        Item[] weightedItems = new Item[values.length];
        for (int i = 0; i < weightedItems.length; i++) {
            weightedItems[i] = new Item(i, values[i]/(float)weights[i]);
        }
        Arrays.sort(weightedItems, new Comparator<Item>() {
            @Override
            public int compare(Item o1, Item o2) {
                return o1.weight < o2.weight ? 1 : -1;
            }
        });

        float[] bounds = new float[values.length];
        for (int i = 0; i < bounds.length; i++) {
            bounds[i] = bound(capacity, values, weights, weightedItems, i);
        }

        float upperBound = bound(capacity, values, weights, weightedItems, -1);

        final Queue<Node> queue = new PriorityQueue<>();
        final Node root = new Node();
        root.level = -1;
        root.size = 0;
        root.value = 0;
        root.bound = upperBound;
        queue.add(root);

        int maxValue = 0;
        List<Integer> bestList = new ArrayList<>();
        while (!queue.isEmpty()) {
            final Node currentNode = queue.remove();
            int value;

            if (currentNode.bound > maxValue && currentNode.level < values.length-1) {
                Node w = new Node();
                w.level = currentNode.level + 1;
                w.size = currentNode.size;
                w.value = currentNode.value;
                w.copyList(currentNode.contains);
                w.bound = bounds[weightedItems[w.level].index];
                if (w.bound > maxValue && w.size <= capacity)
                {
                    queue.add(w);
                }
                else if (w.level >= values.length-1 && w.size <= capacity && w.value > maxValue) {
                    maxValue = w.value;
                }

                Node u = new Node();
                u.level = currentNode.level + 1;
                u.size = currentNode.size + weights[weightedItems[u.level].index];
                u.copyList(currentNode.contains);
                u.add(weightedItems[u.level].index);
                value = currentNode.value + values[weightedItems[u.level].index];
                if (u.size <= capacity && value > maxValue)  {
                    bestList.clear();
                    bestList.addAll(u.contains);
                }
                u.bound = currentNode.bound;
                if (u.bound > maxValue && u.size <= capacity) {
                    queue.add(u);
                    u.value = value;
                }
                else if (u.level >= values.length-1 && currentNode.size <= capacity) {
                    if (currentNode.value > maxValue) {
                        maxValue = currentNode.value;
                    }
                }

            }
            else if (currentNode.value > maxValue) {
                maxValue = currentNode.value;
            }
        }

        for (Integer index : bestList) {
            taken[index] = true;
        }
        return maxValue;
    }

    private static float bound(int capacity, int[] values, int[] weights, Item[] weightedItems, int itemToExclude)
    {
        int currentCapacity = 0;
        int item = 0;
        float bound = 0;
        while (item < weightedItems.length) {
            final boolean skipItem = itemToExclude == weightedItems[item].index;
            if (!skipItem && currentCapacity + weights[weightedItems[item].index] <= capacity) {
                currentCapacity += weights[weightedItems[item].index];
                bound += values[weightedItems[item].index];
            }
            else if (!skipItem) {
                break;
            }
            item++;
        }
        if (item < weightedItems.length && currentCapacity < capacity)
        {
            bound +=  (capacity - currentCapacity) * (values[weightedItems[item].index]/ (float) weights[weightedItems[item].index]);
        }
        return bound;
    }

    private static class Item
    {
        private int index;
        private float weight;

        private Item(int index, float weight) {
            this.index = index;
            this.weight = weight;
        }
    }

    private static class Node
        implements Comparable<Node>
    {
        private int value;
        private int size;
        private float bound;
        private List<Integer> contains = new ArrayList<>(1000);
        private int level;

        public void add(final int index)
        {
            contains.add(index);
        }

        public void copyList(final List<Integer> indexes)
        {
            contains.clear();
            if (indexes != null && !indexes.isEmpty())
            {
                contains.addAll(indexes);
            }
        }

        @Override
        public int compareTo(Node n) {
            return this.bound > n.bound ? -1 : 1;
        }
    }

    private static int dynamicProgramming(int capacity, int[] values, int[] weights, boolean[] taken) {
//        int[][] table = new int[values.length+1][capacity+1];
        List<Integer>[] table = new List[values.length+1];

        table[0] = new LinkedList<>();
        for (int i = 0; i < capacity+1; i++) {
            table[0].add(0);
        }

        int currentItem = 1;
        for (;currentItem < values.length+1;currentItem++) {
            table[currentItem] = new LinkedList<>();
            for (int currentCapacity = 0; currentCapacity < capacity+1;currentCapacity++) {
                int notTakenVal = table[currentItem-1].get(currentCapacity);
                if (weights[currentItem-1] <= currentCapacity) {
                    int takenVal = values[currentItem-1] + table[currentItem-1].get(currentCapacity-weights[currentItem-1]);
                    if ( notTakenVal < takenVal) {
                        table[currentItem].add(currentCapacity, takenVal);
                    }
                    else {
                        table[currentItem].add(currentCapacity, notTakenVal);
                    }
                }
                else {
                    table[currentItem].add(currentCapacity, notTakenVal);
                }
            }
        }

        int currentCapacity = capacity;
        for (int i=values.length;i>0;i--)
        {
            if (table[i].get(currentCapacity) > table[i-1].get(currentCapacity)) {
                taken[i-1] = true;
                currentCapacity -= weights[i-1];
            }
        }
        return table[currentItem-1].get(capacity);
    }

    private static class Key
    {
        private int i,j;

        private Key(int i, int j) {
            this.i = i;
            this.j = j;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Key key = (Key) o;

            if (i != key.i) return false;
            return j == key.j;

        }

        @Override
        public int hashCode() {
            int result = i;
            result = 31 * result + j;
            return result;
        }
    }
}
