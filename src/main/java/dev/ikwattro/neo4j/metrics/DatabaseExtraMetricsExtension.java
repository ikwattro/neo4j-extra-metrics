package dev.ikwattro.neo4j.metrics;

import com.neo4j.metrics.global.MetricsManager;
import org.neo4j.exceptions.UnsatisfiedDependencyException;
import org.neo4j.kernel.extension.context.ExtensionContext;
import org.neo4j.kernel.lifecycle.LifeSupport;
import org.neo4j.kernel.lifecycle.Lifecycle;

import java.util.Optional;

public class DatabaseExtraMetricsExtension implements Lifecycle {

    private final LifeSupport life = new LifeSupport();

    private final ExtensionContext extensionContext;

    private final DatabaseExtraMetricsExtensionFactory.Dependencies dependencies;

    public DatabaseExtraMetricsExtension(ExtensionContext extensionContext, DatabaseExtraMetricsExtensionFactory.Dependencies dependencies) {
        this.extensionContext = extensionContext;
        this.dependencies = dependencies;
    }

    @Override
    public void init() throws Exception {
        if (dependencies.databaseId().name().equals("system")) {
            return;
        }
        Optional<MetricsManager> optionalMetricsManager = getMetricsManager();
        optionalMetricsManager.ifPresent(metricsManager -> {
            if (metricsManager.isConfigured()) {
                var registry = metricsManager.getRegistry();
                this.life.add(
                        new TokenCountMetrics(
                                metricsName(dependencies.databaseId().name()),
                                registry,
                                dependencies.databaseId(),
                                dependencies.jobScheduler(),
                                dependencies.api()
                        )
                );
            }
        });
    }

    @Override
    public void start() throws Exception {
        this.life.start();
    }

    @Override
    public void stop() throws Exception {
        this.life.stop();
    }

    @Override
    public void shutdown() throws Exception {
        this.life.shutdown();
    }

    private Optional<MetricsManager> getMetricsManager() {
        try {
            return Optional.of(dependencies.metricsManager());
        } catch (UnsatisfiedDependencyException e) {
            return Optional.empty();
        }
    }

    private String metricsName(String name) {
        return "%s.database.%s".formatted("neo4j", name);
    }
}
