import com.google.common.graph.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

/*** Link to visual of the graph this creates: https://en.wikipedia.org/wiki/Depth-first_search ***/

public class Main {
    private static ArrayList<String> keys = new ArrayList<>();
    private static MutableGraph<String> DAG;

    public static void main(String[] args){
        readFile();
        generateDAG();

        MutableValueGraph<String, Double> g = buildGraph();

        System.out.printf("This is the BFS traversal: %s\n\n", bfs(g, "A"));
        System.out.printf("This is the DFS traversal: %s\n", dfs(g, "A"));

        System.out.printf("This is the map for Dijkstra's algorithm: %s\n\n", dijkstra(g, "A").entrySet().toString());

        System.out.printf("\nThis is the generated topological sort and answer to euler #79: %s", generateTopologicalSort(DAG));

    }

    private static MutableValueGraph<String, Double> buildGraph() {
        MutableValueGraph<String, Double> g = ValueGraphBuilder.undirected().build();

        g.putEdgeValue("A", "B", 1.0);
        g.putEdgeValue("A", "C", 2.0);
        g.putEdgeValue("A", "E", 3.0);

        g.putEdgeValue("B", "D", 4.0);
        g.putEdgeValue("B", "F", 5.0);

        g.putEdgeValue("C", "G", 6.0);

        g.putEdgeValue("E", "F", 7.0);
        return g;
    }

    private static void readFile() {
        File f = new File("keylog.txt");
        Scanner scan = null;
        try {
            scan = new Scanner(f);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        while(scan.hasNext()){
            keys.add(scan.nextLine());
        }
        scan.close();
    }

    private static <E> List<E> bfs(ValueGraph<E, Double> g, E start){
        List<E> ret = new ArrayList<>();
        Set<E>  visited = new HashSet<>();
        Queue<E> q = new LinkedList<>();

        visited.add(start);
        q.add(start);

        while(!q.isEmpty()) {
            E current = q.poll();
            for(E neighbor : g.adjacentNodes(current)) {
                if(!visited.contains(neighbor)) {
                    visited.add(neighbor);
                    q.add(neighbor);
                }
            }
            ret.add(current);
        }

        return ret;
    }

    private static <E> List<E> dfs(ValueGraph<E, Double> g, E start){
        List<E> ret = new ArrayList<>();
        Set<E>  visited = new HashSet<>();
        Stack<E> stack = new Stack<>();

        stack.add(start);

        while(!stack.isEmpty()){
            E current = stack.pop();

            if(!visited.contains(current)){
                ret.add(current);
                visited.add(current);
            }

            for(E neighbor : g.adjacentNodes(current)){
                if(!visited.contains(neighbor)){
                    stack.push(neighbor);
                }
            }
        }

        return ret;
    }

    private static <E> Map<E, Double> dijkstra(ValueGraph<E, Double> g, E start){
        Set<E> todo = new HashSet<>(bfs(g, start));
        Map<E, Double> distance = new HashMap<>();  // map node to its shortest distance

        distance.put(start, 0.0);
        todo.remove(start);

        for(E node : todo){
            if(g.hasEdgeConnecting(start, node)){
                distance.put(node, g.edgeValue(start, node).get());
            } else {
                distance.put(node, Double.MAX_VALUE);
            }

        }

        while(!todo.isEmpty()){
            E smallestNode = findSmallest(distance, todo);

            todo.remove(smallestNode);

            for(E v : g.adjacentNodes(smallestNode)){
                if(distance.get(smallestNode) + g.edgeValueOrDefault(v, smallestNode,0.0) < distance.get(v)){
                    distance.put(v, distance.get(smallestNode) + g.edgeValueOrDefault(v, smallestNode,0.0));
                }
            }
        }

        return distance;
    }



    private static <E> E findSmallest(Map<E, Double> distance, Set<E> todo){
        E smallestNode = null;
        for(E u : todo){
            if(smallestNode == null) {
                smallestNode = u;
            }
            else if(distance.get(u) < distance.getOrDefault(smallestNode, Double.MIN_VALUE)){
                smallestNode = u;
            }
        }
        return smallestNode;
    }

    private static <E> String generateTopologicalSort(MutableGraph<E> graph){
        Set<E> nodes = graph.nodes();
        if (nodes.isEmpty())
            return "";

        for(E node : nodes){
            if(graph.inDegree(node) == 0) {
                graph.removeNode(node);
                return node.toString() + generateTopologicalSort(graph);
            }
        }
        return "";
    }

    private static void generateDAG(){
        DAG = GraphBuilder.directed().allowsSelfLoops(false).build();

        for(String entry : keys) {
            DAG.putEdge(entry.substring(0, 1), entry.substring(1, 2)); // first to second digit
            DAG.putEdge(entry.substring(1, 2), entry.substring(2, 3)); // second to third
            DAG.putEdge(entry.substring(0, 1), entry.substring(2,3));  // first to third
        }
    }
}
