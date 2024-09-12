package mjw.jgrapht;

import com.google.common.collect.Iterators;
import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.traverse.TopologicalOrderIterator;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Iterator;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

/**
 * @author Jiawei Mao
 * @version 0.1.0
 * @since 21 Aug 2024, 11:27 AM
 */
public class TopologicalOrderTest {
    @Test
    void testString() {
        Graph<String, DefaultEdge> graph = new DefaultDirectedGraph<>(DefaultEdge.class);

        Graphs.addAllVertices(graph, Arrays.asList("v0", "v1", "v2", "v3", "v4", "v5"));
        graph.addEdge("v0", "v1");
        graph.addEdge("v0", "v2");
        graph.addEdge("v1", "v4");
        graph.addEdge("v2", "v4");
        graph.addEdge("v3", "v2");
        graph.addEdge("v3", "v4");
        graph.addEdge("v4", "v5");

        Iterator<String> it = new TopologicalOrderIterator<>(graph);
        String[] array = Iterators.toArray(it, String.class);

        assertArrayEquals(array, new String[]{
                "v0", "v3", "v1", "v2", "v4", "v5"
        });
    }

    @Test
    void testInt() {
        Graph<Integer, DefaultEdge> graph = new DefaultDirectedGraph<>(DefaultEdge.class);
        Graphs.addAllVertices(graph, Arrays.asList(1, 2, 3, 4, 5));
        graph.addEdge(1, 3);
        graph.addEdge(1, 4);
        graph.addEdge(2, 1);
        graph.addEdge(2, 4);
        graph.addEdge(3, 4);
        graph.addEdge(5, 2);
        graph.addEdge(5, 3);

        Iterator<Integer> it = new TopologicalOrderIterator<>(graph);
        Integer[] array = Iterators.toArray(it, Integer.class);
        assertArrayEquals(array, new Integer[]{
                5, 2, 1, 3, 4
        });

    }
}
