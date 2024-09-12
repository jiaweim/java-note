package mjw.jgrapht;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;
import org.jgrapht.graph.SimpleWeightedGraph;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Jiawei Mao
 * @version 0.1.0
 * @since 20 Aug 2024, 5:14 PM
 */
public class CreateGraphTest {

    @Test
    public void createSimpleGraph() {
        Graph<String, DefaultEdge> graph = new SimpleGraph<>(DefaultEdge.class);
        graph.addVertex("A");
        graph.addVertex("B");
        graph.addEdge("A", "B");

        assertEquals(graph.toString(), "([A, B], [{A,B}])");
    }

    @Test
    void directedGraph() {
        DefaultDirectedGraph<String, DefaultEdge> graph = new DefaultDirectedGraph<>(DefaultEdge.class);
        graph.addVertex("v1");
        graph.addVertex("v2");
        graph.addVertex("v3");
        graph.addVertex("v4");
        graph.addVertex("v5");
        graph.addVertex("v6");
        graph.addVertex("v7");
        graph.addVertex("v8");
        graph.addVertex("v9");

        graph.addEdge("v1", "v2");
        graph.addEdge("v2", "v4");
        graph.addEdge("v4", "v3");
        graph.addEdge("v3", "v1");
        graph.addEdge("v5", "v4");
        graph.addEdge("v5", "v6");
        graph.addEdge("v6", "v7");
        graph.addEdge("v7", "v5");
        graph.addEdge("v8", "v5");
        graph.addEdge("v9", "v8");


        System.out.println(graph);
    }

    public static void createWeightedGraph() {
        SimpleWeightedGraph<String, DefaultEdge> graph = new SimpleWeightedGraph<>(DefaultEdge.class);
        graph.addVertex("X");
        graph.addVertex("Y");

    }
}
