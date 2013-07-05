package coloring;

import org.apache.commons.exec.*;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: patricio
 * Date: 7/3/13
 * Time: 9:24 PM
 * To change this template use File | Settings | File Templates.
 */
public class GraphColoringSolver
{
    public static void main(String[] args)
    {
        try {
            solve(args);
        } catch (Exception e) {
             e.printStackTrace();
        }
    }

    public static void solve(String[] args) throws IOException, InterruptedException
    {
        String fileName = null;
        String outDir = null;

        // get the temp file name
        for(String arg : args){
            if(arg.startsWith("-file=")){
                fileName = arg.substring(6);
            }
            else if(arg.startsWith("-out=")){
                outDir = arg.substring(5);
            }
        }
        if(fileName == null)
            return;
        if(outDir == null)
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
        int nodes = Integer.parseInt(firstLine[0]);
        int edges = Integer.parseInt(firstLine[1]);

        List<List<Integer>> adjacencyMatrix = new LinkedList<>();

        for (int i = 0; i < nodes; i++) {
            adjacencyMatrix.add(i, new LinkedList<Integer>());
        }

        for(int i=0; i < edges; i++){
            String line = lines.get(i+1);
            String[] parts = line.split("\\s+");

            int node1 = Integer.parseInt(parts[0]);
            int node2 = Integer.parseInt(parts[1]);

            adjacencyMatrix.get(node1).add(node2);
        }

        lines.clear();

        for (int i = 1; i < nodes; i++) {
            final String cnfFileName = new File(fileName).getName() + ".cnf";
            final File cnfFile = new File(outDir, cnfFileName);
            generateCNF(adjacencyMatrix, nodes, edges, i, cnfFile);
            final File result = new File("/tmp/result");
            if (runSAT(cnfFile, result)) {
                readSATResult(result, nodes, i, adjacencyMatrix);
                break;
            }
        }
    }

    private static boolean runSAT(final File cnfFile, final File result) throws IOException, InterruptedException
    {
        CommandLine cmdLine = new CommandLine("minisat");
        cmdLine.addArgument(cnfFile.toString());
        cmdLine.addArgument(result.toString());
        DefaultExecuteResultHandler resultHandler = new DefaultExecuteResultHandler();
        ExecuteWatchdog watchdog = new ExecuteWatchdog(1*60*1000);
        Executor executor = new DefaultExecutor();
        executor.setWatchdog(watchdog);
        executor.execute(cmdLine, resultHandler);
        resultHandler.waitFor();
        return resultHandler.getExitValue() == 10;

// some time later the result handler callback was invoked so we
// can safely request the exit value

        //run command
        // if satisfiable {
        //  readSATResult(result)
        //}
    }

