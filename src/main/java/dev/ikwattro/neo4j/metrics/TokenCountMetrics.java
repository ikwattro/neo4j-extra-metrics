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
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class TokenCountMetrics extends Metrics {

    private static final String SCHEDULER_RATE_SETTING_NAME = "dev_ikwattro_neo4j_extra_metrics_scheduler_rate";

    private static final long DEFAULT_SCHEDULER_RATE = 30000;

    private final String prefix;

    private final MetricsRegister registry;

    private final NamedDatabaseId databaseId;

    private final JobScheduler scheduler;

    private final GraphDatabaseAPI api;

    private volatile JobHandle<?> updateValuesHandle;

    Map<String, String> registeredMetrics = new HashMap<>();

    Map<String, LabelCount> labelCounts = new HashMap<>();
    Map<String, RelationshipTypeCount> relationshipTypeCounts = new HashMap<>();

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
        var schedulerRate = getJobScheduleRate();
        System.out.println("starting extra metrics job scheduler with rate %d ms".formatted(schedulerRate));
        JobMonitoringParams jobMonitoringParams = JobMonitoringParams.systemJob(databaseId.name(), "update of labels count metrics");
        this.updateValuesHandle = scheduler.scheduleRecurring(Group.DATABASE_INFO_SERVICE, jobMonitoringParams, this::update, schedulerRate, TimeUnit.MILLISECONDS);
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
        String query = """
                CALL db.stats.retrieve("GRAPH COUNTS") YIELD data
                RETURN
                [x IN data.nodes WHERE x.label IS NOT NULL] AS nodes,
                [x IN data.relationships WHERE x.startLabel IS NULL AND x.endLabel IS NULL AND x.relationshipType IS NOT NULL] AS relationships
                """;
        api.executeTransactionally(query, Map.of(), (result) -> {
            while (result.hasNext()) {
                Map<String, Object> record = result.next();
                List<Map<String, Object>> nodesResult = (List<Map<String, Object>>) record.get("nodes");
                List<Map<String, Object>> relationshipsResult = (List<Map<String, Object>>) record.get("relationships");
                nodesResult.forEach(n -> {
                    var labelCount = new LabelCount(n.get("label").toString(), Long.parseLong(n.get("count").toString()));
                    labelCounts.put(labelCount.label(), labelCount);
                });
                relationshipsResult.forEach(r -> {
                    var relationshipTypeCount = new RelationshipTypeCount(r.get("relationshipType").toString(), Long.parseLong(r.get("count").toString()));
                    relationshipTypeCounts.put(relationshipTypeCount.relationshipType(), relationshipTypeCount);
                });
            }
            updateMetrics();
            return null;
        });
    }

    private void updateMetrics() {
        labelCounts.values().forEach(labelCount -> {
            var hash = "label#%s#%s".formatted(databaseId.name(), labelCount.label());
            if (!registeredMetrics.containsKey(hash)) {
                var metricName = MetricRegistry.name(prefix, "extra", "label", labelCount.label());
                registry.register(metricName, () -> new MetricsCounter(() -> labelCounts.get(labelCount.label()).count()));
                registeredMetrics.put(hash, metricName);
            }
        });

        relationshipTypeCounts.values().forEach(relationshipTypeCount -> {
            var hash = "relationship#%s#%s".formatted(databaseId.name(), relationshipTypeCount.relationshipType());
            if (!registeredMetrics.containsKey(hash)) {
                var metricName = MetricRegistry.name(prefix, "extra", "relationshipType", relationshipTypeCount.relationshipType());
                registry.register(metricName, () -> new MetricsCounter(() -> relationshipTypeCounts.get(relationshipTypeCount.relationshipType()).count()));
                registeredMetrics.put(hash, metricName);
            }
        });
    }

    private long getJobScheduleRate() {
        var setting = System.getenv(SCHEDULER_RATE_SETTING_NAME);
        if (null != setting) {
            try {
                var rate = Long.parseLong(setting);
                if (rate < 5000) {
                    return DEFAULT_SCHEDULER_RATE;
                }
                return rate;
            } catch (NumberFormatException e) {
                return DEFAULT_SCHEDULER_RATE;
            }
        }

        return DEFAULT_SCHEDULER_RATE;
    }
}
