package mjw.guava;

import com.google.common.graph.MutableValueGraph;
import com.google.common.graph.ValueGraphBuilder;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 *
 *
 * @author Jiawei Mao
 * @version 0.1.0
 * @since 21 Aug 2024, 5:03 PM
 */
public class ValueGraphTest {

    private static final String DEFAULT = "default";

    @Test
    void directedGraph() {
        MutableValueGraph<Integer, String> graph = ValueGraphBuilder.directed().allowsSelfLoops(true).build();
        graph.putEdgeValue(1, 2, "valueA");
        graph.putEdgeValue(2, 1, "valueB");
        graph.putEdgeValue(2, 3, "valueC");
        graph.putEdgeValue(4, 4, "valueD"); // self-loop

        assertEquals(graph.edgeValueOrDefault(1, 2, null), "valueA");
        assertEquals(graph.edgeValueOrDefault(2, 1, null), "valueB");
        assertEquals(graph.edgeValueOrDefault(2, 3, null), "valueC");
        assertEquals(graph.edgeValueOrDefault(4, 4, null), "valueD");

        assertEquals(graph.edgeValueOrDefault(1, 2, DEFAULT), "valueA");
        assertEquals(graph.edgeValueOrDefault(2, 1, DEFAULT), "valueB");
        assertEquals(graph.edgeValueOrDefault(2, 3, DEFAULT), "valueC");
        assertEquals(graph.edgeValueOrDefault(4, 4, DEFAULT), "valueD");

        String toString = graph.toString();
        assertTrue(toString.contains("valueA"));
        assertTrue(toString.contains("valueB"));
        assertTrue(toString.contains("valueC"));
        assertTrue(toString.contains("valueD"));
    }

    @Test
    void undirectedGraph() {
        MutableValueGraph<Integer, String> graph = ValueGraphBuilder.undirected().allowsSelfLoops(true).build();

        graph.putEdgeValue(1, 2, "valueA");
        graph.putEdgeValue(2, 1, "valueB"); // 无向图，覆盖 valueA
        graph.putEdgeValue(2, 3, "valueC");
        graph.putEdgeValue(4, 4, "valueD");

        assertEquals(graph.edgeValueOrDefault(1, 2, null), "valueB");
        assertEquals(graph.edgeValueOrDefault(2, 1, null), "valueB");
        assertEquals(graph.edgeValueOrDefault(2, 3, null), "valueC");
        assertEquals(graph.edgeValueOrDefault(4, 4, null), "valueD");

        assertEquals(graph.edgeValueOrDefault(1, 2, DEFAULT), "valueB");
        assertEquals(graph.edgeValueOrDefault(2, 1, DEFAULT), "valueB");
        assertEquals(graph.edgeValueOrDefault(2, 3, DEFAULT), "valueC");
        assertEquals(graph.edgeValueOrDefault(4, 4, DEFAULT), "valueD");

        String toString = graph.toString();
        assertFalse(toString.contains("valueA"));
        assertTrue(toString.contains("valueB"));
        assertTrue(toString.contains("valueC"));
        assertTrue(toString.contains("valueD"));


    }
}
