package org.example.graph.scc;
import org.example.graph.common.Edge;
import org.example.metrics.SCCMetrics;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class KosarajuSCC {

    public static Result computeSCC(int n, List<Edge> edges, SCCMetrics m) {
        m.reset();
        long start = System.nanoTime();

        List<List<Integer>> adj = new ArrayList<>(n);
        List<List<Integer>> tadj = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            adj.add(new ArrayList<>());
            tadj.add(new ArrayList<>());
        }
        for (Edge e : edges) {
            adj.get(e.u).add(e.v);
            tadj.get(e.v).add(e.u);
        }

        boolean[] visited = new boolean[n];
        List<Integer> order = new ArrayList<>();

        for (int i = 0; i < n; i++) {
            if (!visited[i]) dfs1(i, adj, visited, order, m);
        }

        int[] compOf = new int[n];
        for (int i = 0; i < n; i++) compOf[i] = -1;
        List<List<Integer>> comps = new ArrayList<>();
        int cid = 0;
        Collections.reverse(order);
        visited = new boolean[n];
        for (int v : order) {
            if (!visited[v]) {
                comps.add(new ArrayList<>());
                dfs2(v, tadj, visited, comps.get(cid), compOf, cid, m);
                cid++;
            }
        }

        m.timeNano = System.nanoTime() - start;
        return new Result(comps, compOf);
    }

    private static void dfs1(int u, List<List<Integer>> adj, boolean[] visited, List<Integer> order, SCCMetrics m) {
        visited[u] = true;
        m.dfsVisits++;
        for (int v : adj.get(u)) {
            m.dfsEdges++;
            if (!visited[v]) dfs1(v, adj, visited, order, m);
        }
        order.add(u);
    }

    private static void dfs2(int u, List<List<Integer>> tadj, boolean[] visited, List<Integer> comp, int[] compOf, int cid, SCCMetrics m) {
        visited[u] = true;
        m.dfsVisits++;
        comp.add(u);
        compOf[u] = cid;
        for (int v : tadj.get(u)) {
            m.dfsEdges++;
            if (!visited[v]) dfs2(v, tadj, visited, comp, compOf, cid, m);
        }
    }

    public record Result(List<List<Integer>> components, int[] compOf) {}
}
