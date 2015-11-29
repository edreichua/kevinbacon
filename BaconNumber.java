package cs10;

import java.io.*;
import java.util.*;

/**
 * COSC 10 Spring 15
 * Problem Set 4: Kevin Bacon Number
 *
 * Created by edreichua on 5/6/15.
 */
public class BaconNumber {

    private static Graph<String,String> undir; // The undirected graph
    private static Graph<String,String> dir; // The directed graph

    /**
     * Search Graph
     *
     * Search the directed graph for the actor name and transverse the graph to print the bacon number and the edges
     *
     * @param actor the name of the actor
     */
    private static void SearchGraph(String actor){

        if(dir.hasVertex(actor)) { // If the actor is related at all to Kevin Bacon
            String curr = actor;
            int i = 0; // The kevin bacon number initialised to zero
            Deque toprint = new ArrayDeque<>(); // Queue of actors' name (vertices) and movie title (edge labels) to be printed
            while (dir.outDegree(curr) > 0) { // Keep transversing until we reach Kevin Bacon node
                String nextactor = null;
                for (String next : dir.outNeighbors(curr)) nextactor = next;
                // add the actors' name and movie title to the queue
                toprint.add(curr);
                toprint.add(dir.getLabel(curr, nextactor));
                toprint.add(nextactor);
                curr = nextactor; // go to the next node
                i++;
            }
            // Series of print statements
            System.out.println(actor + "'s Kevin Bacon number is " + i + ".");
            while (!toprint.isEmpty()) {
                System.out.println(toprint.remove() + " appeared in " + toprint.remove() + " with " + toprint.remove() + ".");
            }
        }else if(undir.hasVertex(actor)){ // The actor is not related at all to Kevin Bacon
            System.out.println(actor + "'s Kevin Bacon number is infinite.");

        }else {  // The actor is not in the database
            System.out.println(actor + " is not in the database.");
        }
    }

    /**
     * CreateDirGraph
     *
     * Create a directed graph (dir) from the undirected graph (undir) using a Breadth-first search
     */
    private static void CreateDirGraph(){

        dir = new AdjacencyMapGraph<>();
        dir.insertVertex("Kevin Bacon"); // Kevin Bacon is the root node
        Deque<String> q = new ArrayDeque<>();
        q.add("Kevin Bacon");

        // Breadth-first search
        while(!q.isEmpty()){
            String v = q.remove();
            for(String entry: undir.outNeighbors(v)){
                if(!dir.hasVertex(entry)){
                    // add vertices to graph and queue for neighbours of v if they are not already in the graph
                    String e = undir.getLabel(v,entry);
                    dir.insertVertex(entry);
                    dir.insertDirected(entry,v,e);
                    q.add(entry);
                }
            }
        }
       //    System.out.println(dir.toString()); // Uncomment to print the graph
    }

    /**
     * CreateUnGraph
     *
     * Create an undirected graph (undir) from the three files with the vertices as actors and edges as movies
     *
     * @param file1 actor id -> actor name
     * @param file2 movie id -> movie name
     * @param file3 movie id -> actor id
     */

    private static void CreateUnDirGraph(String file1, String file2, String file3) {

        undir = new AdjacencyMapGraph<>();

        // Create the maps from the 3 files
        Map<String, String> map1 = CreateMap(file1);
        Map<String, String> map2 = CreateMap(file2);
        Map<String, List<String>> map3 = CreateA2M(file3);

        // Insert the names of all the actors as vertices
        for (String actorid : map1.keySet())
            undir.insertVertex(map1.get(actorid));

        // Construct undirected graph
        for (String movieid : map3.keySet()) {
            String moviename = map2.get(movieid);
            List<String> edges = map3.get(movieid); // list of actors who appear in the same movie
            while (!edges.isEmpty()) { // Construct vertices and edges among all actors in the same movie
                String actor1 = map1.get(edges.remove(edges.size() - 1));
                for (String entry : edges) {
                    String actor2 = map1.get(entry);
                    undir.insertUndirected(actor1, actor2, moviename);
                }
            }
        }
    }

    /**
     * CreateUnDirGraphTest
     *
     * Test case in the hand-coded example for creating undirected graph
     *
     */
    private static void CreateUnDirGraphTest() {

        undir = new AdjacencyMapGraph<>();

        undir.insertVertex("Kevin Bacon");
        undir.insertVertex("actor1");
        undir.insertVertex("actor2");
        undir.insertVertex("actor3");
        undir.insertVertex("actor4");
        undir.insertVertex("actor5");
        undir.insertVertex("actor6");

        undir.insertUndirected("Kevin Bacon", "actor1", "movie1");
        undir.insertUndirected("Kevin Bacon","actor2","movie1");
        undir.insertUndirected("actor1","actor2","movie1");
        undir.insertUndirected("actor1","actor3","movie2");
        undir.insertUndirected("actor3","actor2","movie3");
        undir.insertUndirected("actor3","actor4","movie4");
        undir.insertUndirected("actor5","actor6","movie5");
    }

    /**
     * CreateMap
     *
     * Read the file and create a map. To be used with actor id -> name file and movie id -> title file
     *
     * @param filename filename
     * @return Map String -> String
     */
    private static Map<String, String> CreateMap(String filename) {
        BufferedReader input = null;
        Map<String, String> result = new TreeMap<>();
        try {
            input = new BufferedReader(new FileReader(filename)); // Open file
            String pair;
            while ((pair = input.readLine()) != null) { // Read file and add strings to map
                String[] parts = pair.split("\\|");
                result.put(parts[0], parts[1]);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Close file if file exist. If not, catch the exception
            try {
                input.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return (result);
    }

    /**
     * Create2AM
     *
     * Read the file and create a map. To be used with movies id -> actors id file
     *
     * @param filename
     * @return Map String -> List<String> with movie title -> list of actors
     */
    private static Map<String, List<String>> CreateA2M(String filename) {
        BufferedReader input = null;
        Map<String, List<String>> result = new TreeMap<>();
        try {
            input = new BufferedReader(new FileReader(filename)); // Open file
            String pair;
            while ((pair = input.readLine()) != null) { // Read file and add string and list of strings to map
                String[] parts = pair.split("\\|");
                if (result.containsKey(parts[0])) { // if item exist, update the list of actors
                    result.get(parts[0]).add(parts[1]);
                } else { // Add new item to map
                    List<String> newlist = new ArrayList<>();
                    newlist.add(parts[1]);
                    result.put(parts[0], newlist);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Close file if file exist. If not, catch the exception
            try {
                input.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return (result);
    }

    public static void main(String[] args) {
        // List of filenames
        String filename1 = "inputs/actors.txt";
        String filename2 = "inputs/movies.txt";
        String filename3 = "inputs/movie-actors.txt";
        boolean flag = true; // to be used in the while loop
        Scanner scan = new Scanner(System.in);

        // Create the graphs
        CreateUnDirGraph(filename1, filename2, filename3);
        //CreateUnDirGraphTest(); // Comment out for hand-coded test case
        CreateDirGraph();

        System.out.println("The Kevin Bacon Game");
        System.out.println("To quit the program, type return in answer to a question.\n");

        // Prompt user to key in name of an actor repeatedly until user hit return
       while(flag){
           System.out.print("Enter the name of an actor: ");
           String actor = scan.nextLine();
           if(actor.isEmpty()){
               flag = !flag;
               System.out.println("Exit game. Have a great day!");
           }else{
               SearchGraph(actor); // Search the graph
               System.out.println();
           }
       }
    }
}