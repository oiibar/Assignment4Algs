package org.example.metrics;

public interface Metrics {
    void reset();
    long getTimeNano();
}
