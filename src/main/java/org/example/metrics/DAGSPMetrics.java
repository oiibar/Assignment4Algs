package org.example.metrics;

public class DAGSPMetrics implements Metrics {
    public long timeNano;
    public long relaxations = 0;

    @Override public void reset() {
        timeNano = 0;
        relaxations = 0;
    }

    @Override public long getTimeNano() { return timeNano; }
}

