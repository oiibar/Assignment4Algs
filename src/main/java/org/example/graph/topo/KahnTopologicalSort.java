package org.example.graph.topo;

import org.example.graph.common.Edge;
import org.example.metrics.KahnMetrics;
import java.util.*;

public class KahnTopologicalSort {

    public static class Result {
        public final List<Integer> order;
        public final KahnMetrics metrics;

        public Result(List<Integer> order, KahnMetrics metrics) {
            this.order = order;
            this.metrics = metrics;
        }
    }

    public static Result sort(int C, List<Edge> edges, KahnMetrics m) {
        m.reset();
        long start = System.nanoTime();

        int[] indeg = new int[C];
        List<List<Integer>> adj = new ArrayList<>(C);
        for (int i = 0; i < C; i++) adj.add(new ArrayList<>());

        for (Edge e : edges) {
            adj.get(e.u).add(e.v);
            indeg[e.v]++;
        }

        Deque<Integer> q = new ArrayDeque<>();
        for (int i = 0; i < C; i++) if (indeg[i] == 0) {
            q.add(i);
            m.pushes++;
        }

        List<Integer> order = new ArrayList<>();

        while (!q.isEmpty()) {
            int u = q.removeFirst();
            m.pops++;

            order.add(u);

            for (int v : adj.get(u)) {
                indeg[v]--;
                if (indeg[v] == 0) {
                    q.addLast(v);
                    m.pushes++;
                }
            }
        }

        m.timeNano = System.nanoTime() - start;
        return new Result(order, m);
    }
}
