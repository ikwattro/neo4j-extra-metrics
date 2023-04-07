# Neo4j Extra Metrics extension

Extension providing additional metrics for database labels and relationship type counts.

## Installation

Clone this repository

```bash
git clone git@github.com:ikwattro/neo4j-extra-metrics
```

Build the package

```bash
mvn clean package -DskipTests
```

Install the produced jar file to the `plugins` directory of your Neo4j instance

Add the following setting to your `neo4j.conf`

```
server.metrics.enabled=true
server.metrics.namespaces.enabled=true
server.metrics.prometheus.enable=true
server.metrics.prometheus.endpoint=0.0.0.0:2004
server.metrics.filter=*
```

or in your docker compose neo4j service environment

```
- NEO4J_server_metrics_enabled=true
- NEO4J_server_metrics_namespaces_enabled=true
- NEO4J_server_metrics_prometheus_enabled=true
- NEO4J_server_metrics_prometheus_endpoint=0.0.0.0:2004
- NEO4J_server_metrics_filter=*
```

Note that this enables all metrics, refer to the [`server.metrics.filter`](https://neo4j.com/docs/operations-manual/current/reference/configuration-settings/#config_server.metrics.filter) setting , 
this plugin metrics are registered under the `extra` label.

Restart your neo4j server

Once restarted, you can inspect metrics on http://localhost:2004/metrics

```
# HELP neo4j_database_neo4j_extra_label_Person_total Generated from Dropwizard metric import (metric=neo4j.database.neo4j.extra.label.Person, type=com.neo4j.metrics.metric.MetricsCounter)
# TYPE neo4j_database_neo4j_extra_label_Person_total counter
neo4j_database_neo4j_extra_label_Person_total 399.0
# HELP neo4j_database_neo4j_extra_relationshipType_ACTED_IN_total Generated from Dropwizard metric import (metric=neo4j.database.neo4j.extra.relationshipType.ACTED_IN, type=com.neo4j.metrics.metric.MetricsCounter)
# TYPE neo4j_database_neo4j_extra_relationshipType_ACTED_IN_total counter
neo4j_database_neo4j_extra_relationshipType_ACTED_IN_total 516.0
# HELP neo4j_database_neo4j_extra_relationshipType_PRODUCED_total Generated from Dropwizard metric import (metric=neo4j.database.neo4j.extra.relationshipType.PRODUCED, type=com.neo4j.metrics.metric.MetricsCounter)
# TYPE neo4j_database_neo4j_extra_relationshipType_PRODUCED_total counter
neo4j_database_neo4j_extra_relationshipType_PRODUCED_total 45.0
# HELP neo4j_database_neo4j_extra_relationshipType_WROTE_total Generated from Dropwizard metric import (metric=neo4j.database.neo4j.extra.relationshipType.WROTE, type=com.neo4j.metrics.metric.MetricsCounter)
# TYPE neo4j_database_neo4j_extra_relationshipType_WROTE_total counter
neo4j_database_neo4j_extra_relationshipType_WROTE_total 30.0
# HELP neo4j_database_neo4j_extra_relationshipType_REVIEWED_total Generated from Dropwizard metric import (metric=neo4j.database.neo4j.extra.relationshipType.REVIEWED, type=com.neo4j.metrics.metric.MetricsCounter)
# TYPE neo4j_database_neo4j_extra_relationshipType_REVIEWED_total counter
neo4j_database_neo4j_extra_relationshipType_REVIEWED_total 27.0
# HELP neo4j_database_neo4j_extra_label_Movie_total Generated from Dropwizard metric import (metric=neo4j.database.neo4j.extra.label.Movie, type=com.neo4j.metrics.metric.MetricsCounter)
# TYPE neo4j_database_neo4j_extra_label_Movie_total counter
neo4j_database_neo4j_extra_label_Movie_total 114.0
# HELP neo4j_database_neo4j_extra_relationshipType_FOLLOWS_total Generated from Dropwizard metric import (metric=neo4j.database.neo4j.extra.relationshipType.FOLLOWS, type=com.neo4j.metrics.metric.MetricsCounter)
# TYPE neo4j_database_neo4j_extra_relationshipType_FOLLOWS_total counter
neo4j_database_neo4j_extra_relationshipType_FOLLOWS_total 9.0
# HELP neo4j_database_neo4j_extra_relationshipType_DIRECTED_total Generated from Dropwizard metric import (metric=neo4j.database.neo4j.extra.relationshipType.DIRECTED, type=com.neo4j.metrics.metric.MetricsCounter)
# TYPE neo4j_database_neo4j_extra_relationshipType_DIRECTED_total counter
neo4j_database_neo4j_extra_relationshipType_DIRECTED_total 132.0
```



