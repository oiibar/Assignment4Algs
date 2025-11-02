package org.example.graph.dagsp;
import org.example.graph.common.Edge;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DagEdgeWeighted {
    public static class ShortestResult {
        public final double[] dist;
        public final int[] prev;
        public final List<Integer> topoOrder;

        public ShortestResult(double[] dist, int[] prev, List<Integer> topoOrder) {
            this.dist = dist; this.prev = prev; this.topoOrder = topoOrder;
        }
    }

    public static class LongestResult {
        public final double[] dist;
        public final int[] prev;
        public final int maxIndex;
        public final double maxValue;
        public final List<Integer> topoOrder;

        public LongestResult(double[] dist,int[] prev,int maxIndex,double maxValue,List<Integer> topoOrder) {
            this.dist=dist; this.prev=prev; this.maxIndex=maxIndex; this.maxValue=maxValue; this.topoOrder=topoOrder;
        }
    }

    private static List<Integer> topoOrder(int C, List<List<Edge>> adj) {
        int[] indeg = new int[C];
        for (int u = 0; u < C; u++) for (Edge e : adj.get(u)) indeg[e.v]++;
        List<Integer> order = new ArrayList<>();
        ArrayList<Integer> q = new ArrayList<>();
        for (int i = 0; i < C; i++) if (indeg[i] == 0) q.add(i);
        int idx = 0;
        while (idx < q.size()) {
            int u = q.get(idx++);
            order.add(u);
            for (Edge e : adj.get(u)) {
                indeg[e.v]--;
                if (indeg[e.v] == 0) q.add(e.v);
            }
        }
        return order;
    }

    public static ShortestResult shortest(int C, List<List<Edge>> adj, int source) {
        List<Integer> topo = topoOrder(C, adj);
        double[] dist = new double[C];
        int[] prev = new int[C];
        for (int i = 0; i < C; i++) { dist[i] = Double.POSITIVE_INFINITY; prev[i] = -1; }
        dist[source] = 0.0;
        for (int u : topo) {
            if (Double.isInfinite(dist[u])) continue;
            for (Edge e : adj.get(u)) {
                int v = e.v; double w = e.w;
                if (dist[v] > dist[u] + w) {
                    dist[v] = dist[u] + w;
                    prev[v] = u;
                }
            }
        }
        return new ShortestResult(dist, prev, topo);
    }

    public static LongestResult longest(int C, List<List<Edge>> adj) {
        List<Integer> topo = topoOrder(C, adj);
        double[] dist = new double[C];
        int[] prev = new int[C];
        for (int i = 0; i < C; i++) { dist[i] = Double.NEGATIVE_INFINITY; prev[i] = -1; }
        boolean[] hasIncoming = new boolean[C];
        for (int u = 0; u < C; u++) for (Edge e : adj.get(u)) hasIncoming[e.v] = true;
        for (int i = 0; i < C; i++) if (!hasIncoming[i]) dist[i] = 0.0;

        for (int u : topo) {
            if (Double.isInfinite(dist[u]) && dist[u] < 0) continue; // unreachable
            for (Edge e : adj.get(u)) {
                int v = e.v; double w = e.w;
                if (dist[v] < dist[u] + w) {
                    dist[v] = dist[u] + w;
                    prev[v] = u;
                }
            }
        }
        double max = Double.NEGATIVE_INFINITY; int midx = -1;
        for (int i = 0; i < C; i++) if (dist[i] > max) { max = dist[i]; midx = i; }
        return new LongestResult(dist, prev, midx, max, topo);
    }

    public static List<Integer> reconstructPath(int[] prev, int target) {
        if (target < 0) return Collections.emptyList();
        List<Integer> path = new ArrayList<>();
        int cur = target;
        while (cur != -1) { path.add(cur); cur = prev[cur]; }
        Collections.reverse(path);
        return path;
    }
}