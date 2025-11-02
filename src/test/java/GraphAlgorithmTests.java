import org.example.graph.common.Edge;
import org.example.graph.scc.KosarajuSCC;
import org.example.graph.topo.Condensation;
import org.example.graph.topo.KahnTopologicalSort;
import org.example.graph.dagsp.DagEdgeWeighted;
import org.example.metrics.DAGSPMetrics;
import org.example.metrics.KahnMetrics;
import org.example.metrics.SCCMetrics;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;

public class GraphAlgorithmTests {

    @Test
    public void testSCCSimple() {
        int n = 3;
        List<Edge> edges = List.of(
                new Edge(0,1,1),
                new Edge(1,2,1),
                new Edge(2,0,1)
        );
        SCCMetrics sm = new SCCMetrics();
        var res = KosarajuSCC.computeSCC(n, edges, sm);
        assertEquals(1, res.components().size());
    }

    @Test
    public void testSCCMultiple() {
        int n = 4;
        List<Edge> edges = List.of(
                new Edge(0,1,1),
                new Edge(1,0,1),
                new Edge(2,3,1)
        );
        SCCMetrics sm = new SCCMetrics();
        var res = KosarajuSCC.computeSCC(n, edges, sm);
        assertEquals(3, res.components().size());
    }

    @Test
    public void testCondensation() {
        int n = 4;
        List<Edge> edges = List.of(
                new Edge(0,1,1),
                new Edge(1,0,1),
                new Edge(2,3,1)
        );
        SCCMetrics sm = new SCCMetrics();
        var res = KosarajuSCC.computeSCC(n, edges, sm);
        var cg = Condensation.build(res.components(), res.compOf(), edges);
        for(int cu=0; cu<cg.C; cu++){
            for(Edge e: cg.adj.get(cu)){
                assertNotEquals(cu, e.v);
            }
        }
    }

    @Test
    public void testKahnTopo() {
        int n = 4;
        List<Edge> edges = List.of(
                new Edge(0,1,1),
                new Edge(0,2,1),
                new Edge(2,3,1)
        );
        KahnMetrics km = new KahnMetrics();
        var res = KahnTopologicalSort.sort(n, edges, km);
        assertEquals(4, res.order.size());
        int[] pos = new int[n];
        for(int i=0;i<n;i++) pos[res.order.get(i)] = i;
        for(Edge e: edges) {
            assertTrue(pos[e.u] < pos[e.v]);
        }
    }

    @Test
    public void testDAGShortestAndLongest() {
        int n = 4;
        List<Edge> edges = List.of(
                new Edge(0,1,2),
                new Edge(1,2,3),
                new Edge(0,3,10)
        );
        DAGSPMetrics m = new DAGSPMetrics();
        List<List<Edge>> adj = new ArrayList<>();
        for(int i=0;i<n;i++) adj.add(new ArrayList<>());
        for(Edge e: edges) adj.get(e.u).add(e);

        var sp = DagEdgeWeighted.shortest(n, adj, 0, m);
        assertEquals(0, sp.dist[0]);
        assertEquals(2, sp.dist[1]);
        assertEquals(5, sp.dist[2]);
        assertEquals(10, sp.dist[3]);

        var lp = DagEdgeWeighted.longest(n, adj, m);
        assertEquals(10, lp.maxValue);
    }
}