    private static void generateCNF(final List<List<Integer>> adjacencyMatrix, final int nodes, final int edges, int maxColors, final File out)
    {
        try (BufferedWriter bf = new BufferedWriter(new FileWriter(out))) {
//        Writer bf = new OutputStreamWriter(System.err);
//        try {
            final int varNumber = nodes * maxColors;
            final int clauseNumber = nodes + maxColors * (maxColors - 1) / 2 + maxColors * edges;

            bf.write("p cnf " + varNumber + " " + clauseNumber + "\n");

            for (int i = 0; i < nodes; i++) {
                for (int j = 0; j < maxColors; j++) {
                    bf.write(getVariableIndex(i, j, nodes) + " ");
                }
                bf.write("0\n");
                for (int j = 0; j < maxColors; j++) {
                    for (int k = j+1; k < maxColors; k++) {
                        bf.write("-" + getVariableIndex(i, j, nodes) + " -" + getVariableIndex(i, k, nodes) + " 0\n");
                    }
                }
            }

            for (int i = 0; i < adjacencyMatrix.size(); i++) {
                List<Integer> node1 = adjacencyMatrix.get(i);
                for (int j = 0; j < node1.size(); j++) {
                    for (int k = 0;k < maxColors;k++) {
                        bf.write("-" + getVariableIndex(node1.get(j), k, nodes) + " -" + getVariableIndex(i, k, nodes) + " 0\n");
                    }
                }
            }

            bf.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static int getVariableIndex(final int node, final int color, final int totalNodes)
    {
        return totalNodes*color + node + 1;
    }

    private static int getNodeFromVariable(final int variable, final int totalNodes)
    {
        return (variable - 1) % totalNodes;
    }

    private static int getColorFromVariable(final int variable, final int totalNodes)
    {
        return (variable - 1) / totalNodes;
    }

    private static void readSATResult(final File file, final int nodes, final int color, final List<List<Integer>> adjacencyMatrix) throws IOException
    {
        List<String> lines = new ArrayList<>();

        try (BufferedReader input = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = input.readLine()) != null) {
                lines.add(line);
            }
        }

        List<Integer> nodesColor = Arrays.asList(new Integer[nodes]);
        // parse the data in the file
        String[] variables = lines.get(1).split("\\s+");
        for (int i = 0; i < variables.length-1; i++) {
            final int variable = Integer.valueOf(variables[i]);
            if (variable > 0) {
                final int node = getNodeFromVariable(variable, nodes);
                final int assignedColor = getColorFromVariable(variable, nodes);
                nodesColor.set(node, assignedColor);
            }
        }

        try (BufferedWriter bf = new BufferedWriter(new FileWriter("/tmp/coloring.output"))) {
            bf.write(color + " 1\n");
            for (Integer assignedColor : nodesColor) {
                bf.write(assignedColor + " ");
            }
            bf.write("\n");
            bf.flush();
        }

        try (BufferedWriter bf = new BufferedWriter(new FileWriter("/tmp/graph.dot"))) {
            bf.write("graph {\n");
            for (int i = 0; i < adjacencyMatrix.size(); i++) {
                List<Integer> node1 = adjacencyMatrix.get(i);
                for (int j = 0; j < node1.size(); j++) {
                    bf.write("\t" + i + " -- " + node1.get(j) + ";\n");
                }
            }
            for (int i = 0; i < nodesColor.size(); i++) {
                final String colorString = getColorString(nodesColor.get(i));
                bf.write("\t" + i + "[style=filled color=" + colorString + " label=" + nodesColor.get(i) + "];\n");
            }
            bf.write("}\n");
            bf.flush();
        }



    }

    private static String getColorString(final int color)
    {
        return allColors[color%allColors.length];
    }


    private static String[] allColors;

    static {
        allColors =
                new String[] {
                        "blueviolet", "deeppink4", "brown", "darkorchid1", "darkslategray1", "azure3", "chocolate1",
                        "cyan4", "cornsilk3", "darkorange2", "azure4", "brown1", "blanchedalmond", "antiquewhite",
                        "firebrick", "darkslategray", "firebrick1", "chartreuse", "cornsilk4", "dimgray",
                        "darkolivegreen", "darkorange4", "antiquewhite4", "cornsilk", "brown3", "darkorchid4",
                        "aquamarine4", "firebrick3", "dimgrey", "darkorchid3", "goldenrod", "darkgoldenrod3",
                        "darkturquoise", "cyan3", "darkviolet", "chartreuse4", "bisque4black", "blue", "bisque",
                        "gold3", "goldenrod4", "azure", "darkseagreen1", "deeppink1", "chartreuse1", "forestgreen",
                        "chartreuse3", "aliceblue", "cadetblue", "darkorange3", "dodgerblue", "chocolate",
                        "dodgerblue3", "dodgerblue4", "darkolivegreen1", "chocolate3", "goldenrod1", "darkseagreen",
                        "aquamarine", "deeppink3", "cyan", "gold", "aquamarine3", "goldenrod3", "burlywood1",
                        "firebrick4", "darkslategrey", "blue4", "darkolivegreen4", "blue3", "chocolate4",
                        "darkslategray4", "darksalmon", "crimson", "deepskyblue", "deepskyblue3", "bisque2",
                        "darkslategray3", "cornflowerblue", "bisque3", "floralwhite", "brown4", "coral3", "beige",
                        "darkgreen", "cadetblue4", "gainsboro", "darkkhaki", "darkolivegreen3", "darkgoldenrod",
                        "burlywood", "coral4", "burlywood3", "darkgoldenrod1", "darkslateblue", "darkorange",
                        "deepskyblue1", "darkseagreen3", "coral", "burlywood4", "darkorchid", "cadetblue3",
                        "deepskyblue4", "gold4", "deeppink", "darkgoldenrod4", "cadetblue1", "darkseagreen4"
                };
    }

}
