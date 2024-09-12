package mjw.guava;

import com.google.common.graph.GraphBuilder;
import com.google.common.graph.MutableGraph;
import com.google.common.graph.MutableValueGraph;
import com.google.common.graph.ValueGraphBuilder;
import org.junit.jupiter.api.Test;

/**
 *
 *
 * @author Jiawei Mao
 * @version 0.1.0
 * @since 21 Aug 2024, 3:43 PM
 */
public class CreateGraphTest {

    @Test
    void createGraph() {
        MutableGraph<Integer> graph = GraphBuilder.undirected().build();

    }

    @Test
    void valueGraph() {
        MutableValueGraph<Integer, Double> graph = ValueGraphBuilder.directed().build();
        graph.addNode(1);
        graph.putEdgeValue(2, 3, 1.5);

    }

}
