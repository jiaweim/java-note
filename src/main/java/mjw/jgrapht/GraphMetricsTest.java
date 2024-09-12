package mjw.jgrapht;

import org.jgrapht.Graph;
import org.jgrapht.GraphMetrics;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;
import org.junit.jupiter.api.Test;

/**
 * @author Jiawei Mao
 * @version 0.1.0
 * @since 21 Aug 2024, 11:09 AM
 */
public class GraphMetricsTest {

    @Test
    void diameter() {
        Graph<Integer, DefaultWeightedEdge> g = new SimpleDirectedWeightedGraph<>(DefaultWeightedEdge.class);
        Graphs.addEdgeWithVertices(g, 0, 1, 10);
        Graphs.addEdgeWithVertices(g, 1, 0, 12);
        double diameter = GraphMetrics.getDiameter(g);
        System.out.println(diameter);
    }
}
