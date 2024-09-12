package mjw.guava;

import com.google.common.collect.*;
import com.google.common.graph.*;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;

/**
 *
 *
 * @author Jiawei Mao
 * @version 0.1.0
 * @since 21 Aug 2024, 6:43 PM
 */
public class TraverserTest {
    /**
     * The undirected graph in the {@link Traverser#breadthFirst(Object)} javadoc:
     *
     * <pre>{@code
     * b ---- a ---- d
     * |      |
     * |      |
     * e ---- c ---- f
     * }</pre>
     */
    private static final SuccessorsFunction<Character> JAVADOC_GRAPH =
            createUndirectedGraph("ba", "ad", "be", "ac", "ec", "cf");

    private static SuccessorsFunction<Character> createUndirectedGraph(String... edges) {
        return createGraph(/* directed= */ false, edges);
    }

    /**
     * Creates a graph from a list of node pairs (encoded as strings, e.g. "ab" means that this graph
     * has an edge between 'a' and 'b').
     *
     * <p>The {@code successors} are always returned in alphabetical order.
     */
    private static SuccessorsFunction<Character> createGraph(boolean directed, String... edges) {
        ImmutableMultimap.Builder<Character, Character> graphMapBuilder = ImmutableMultimap.builder();
        for (String edge : edges) {
            checkArgument(
                    edge.length() == 2, "Expecting each edge to consist of 2 characters but got %s", edge);
            char node1 = edge.charAt(0);
            char node2 = edge.charAt(1);
            graphMapBuilder.put(node1, node2);
            if (!directed) {
                graphMapBuilder.put(node2, node1);
            }
        }
        final ImmutableMultimap<Character, Character> graphMap = graphMapBuilder.build();

        return new SuccessorsFunction<Character>() {
            @Override
            public Iterable<? extends Character> successors(Character node) {
                checkArgument(graphMap.containsKey(node) || graphMap.containsValue(node),
                        "Node %s is not an element of this graph", node);
                return Ordering.natural().immutableSortedCopy(graphMap.get(node));
            }
        };
    }


    @Test
    void testSimpleGraph() {
        ImmutableGraph.Builder<Integer> builder = GraphBuilder.undirected().allowsSelfLoops(false).immutable();
        builder.addNode(0);
        builder.addNode(1);
        builder.addNode(2);
        builder.addNode(3);
        builder.addNode(4);
        builder.addNode(5);
        builder.putEdge(0, 2);
        builder.putEdge(0, 1);
        builder.putEdge(0, 5);
        builder.putEdge(1, 2);
        builder.putEdge(2, 3);
        builder.putEdge(3, 5);
        builder.putEdge(2, 4);
        builder.putEdge(3, 4);

        // DFS 的顺序并不唯一，与 edge 顺序相关
        ImmutableGraph<Integer> graph = builder.build();
        Iterable<Integer> it = Traverser.forGraph(graph).depthFirstPreOrder(0);
        assertIterableEquals(it, Arrays.asList(0, 2, 1, 3, 5, 4));
    }
}
