package dev.ikwattro.neo4j.metrics;

import com.neo4j.metrics.global.MetricsManager;
import org.neo4j.configuration.Config;
import org.neo4j.kernel.database.NamedDatabaseId;
import org.neo4j.kernel.extension.ExtensionFactory;
import org.neo4j.kernel.extension.ExtensionType;
import org.neo4j.kernel.extension.context.ExtensionContext;
import org.neo4j.kernel.internal.GraphDatabaseAPI;
import org.neo4j.kernel.lifecycle.Lifecycle;
import org.neo4j.scheduler.JobScheduler;

public class DatabaseExtraMetricsExtensionFactory extends ExtensionFactory<DatabaseExtraMetricsExtensionFactory.Dependencies> {

    public DatabaseExtraMetricsExtensionFactory() {
        super(ExtensionType.DATABASE, "databaseExtraMetrics");
    }

    @Override
    public Lifecycle newInstance(ExtensionContext extensionContext, Dependencies dependencies) {
        return new DatabaseExtraMetricsExtension(extensionContext, dependencies);
    }

    public interface Dependencies {
        MetricsManager metricsManager();

        GraphDatabaseAPI api();

        JobScheduler jobScheduler();

        NamedDatabaseId databaseId();
        Config config();
    }
}
