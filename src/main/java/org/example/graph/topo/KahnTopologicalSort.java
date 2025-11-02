package org.example.graph.topo;
import org.example.graph.common.Edge;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public class KahnTopologicalSort {
    public static class Result {
        public final List<Integer> order;
        public final Metrics metrics;

        public Result(List<Integer> order, Metrics metrics) {
            this.order = order;
            this.metrics = metrics;
        }
    }

    public static class Metrics {
        public final int pushes;
        public final int pops;

        public Metrics(int pushes, int pops) {
            this.pushes = pushes;
            this.pops = pops;
        }
    }

    public static Result sort(int C, List<Edge> edges) {
        int[] indeg = new int[C];
        List<List<Integer>> adj = new ArrayList<>(C);
        for (int i = 0; i < C; i++) adj.add(new ArrayList<>());
        for (Edge e : edges) {
            adj.get(e.u).add(e.v);
            indeg[e.v]++;
        }
        Deque<Integer> q = new ArrayDeque<>();
        for (int i = 0; i < C; i++) if (indeg[i] == 0) q.add(i);
        int pushes = q.size();
        int pops = 0;
        List<Integer> order = new ArrayList<>();
        while (!q.isEmpty()) {
            int u = q.removeFirst();
            pops++;
            order.add(u);
            for (int v : adj.get(u)) {
                indeg[v]--;
                if (indeg[v] == 0) { q.addLast(v); pushes++; }
            }
        }
        return new Result(order, new Metrics(pushes, pops));
    }
}
