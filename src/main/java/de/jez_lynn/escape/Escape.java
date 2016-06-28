package de.jez_lynn.escape;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.Scanner;

import com.sun.javafx.geom.Vec2d;

import de.jez_lynn.escape.helper.Reader;
import de.jez_lynn.escape.util.Edge;
import de.jez_lynn.escape.util.Node;

/**
 * Copyright 2016
 * Created on   : 24.06.2016
 * Author       : Michael Schlittenbauer
 */
public class Escape {
    //Arbitrary information for level files
    private int LEVEL = 0, DIFFICULTY = 0;

    //Uninitialized Edge to compare against
    private final Edge emptyEdge = new Edge(-842150451, -842150451, -842150451);

    //Constants for given auxillary data
    private final int MAXSOLDIERS = 1000;
    private final int MAXVISION = 100;
    private final int INFINITY = 1000000;

    //lenght width and number of soldiers will be given by files
    private int lenght, width, soldiers, source, sink;

    private Node[] nodes = new Node[2 * MAXSOLDIERS + 2];
    private LinkedList<Edge> edges = new LinkedList<>();
    private int[] xsoldier = new int[MAXSOLDIERS + 1];
    private int[] ysoldier = new int[MAXSOLDIERS + 1];

    /**
     * Constructor for class
     *
     * @param level number between 1 and 10
     * @param diff  number between 0 and 4
     */
    public Escape(int level, int diff) {
        LEVEL = level;
        DIFFICULTY = diff;
    }

    /**
     * Main method which will handle all the escaping stuff
     */
    private void escape() {
        //First initialize our node array with newly empty nodes
        for (int i = 0; i < nodes.length; i++) {
            nodes[i] = new Node();
        }
        //Read the input file
        input();
        //Init the flow graph
        initGraph();
        //search fpr paths through the network und augment paths
        while (bfs()) adjustFlows();
        //read the correct solution file (just for displaying)
        readSolution();
        //create the output
        output();
    }

    /**
     * This will handle the file input to get soldier positions, number of soldiers, the playground with and length
     */
    private void input() {
        String path = "/escape.i%d-%d";
        Reader r = new Reader(String.format(path, LEVEL, DIFFICULTY));
        lenght = r.getLenght();
        width = r.getWidth();
        soldiers = r.getSoldiers();

        for (int i = 1; i <= soldiers; i++) {
            Vec2d pos = r.getPosition(i);
            xsoldier[i] = (int) pos.x;
            ysoldier[i] = (int) pos.y;
        }

        source = 0;
        sink = 2 * soldiers + 1;
    }

    /**
     * This will init our flow graph and will be run only once
     */
    private void initGraph() {
        //As long as there a soldiers left
        for (int i = 1; i <= soldiers; i++) {
            //add an Edge for this soldier to the flow graph
            addEdge(i, i + soldiers, 1); //i-in to i-out
            //check if this soldier can see the sink
            if (width - ysoldier[i] <= MAXVISION)
                addEdge(i + soldiers, sink, INFINITY); //i-out to sink
            //check if this soldier can see the source
            if (ysoldier[i] <= MAXVISION)
                addEdge(source, i, INFINITY); //source to i-in
            //and check for every other solder if this soldier can see one of the other soldiers
            for (int j = i + 1; j <= soldiers; j++) {
                long dx = xsoldier[i] - xsoldier[j];
                long dy = ysoldier[i] - ysoldier[j];
                if (dx * dx + dy * dy <= (MAXVISION + MAXVISION) * (MAXVISION + MAXVISION)) {
                    addEdge(i + soldiers, j, INFINITY); //i-out to j-in
                    addEdge(j + soldiers, i, INFINITY); //j-out to i-in
                }
            }
        }
    }

    /**
     * Auxiliary method to add an Edge to the flow graph
     *
     * @param from     integer id from which node we start
     * @param to       integer id to which node we flow
     * @param capacity the capacity of the newly created Edge
     */
    private void addEdge(int from, int to, int capacity) {
        edges.addFirst(new Edge(from, to, capacity));
        nodes[from].edges.addLast(edges.getFirst());
        nodes[to].edges.addLast(edges.getFirst());
    }

    /**
     * A breath first search variant to find paths throw the flow network
     *
     * @return either true if there was a new path or false if there were no such path
     */
    private boolean bfs() {
        //Fist of all init all last Edges of Edges as empty
        for (int i = 0; i <= 2 * soldiers + 1; i++)
            nodes[i].lastEdge = emptyEdge;

        //Create a queue of Integers id's to search for
        LinkedList<Integer> queue = new LinkedList<>();

        //Start with the source
        queue.addLast(source);

        //As long as we have id's in the queue
        while (!queue.isEmpty()) {
            //Get the first id as actual current id
            int akt = queue.pop();
            //Check if whe reached the sink if so return true we found a path from source to sink
            if (akt == sink)
                return true;
            //Otherwise check all connected Edges to the current node
            for (Edge e : nodes[akt].edges) {
                int to = e.to;
                if (to == akt) to = e.from;
                //Check if the node we are going to has other edges to go to and it is not our source (we want go back there)
                if (!nodes[to].lastEdge.equals(emptyEdge) || to == source) continue;
                //if we can go to e and have capacity left or we can come from e and have flow left add e to the queue
                if ((to == e.to && e.flow < e.capacity) || (to == e.from && e.flow > 0)) {
                    nodes[to].lastEdge = e;
                    queue.addLast(to);
                }

            }
        }
        return false;
    }

    /**
     * Another auxiliary method to adjust flows in the network if we found a path in the network
     */
    private void adjustFlows() {
        //Start at the sink
        int akt = sink;
        //Go back the path we found
        while (!nodes[akt].lastEdge.equals(emptyEdge)) {
            Edge e = nodes[akt].lastEdge;
            //and adjust the flow of the edges accordingly
            if (akt == e.to) {
                e.flow++;
                akt = e.from;
            } else {
                e.flow--;
                akt = e.to;
            }
        }
    }

    private void output() {
        int soldiersToKill = 0;
        for (Edge e : nodes[0].edges) {
            soldiersToKill += e.flow;
        }
        System.out.println("Du hast " + soldiersToKill + " Soldaten getöt");
    }

    private void readSolution() {
        String path = String.format("/escape.o%d-%d", LEVEL, DIFFICULTY);
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(Escape.class.getResourceAsStream(path)));
            String line;
            while ((line = br.readLine()) != null && !line.isEmpty()) {
                System.out.println("Es müsste/n " + line + " Soldate/n getöt werden");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (br != null)
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
    }

    public static void main(String[] args) throws IOException {
        //Check the working directory (just for debugging)
        System.out.println("Working Directory = " +
                System.getProperty("user.dir"));

        //Show welcome message
        System.out.println("Willkommen bei Escape");

        //Read in a level number
        Scanner sc = new Scanner(System.in);
        int level;
        do {
            System.out.println("Bitte wähle einen Level (1-10)");
            while (!sc.hasNextInt()) {
                System.out.println("Das ist keine Zahl");
                sc.next();
            }
            level = sc.nextInt();
        } while (level <= 0 || level > 10);

        //Read in a difficulty number
        int diff;
        do {
            System.out.println("Bitte wähle einen Schwierigkeitsgrad (0-4)");
            while (!sc.hasNextInt()) {
                System.out.println("Das ist keine Zahl");
                sc.next();
            }
            diff = sc.nextInt();
        } while (diff < 0 || diff > 4);
        sc.close();

        //Start the escape
        Escape prisoner = new Escape(level, diff);
        prisoner.escape();
    }
}
