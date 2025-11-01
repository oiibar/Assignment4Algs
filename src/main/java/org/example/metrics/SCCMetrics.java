package org.example.metrics;

public class SCCMetrics implements Metrics {
    public long timeNano;
    public long dfsVisits = 0;
    public long dfsEdges  = 0;

    @Override public void reset() {
        timeNano = 0;
        dfsVisits = 0;
        dfsEdges = 0;
    }

    @Override public long getTimeNano() { return timeNano; }
}

