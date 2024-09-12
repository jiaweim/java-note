package mjw.guava;

import com.google.common.graph.GraphBuilder;
import com.google.common.graph.Graphs;
import com.google.common.graph.MutableGraph;
import org.junit.jupiter.api.Test;

/**
 *
 *
 * @author Jiawei Mao
 * @version 0.1.0
 * @since 21 Aug 2024, 5:54 PM
 */
public class DFSTest {
    @Test
    void test(){
        MutableGraph<Integer> graph = GraphBuilder.undirected().allowsSelfLoops(false).build();
        graph.addNode(0);
        graph.addNode(1);
        graph.addNode(2);
        graph.addNode(3);
        graph.addNode(4);
        graph.addNode(5);

        graph.putEdge(0,1);
        graph.putEdge(0,2);
        graph.putEdge(0,5);
        graph.putEdge(1,2);
        graph.putEdge(2,3);
        graph.putEdge(3,5);
        graph.putEdge(3,4);
        graph.putEdge(2,4);


    }
}
