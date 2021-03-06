package org.rhq.metrics.impl.memory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.datastax.driver.core.Session;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

import org.rhq.metrics.core.MetricsService;
import org.rhq.metrics.core.RawNumericMetric;

import gnu.trove.map.TLongDoubleMap;
import gnu.trove.map.hash.TLongDoubleHashMap;

/**
 * A memory based storage backend for rapid prototyping
 * @author Heiko W. Rupp
 */
public class MemoryMetricsService implements MetricsService {

    private static final ListenableFuture<Void> VOID_FUTURE = Futures.immediateFuture(null);

    private Map<String,TLongDoubleMap> storage = new HashMap<>();

    @Override
    public void startUp(Session session) {
        throw new IllegalArgumentException("Not supported");
    }

    @Override
    public void startUp(Map<String, String> params) {

    }

    @Override
    public void shutdown() {
    }

    @Override
    public ListenableFuture<Void> addData(RawNumericMetric data) {
        addMetric(data);
        return VOID_FUTURE;
    }

    @Override
    public ListenableFuture<Map<RawNumericMetric, Throwable>> addData(Set<RawNumericMetric> data) {

        TLongDoubleMap map ;
        for (RawNumericMetric metric : data) {
            addMetric(metric);

            // TODO expire an old entry
        }
        Map<RawNumericMetric, Throwable> errors = Collections.emptyMap();
        return Futures.immediateFuture(errors);
    }

    private void addMetric(RawNumericMetric metric) {
        TLongDoubleMap map;
        String metricId = metric.getId();
        if (storage.containsKey(metricId)) {
            map = storage.get(metricId);
        } else {
            map = new TLongDoubleHashMap();
            storage.put(metricId,map);
        }
        map.put(metric.getTimestamp(), metric.getAvg()); // TODO getAvg() may be wrong in future
    }

    @Override
    public ListenableFuture<List<RawNumericMetric>> findData(String bucket, String id, long start, long end) {
        return findData(id, start, end);
    }

    @Override
    public ListenableFuture<List<RawNumericMetric>> findData(String id, long start, long end) {
        List<RawNumericMetric> metrics = new ArrayList<>();

        if (storage.containsKey(id)) {
            TLongDoubleMap map = storage.get(id);
            for (long ts : map.keys()) {
                if (ts>=start && ts<=end) {
                    RawNumericMetric metric = new RawNumericMetric(id,map.get(ts),ts);
                    metrics.add(metric);
                }

            }
        }
        return Futures.immediateFuture(metrics);
    }

    @Override
    public boolean idExists(String id) {
        return storage.containsKey(id);
    }

    @Override
    public List<String> listMetrics() {
        List<String> metrics = new ArrayList<>(storage.keySet().size());
        metrics.addAll(storage.keySet());

        return metrics;
    }
}
