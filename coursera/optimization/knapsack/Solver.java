import java.io.*;
import java.util.List;
import java.util.ArrayList;

/**
 * The class <code>Solver</code> is an implementation of a greedy algorithm to solve the knapsack problem.
 *
 */
public class Solver {

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
        List<String> lines = new ArrayList<String>();

        BufferedReader input =  new BufferedReader(new FileReader(fileName));
        try {
            String line = null;
            while (( line = input.readLine()) != null){
                lines.add(line);
            }
        }
        finally {
            input.close();
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

        value = dynamicProgramming(capacity, values, weights, taken);

        // prepare the solution in the specified output format
        System.out.println(value+" 1");
        for(int i=0; i < items; i++){
            System.out.print(taken[i]+" ");
        }
        System.out.println("");
    }

    private static int branchAndBound(int capacity, int[] values, int[] weights, boolean[] taken)
    {

    }

    private static int dynamicProgramming(int capacity, int[] values, int[] weights, boolean[] taken) {
        int[][] table = new int[capacity+1][values.length+1];

        int currentItem = 1;
        for (;currentItem < values.length+1;currentItem++) {
            for (int currentCapacity = 0; currentCapacity < capacity+1;currentCapacity++) {
                int notTakenVal = table[currentCapacity][currentItem-1];
                if (weights[currentItem-1] <= currentCapacity) {
                    int takenVal = values[currentItem-1] + table[currentCapacity-weights[currentItem-1]][currentItem-1];
                    if ( notTakenVal < takenVal) {
                        table[currentCapacity][currentItem] = takenVal;
                    }
                    else {
                        table[currentCapacity][currentItem] = notTakenVal;
                    }
                }
                else {
                    table[currentCapacity][currentItem] = notTakenVal;
                }
            }
        }

        int currentCapacity = capacity;
        for (int i=values.length;i>0;i--)
        {
            if (table[currentCapacity][i] > table[currentCapacity][i-1]) {
                taken[i-1] = true;
                currentCapacity -= weights[i-1];
            }
        }
        return table[capacity][(currentItem-1)];
    }
}