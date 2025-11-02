package org.example.graph.topo;
import org.example.graph.common.Edge;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Condensation {
    public static CondensedGraph build(List<List<Integer>> comps, int[] compOf, List<Edge> edges) {
        int C = comps.size();
        Map<Integer, Map<Integer, Integer>> cmap = new HashMap<>();
        for (Edge e : edges) {
            int cu = compOf[e.u];
            int cv = compOf[e.v];
            if (cu == cv) continue;
            cmap.putIfAbsent(cu, new HashMap<>());
            Map<Integer, Integer> row = cmap.get(cu);
            row.put(cv, Math.min(row.getOrDefault(cv, Integer.MAX_VALUE), e.w));
        }
        List<List<Edge>> cadj = new ArrayList<>(C);
        List<Edge> cadges = new ArrayList<>();
        for (int i = 0; i < C; i++) cadj.add(new ArrayList<>());
        for (Map.Entry<Integer, Map<Integer, Integer>> e1 : cmap.entrySet()) {
            int u = e1.getKey();
            for (Map.Entry<Integer, Integer> e2 : e1.getValue().entrySet()) {
                int v = e2.getKey();
                int w = e2.getValue();
                Edge ce = new Edge(u, v, w);
                cadj.get(u).add(ce);
                cadges.add(ce);
            }
        }
        return new CondensedGraph(C, cadj, cadges);
    }

    public static class CondensedGraph {
        public final int C;
        public final List<List<Edge>> adj;
        public final List<Edge> edges;

        public CondensedGraph(int C, List<List<Edge>> adj, List<Edge> edges) {
            this.C = C;
            this.adj = adj;
            this.edges = edges;
        }
    }
}