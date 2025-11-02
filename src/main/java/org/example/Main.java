package org.example;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.example.graph.common.Edge;
import org.example.graph.dagsp.DagEdgeWeighted;
import org.example.graph.scc.KosarajuSCC;
import org.example.graph.topo.Condensation;
import org.example.graph.topo.KahnTopologicalSort;
import org.example.metrics.DAGSPMetrics;
import org.example.metrics.KahnMetrics;
import org.example.metrics.SCCMetrics;

import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Main {

    static class InputGraph {
        boolean directed;
        int n;
        List<InputEdge> edges;
        int source;
        String weight_model;
    }
    static class InputEdge { int u; int v; int w; }

    public static void main(String[] args) throws Exception {

        File folder = new File("data");
        File[] files = folder.listFiles((dir, name) -> name.endsWith(".json"));

        if (files == null || files.length == 0) {
            System.err.println("no input json found in /data folder");
            System.exit(1);
        }

        List<String[]> rows = new ArrayList<>();
        rows.add(new String[]{
                "dataset","Vertices","Edges",
                "scc_count","scc_time","scc_dfsVisits","scc_dfsEdges",
                "kahn_pushes","kahn_pops","kahn_time",
                "dag_relax","dag_time",
                "shortest_target","shortest_dist","longest_value"
        });

        for (File f : files) {

            Gson gson = new Gson();
            Type t = new TypeToken<InputGraph>(){}.getType();
            InputGraph in = gson.fromJson(new FileReader(f), t);

            List<Edge> edges = new ArrayList<>();
            for (InputEdge ie : in.edges) edges.add(new Edge(ie.u, ie.v, ie.w));

            SCCMetrics sm = new SCCMetrics();
            long t1 = System.nanoTime();
            KosarajuSCC.Result sres = KosarajuSCC.computeSCC(in.n, edges, sm);
            sm.timeNano = System.nanoTime() - t1;

            int sccCount = sres.components().size();

            Condensation.CondensedGraph cg = Condensation.build(sres.components(), sres.compOf(), edges);

            KahnMetrics km = new KahnMetrics();
            long t2 = System.nanoTime();
            KahnTopologicalSort.Result kres = KahnTopologicalSort.sort(cg.C, cg.edges, km);
            km.timeNano = System.nanoTime() - t2;

            DAGSPMetrics dagsp = new DAGSPMetrics();
            int sourceComp = sres.compOf()[in.source];

            long t3 = System.nanoTime();
            DagEdgeWeighted.ShortestResult sp = DagEdgeWeighted.shortest(cg.C, cg.adj, sourceComp, dagsp);

            int target = -1; double best = Double.NEGATIVE_INFINITY;
            for (int i = 0; i < sp.dist.length; i++)
                if (!Double.isInfinite(sp.dist[i]) && sp.dist[i] > best) { best = sp.dist[i]; target = i; }

            DagEdgeWeighted.LongestResult lp = DagEdgeWeighted.longest(cg.C, cg.adj, dagsp);
            dagsp.timeNano = System.nanoTime() - t3;

            rows.add(new String[]{
                    f.getName(),
                    String.valueOf(in.n),
                    String.valueOf(edges.size()),

                    String.valueOf(sccCount),
                    String.valueOf(sm.timeNano),
                    String.valueOf(sm.dfsVisits),
                    String.valueOf(sm.dfsEdges),

                    String.valueOf(km.pushes),
                    String.valueOf(km.pops),
                    String.valueOf(km.timeNano),

                    String.valueOf(dagsp.relaxations),
                    String.valueOf(dagsp.timeNano),

                    String.valueOf(target),
                    String.valueOf(best),
                    String.valueOf(lp.maxValue)
            });
        }

        try(PrintWriter pw = new PrintWriter("results.csv")) {
            for (String[] r : rows) pw.println(String.join(",", r));
        }

        System.out.println("results.csv generated.");
    }
}
