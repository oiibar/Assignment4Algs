package org.example.metrics;

public class KahnMetrics implements Metrics {
    public long timeNano;
    public long pushes = 0;
    public long pops = 0;

    @Override public void reset() {
        timeNano = 0;
        pushes = 0;
        pops = 0;
    }

    @Override public long getTimeNano() { return timeNano; }
}

