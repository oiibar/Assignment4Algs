package org.example.graph.scc;
import org.example.graph.common.Edge;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class KosarajuSCC {
    public static Result computeSCC(int n, List<Edge> edges) {
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
            if (!visited[i]) dfs1(i, adj, visited, order);
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
                dfs2(v, tadj, visited, comps.get(cid), compOf, cid);
                cid++;
            }
        }
        return new Result(comps, compOf);
    }

    private static void dfs1(int u, List<List<Integer>> adj, boolean[] visited, List<Integer> order) {
        visited[u] = true;
        for (int v : adj.get(u)) if (!visited[v]) dfs1(v, adj, visited, order);
        order.add(u);
    }

    private static void dfs2(int u, List<List<Integer>> tadj, boolean[] visited, List<Integer> comp, int[] compOf, int cid) {
        visited[u] = true;
        comp.add(u);
        compOf[u] = cid;
        for (int v : tadj.get(u)) if (!visited[v]) dfs2(v, tadj, visited, comp, compOf, cid);
    }

    public record Result(List<List<Integer>> components, int[] compOf) {}
}