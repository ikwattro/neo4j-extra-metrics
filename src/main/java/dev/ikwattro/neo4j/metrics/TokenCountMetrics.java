package dev.ikwattro.neo4j.metrics;

import com.codahale.metrics.MetricRegistry;
import com.neo4j.metrics.metric.MetricsCounter;
import com.neo4j.metrics.metric.MetricsRegister;
import com.neo4j.metrics.source.MetricGroup;
import com.neo4j.metrics.source.Metrics;
import org.neo4j.kernel.database.NamedDatabaseId;
import org.neo4j.kernel.internal.GraphDatabaseAPI;
import org.neo4j.scheduler.Group;
import org.neo4j.scheduler.JobHandle;
import org.neo4j.scheduler.JobMonitoringParams;
import org.neo4j.scheduler.JobScheduler;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class TokenCountMetrics extends Metrics {

    private final String prefix;

    private final MetricsRegister registry;

    private final NamedDatabaseId databaseId;

    private final JobScheduler scheduler;

    private final GraphDatabaseAPI api;

    private volatile JobHandle<?> updateValuesHandle;

    Map<String, String> registeredMetrics = new HashMap<>();

    Map<String, Long> counts = new HashMap<>();

    private final Set<String> labels = new HashSet<>();

    public TokenCountMetrics(String prefix, MetricsRegister registry, NamedDatabaseId databaseId, JobScheduler scheduler, GraphDatabaseAPI api) {
        super(MetricGroup.GENERAL);
        this.prefix = prefix;
        this.registry = registry;
        this.databaseId = databaseId;
        this.scheduler = scheduler;
        this.api = api;
    }

    @Override
    public void start() {
        JobMonitoringParams jobMonitoringParams = JobMonitoringParams.systemJob(databaseId.name(), "update of labels count metrics");
        this.updateValuesHandle = scheduler.scheduleRecurring(Group.DATABASE_INFO_SERVICE, jobMonitoringParams, this::update, 30000, TimeUnit.MILLISECONDS);
    }

    @Override
    public void stop() {
        if (this.updateValuesHandle != null) {
            this.updateValuesHandle.cancel();
            this.updateValuesHandle = null;
        }

        registeredMetrics.values().forEach(this.registry::remove);
    }

    private void update() {
        api.executeTransactionally("CALL db.labels()", Map.of(), (result) -> {
            while (result.hasNext()) {
                var label = result.next().get("label").toString();
                labels.add(label);
            }
            return null;
        });

        labels.forEach(l -> {
            api.executeTransactionally("MATCH (n:`%s`) RETURN count(n) AS c".formatted(l), Map.of(), (result) -> {
                while (result.hasNext()) {
                    long count = (long) result.next().get("c");
                    updateLabelMetric(l, count);
                }
                return null;
            });
        });
    }

    private void updateLabelMetric(String label, long count) {
        counts.put(label, count);
        var hash = "%s#%s".formatted(databaseId.name(), label);
        if (!registeredMetrics.containsKey(hash)) {
            var metricName = MetricRegistry.name(prefix, "extra", "label", label);
            registeredMetrics.put(hash, metricName);
            registry.register(metricName, () -> new MetricsCounter(() -> counts.get(label)));
        }
    }
}
